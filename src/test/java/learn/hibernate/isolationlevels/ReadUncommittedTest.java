package learn.hibernate.isolationlevels;

import static org.assertj.core.api.Assertions.assertThat;
import static learn.hibernate.SqlSupport.User.FIND_ALL_BY_NAME;
import static learn.hibernate.SqlSupport.User.FIND_ONE;
import static learn.hibernate.SqlSupport.User.INSERT_USER;
import static learn.hibernate.SqlSupport.User.INSERT_USER_WITH_ID;
import static learn.hibernate.SqlSupport.User.UPDATE_NAME;
import static learn.hibernate.SqlSupport.User.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
    public void phantomReadPermitted() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        // preconditions
        jdbc.update(INSERT_USER, new BeanPropertySqlParameterSource(new User("Dmytro")));

        // ----------------------------------------------tx1------------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] select users
            List<User> usersBefore = jdbc.query(FIND_ALL_BY_NAME, new MapSqlParameterSource("name", "Dmytro"), mapper);
            assertThat(usersBefore).hasSize(1);

            // -------------------------------tx2-----------------------------------
            // [tx2] insert new user
            tx2.execute(s2 -> jdbc.update(INSERT_USER, new BeanPropertySqlParameterSource(new User("Dmytro"))));
            // -------------------------------tx2-----------------------------------

            // [tx1] select user (new phantom user appeared)
            List<User> usersAfter = jdbc.query(FIND_ALL_BY_NAME, new MapSqlParameterSource("name", "Dmytro"), mapper);
            assertThat(usersAfter).hasSize(2);
            return null;
            // -------------------------------------------tx1-----------------------------------------------------------
        });
    }

    @Test(timeout = 5000)
    public void lostUpdatePermitted() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        User initialUser = new User(2L, "Sergii");
        User tx1User = new User(2L, "Sergii-TX1");
        User tx2User = new User(2L, "Sergii-TX2");

        // preconditions
        jdbc.update(INSERT_USER_WITH_ID, new BeanPropertySqlParameterSource(initialUser));

        // -----------------------------------------------tx1-----------------------------------------------------------
        tx1.execute(s1 -> {
            // [tx1] select existing user
            User existing = jdbc.queryForObject(FIND_ONE, new BeanPropertySqlParameterSource(initialUser), mapper);
            assertThat(existing).isEqualToComparingFieldByField(initialUser);

            // -------------------------------tx2-----------------------------------
            // [tx2] update existing user (updates will be lost)
            tx2.execute(s2 -> jdbc.update(UPDATE_NAME, new BeanPropertySqlParameterSource(tx2User)));
            // -------------------------------tx2-----------------------------------

            // [tx1] update existing user
            jdbc.update(UPDATE_NAME, new BeanPropertySqlParameterSource(tx1User));
            return null;
        });
        // -----------------------------------------------tx1-----------------------------------------------------------

        User actual = jdbc.queryForObject(FIND_ONE, new BeanPropertySqlParameterSource(initialUser), mapper);
        assertThat(actual).isEqualToComparingFieldByField(tx1User);
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