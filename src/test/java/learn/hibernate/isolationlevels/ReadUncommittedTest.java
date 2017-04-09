package learn.hibernate.isolationlevels;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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

    private static final RowMapper<User> mapper = BeanPropertyRowMapper.newInstance(User.class);

    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Test
    public void phantomReadPermitted() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        // preconditions
        SqlParameterSource insertParams = new BeanPropertySqlParameterSource(new User("Dmytro"));
        jdbc.update("INSERT INTO user (name) VALUES (:name)", insertParams);

        // 1. [tx1] start
        tx1.execute(s -> {
            // 2. [tx1] selecting user
            SqlParameterSource findParams = new MapSqlParameterSource("name", "Dmytro");
            List<User> usersBefore = jdbc.query("SELECT * FROM user WHERE user.name = :name", findParams, mapper);
            assertThat(usersBefore).hasSize(1);

            // 3. [tx2] start
            // 4. [tx2] doing inserts
            tx2.execute(s1 -> jdbc.update("INSERT INTO user (name) VALUES (:name)", insertParams));
            // 4. [tx2] finish

            // 2. [tx1] selecting user (phantom user appeared)
            List<User> usersAfter = jdbc.query("SELECT * FROM user WHERE user.name = :name", findParams, mapper);
            assertThat(usersAfter).hasSize(2);
            // 6. [tx1] finish
            return null;
        });
    }

    @Test
    public void lostUpdatePermitted() {
        TransactionTemplate tx1 = configuredTransactionTemplate();
        TransactionTemplate tx2 = configuredTransactionTemplate();

        // preconditions
        User initialUser = new User(1L, "Sergii");
        SqlParameterSource params = new BeanPropertySqlParameterSource(initialUser);
        jdbc.update("INSERT INTO user (id, name) VALUES (:id, :name)", params);

        BeanPropertySqlParameterSource findUserParams = new BeanPropertySqlParameterSource(initialUser);

        // 1. [tx1] start
        tx1.execute(s -> {
            User existingUser = jdbc.queryForObject("SELECT * FROM user WHERE user.id = :id", findUserParams, mapper);
            assertThat(existingUser).isEqualToComparingFieldByField(initialUser);

            // 2. [tx2] start
            tx2.execute(s2 -> {
                        SqlParameterSource updateParams1 = new MapSqlParameterSource().
                                addValue("id", 1).
                                addValue("name", "Sergii-TX2");

                        // 3. [tx2] update existing user (updates will be lost)
                        jdbc.update("UPDATE user SET name = :name WHERE id = :id", updateParams1);
                        return null;
                    }
            );
            // 4. [tx2] finish
            SqlParameterSource updateParams2 = new MapSqlParameterSource("name", "Sergii-TX1").addValue("id", 1);
            // 5. [tx1] update existing user
            jdbc.update("UPDATE user SET name = :name WHERE id = :id", updateParams2);
            return null;
        }); // 6. [tx1] finish

        User actual = jdbc.queryForObject("SELECT * FROM user WHERE user.id = :id", findUserParams, mapper);
        assertThat(actual).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(actual).hasFieldOrPropertyWithValue("name", "Sergii-TX1");
    }

    private TransactionTemplate configuredTransactionTemplate() {
        TransactionTemplate tx1 = new TransactionTemplate(txManager);
        setIsolationAndPropagation(tx1);
        return tx1;
    }

    private static void setIsolationAndPropagation(TransactionTemplate t) {
        t.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        t.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }
}