package learn.hibernate.rollback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import learn.hibernate.config.AppConfiguration;
import learn.hibernate.entity.User;
import learn.hibernate.exceptions.CustomRuntimeException;
import learn.hibernate.services.IUserService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/scripts/schema.sql")
@WebAppConfiguration
public class TransactionsRollbackTests {

    @Autowired
    private IUserService userService;

    @Test
    public void transactionShouldRollback() {
        User user = new User("Sergii");
        try {
            userService.rollbackSave(user);
        } catch (CustomRuntimeException e) {
            // do nothing
        }
        User actual = userService.findUserById(user.getId()).orElse(null);

        assertThat(actual).isNull();
    }

    @Test
    public void transactionShouldNotRollBack() {
        User expected = new User("Sergii");
        try {
            userService.noRollbackSave(expected);
        } catch (CustomRuntimeException e) {
            // do nothing
        }
        User actual = userService.findUserById(expected.getId()).orElse(null);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }
}