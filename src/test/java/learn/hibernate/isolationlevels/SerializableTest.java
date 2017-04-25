package learn.hibernate.isolationlevels;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static learn.hibernate.AssertJConditions.equalTo;
import static learn.hibernate.AssertionsUtils.WaitingAssert.assertThatInvocationOf;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import learn.hibernate.config.AppConfiguration;
import learn.hibernate.entity.User;
import learn.hibernate.repository.UserRepository;

@SuppressWarnings("Duplicates")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/scripts/schema.sql")
@WebAppConfiguration
public class SerializableTest {

    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    @Qualifier("user-jdbc-repository")
    private UserRepository userRepository;

    @Test
    public void dirtyRead_no() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        User initial = new User(1L, "Sergii");
        User dirty = new User(1L, "Ivan");

        // preconditions
        userRepository.save(initial);

        // ----------------------------------------------tx1------------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] update name of existing user
            String newName = dirty.getName();
            userRepository.updateUserName(newName, initial.getId());

            // -------------------------------tx2-----------------------------------
            // [tx2] here we try to find user to check if it is dirty, but fail as serializable holds an exclusive lock
            Callable<User> find = () -> tx2.execute(s2 -> userRepository.findOne(initial.getId()));
            assertThatInvocationOf(find).willHangFor(4, SECONDS);
            // -------------------------------tx2-----------------------------------
            return null;
        });
        // ----------------------------------------------tx1------------------------------------------------------------
    }


    @Test
    public void nonRepeatableRead_no() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        User initial = new User(1L, "Sergii");

        // preconditions
        userRepository.save(initial);

        // ----------------------------------------------tx1------------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] select user
            User before = userRepository.findOne(initial.getId());
            assertThat(before).is(equalTo(initial));

            // -------------------------------tx2-----------------------------------
            // [tx2] here we try to update user to cause non-repeatable read, but fail
            // as serializable holds an exclusive lock
            Callable<Integer> update = () -> tx2.execute(s2 -> userRepository.updateUserName("Ivan", initial.getId()));
            assertThatInvocationOf(update).willHangFor(4, SECONDS);
            // -------------------------------tx2-----------------------------------

            // [tx1] select user again (should get the same result as before)
            User after = userRepository.findOne(initial.getId());
            assertThat(after).is(equalTo(before));

            return null;
        });
        // ----------------------------------------------tx1------------------------------------------------------------
    }

    @Test
    public void phantomRead_no() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        // preconditions
        userRepository.save(new User("Ivan"));

        // ----------------------------------------------tx1------------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] select users
            List<User> usersBefore = userRepository.findAllByName("Ivan");
            assertThat(usersBefore).hasSize(1);

            // -------------------------------tx2-----------------------------------
            // [tx2] here we try insert new phantom user but fail as serializable holds an exclusive lock
            Callable<User> insert = () -> tx2.execute(s2 -> userRepository.save(new User("Ivan")));
            // -------------------------------tx2-----------------------------------
            assertThatInvocationOf(insert).willHangFor(4, SECONDS);

            // [tx1] select user (insert made by tx2 should be invisible)
            List<User> usersAfter = userRepository.findAllByName("Ivan");
            assertThat(usersAfter).hasSize(1);
            return null;
            // -------------------------------------------tx1-----------------------------------------------------------
        });
    }

    @Test
    public void lostUpdate_no() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        User initial = new User(1L, "Sergii");
        User tx1User = new User(1L, "Sergii-TX1");
        User tx2User = new User(1L, "Sergii-TX2");

        // preconditions
        userRepository.save(initial);

        // -----------------------------------------------tx1-----------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] select existing user
            User existing = userRepository.findOne(initial.getId());
            assertThat(existing).is(equalTo(initial));

            // -------------------------------tx2-----------------------------------
            // [tx2] here we try update user to simulate lost update, but fail as serializable holds an exclusive lock
            Callable<Integer> update = () ->
                    tx2.execute(s2 -> userRepository.updateUserName(tx2User.getName(), existing.getId()));
            // -------------------------------tx2-----------------------------------
            assertThatInvocationOf(update).willHangFor(4, SECONDS);

            // [tx1] update existing user
            userRepository.updateUserName(tx1User.getName(), existing.getId());
            return null;
        });
        // -----------------------------------------------tx1-----------------------------------------------------------

        User actual = userRepository.findOne(initial.getId());
        assertThat(actual).is(equalTo(tx1User));
    }

    private TransactionTemplate configuredTransactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(txManager);
        setIsolationAndPropagation(transactionTemplate);
        return transactionTemplate;
    }

    private static void setIsolationAndPropagation(TransactionTemplate t) {
        t.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        t.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }
}