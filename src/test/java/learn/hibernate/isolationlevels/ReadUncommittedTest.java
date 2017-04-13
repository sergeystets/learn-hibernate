package learn.hibernate.isolationlevels;

import static org.assertj.core.api.Assertions.assertThat;
import static learn.hibernate.AssertJConditions.equalTo;

import java.util.List;

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

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/scripts/schema.sql")
@WebAppConfiguration
public class ReadUncommittedTest {

    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    @Qualifier("user-jdbc-repository")
    private UserRepository userRepository;

    @Test
    public void dirtyReadPermitted() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        User initial = new User(1L, "Sergii");
        User dirty = new User(1L, "Dmytro");

        // preconditions
        userRepository.save(initial);

        // ----------------------------------------------tx1------------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] update name of existing user
            String newName = dirty.getName();
            userRepository.updateUserName(newName, initial.getId());

            // -------------------------------tx2-----------------------------------
            tx2.execute(s2 -> {
                // [tx2] here we read non-committed changes
                User existing = userRepository.findOne(initial.getId());
                assertThat(existing).is(equalTo(dirty));
                return null;
            });
            // -------------------------------tx2-----------------------------------
            return null;
        });
        // ----------------------------------------------tx1------------------------------------------------------------
    }

    @Test
    public void nonRepeatableReadPermitted() {
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
            // [tx2] update existing user
            tx2.execute(s2 -> userRepository.updateUserName("Dmytro", initial.getId()));
            // -------------------------------tx2-----------------------------------

            // [tx1] select user again (but got different result than before)
            User after = userRepository.findOne(initial.getId());
            assertThat(after).isNot(equalTo(before));
            return null;
        });
        // ----------------------------------------------tx1------------------------------------------------------------
    }

    @Test
    public void phantomReadPermitted() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        // preconditions
        userRepository.save(new User("Dmytro"));

        // ----------------------------------------------tx1------------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] select users
            List<User> usersBefore = userRepository.findAllByName("Dmytro");
            assertThat(usersBefore).hasSize(1);

            // -------------------------------tx2-----------------------------------
            // [tx2] insert new user
            tx2.execute(s2 -> userRepository.save(new User("Dmytro")));
            // -------------------------------tx2-----------------------------------

            // [tx1] select user (new phantom user appeared)
            List<User> usersAfter = userRepository.findAllByName("Dmytro");
            assertThat(usersAfter).hasSize(2);
            return null;
            // -------------------------------------------tx1-----------------------------------------------------------
        });
    }

    @Test
    public void lostUpdatePermitted() {
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
            // [tx2] update existing user (updates will be lost)
            tx2.execute(s2 -> userRepository.updateUserName(tx2User.getName(), existing.getId()));
            // -------------------------------tx2-----------------------------------

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
        t.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        t.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }
}