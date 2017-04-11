package learn.hibernate.isolationlevels;

import static org.assertj.core.api.Assertions.assertThat;
import static learn.hibernate.AssertJConditions.equalTo;
import static learn.hibernate.SqlSupport.User.FIND_ALL_BY_NAME;
import static learn.hibernate.SqlSupport.User.FIND_ONE;
import static learn.hibernate.SqlSupport.User.INSERT_USER;
import static learn.hibernate.SqlSupport.User.INSERT_USER_WITH_ID;
import static learn.hibernate.SqlSupport.User.UPDATE_NAME;
import static learn.hibernate.SqlSupport.User.id;
import static learn.hibernate.SqlSupport.User.idAndName;
import static learn.hibernate.SqlSupport.User.mapper;
import static learn.hibernate.SqlSupport.User.name;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/scripts/schema.sql")
@WebAppConfiguration
public class ReadUncommittedTest {

    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Test(timeout = 5000)
    public void dirtyReadPermitted() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        User initial = new User(1L, "Sergii");
        User dirty = new User(1L, "Dmytro");

        // preconditions
        jdbc.update(INSERT_USER_WITH_ID, new BeanPropertySqlParameterSource(initial));

        // ----------------------------------------------tx1------------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] update name of existing user
            String newName = dirty.getName();
            jdbc.update(UPDATE_NAME, idAndName(initial.getId(), newName));

            // -------------------------------tx2-----------------------------------
            tx2.execute(s2 -> {
                // [tx2] here we read non-committed changes
                User existing = jdbc.queryForObject(FIND_ONE, id(initial.getId()), mapper);
                assertThat(existing).is(equalTo(dirty));
                return null;
            });
            // -------------------------------tx2-----------------------------------
            return null;
        });
        // ----------------------------------------------tx1------------------------------------------------------------
    }

    @Test(timeout = 5000)
    public void nonRepeatableReadPermitted() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        User initial = new User(1L, "Sergii");

        // preconditions
        jdbc.update(INSERT_USER_WITH_ID, new BeanPropertySqlParameterSource(initial));

        // ----------------------------------------------tx1------------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] select user
            User before = jdbc.queryForObject(FIND_ONE, id(initial.getId()), mapper);
            assertThat(before).is(equalTo(initial));

            // -------------------------------tx2-----------------------------------
            // [tx2] update existing user
            tx2.execute(s2 -> jdbc.update(UPDATE_NAME, idAndName(initial.getId(), "Dmytro")));
            // -------------------------------tx2-----------------------------------

            User after = jdbc.queryForObject(FIND_ONE, id(initial.getId()), mapper);
            assertThat(after).isNot(equalTo(before));
            return null;
        });
        // ----------------------------------------------tx1------------------------------------------------------------
    }

    @Test(timeout = 5000)
    public void phantomReadPermitted() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        // preconditions
        jdbc.update(INSERT_USER, name("Dmytro"));

        // ----------------------------------------------tx1------------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] select users
            List<User> usersBefore = jdbc.query(FIND_ALL_BY_NAME, name("Dmytro"), mapper);
            assertThat(usersBefore).hasSize(1);

            // -------------------------------tx2-----------------------------------
            // [tx2] insert new user
            tx2.execute(s2 -> jdbc.update(INSERT_USER, name("Dmytro")));
            // -------------------------------tx2-----------------------------------

            // [tx1] select user (new phantom user appeared)
            List<User> usersAfter = jdbc.query(FIND_ALL_BY_NAME, name("Dmytro"), mapper);
            assertThat(usersAfter).hasSize(2);
            return null;
            // -------------------------------------------tx1-----------------------------------------------------------
        });
    }

    @Test(timeout = 5000)
    public void lostUpdatePermitted() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        User initialUser = new User(1L, "Sergii");
        User tx1User = new User(1L, "Sergii-TX1");
        User tx2User = new User(1L, "Sergii-TX2");

        // preconditions
        jdbc.update(INSERT_USER_WITH_ID, new BeanPropertySqlParameterSource(initialUser));

        // -----------------------------------------------tx1-----------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] select existing user
            User existing = jdbc.queryForObject(FIND_ONE, id(initialUser.getId()), mapper);
            assertThat(existing).is(equalTo(initialUser));

            // -------------------------------tx2-----------------------------------
            // [tx2] update existing user (updates will be lost)
            tx2.execute(s2 -> jdbc.update(UPDATE_NAME, idAndName(existing.getId(), tx2User.getName())));
            // -------------------------------tx2-----------------------------------

            // [tx1] update existing user
            jdbc.update(UPDATE_NAME, idAndName(existing.getId(), tx1User.getName()));
            return null;
        });
        // -----------------------------------------------tx1-----------------------------------------------------------

        User actual = jdbc.queryForObject(FIND_ONE, id(initialUser.getId()), mapper);
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