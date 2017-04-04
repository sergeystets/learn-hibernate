package learn.hibernate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import learn.hibernate.config.AppConfiguration;
import learn.hibernate.entity.User;
import learn.hibernate.exceptions.CustomRuntimeException;
import learn.hibernate.services.IUserService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@WebAppConfiguration
public class UserServiceTransactionsTest {

    @Autowired
    private IUserService userService;

    @Test
    public void saveShouldRollBackWhenLogThrewRuntimeException() {
        User expected = new User("Sergii");
        try {
            userService.saveAndLog1(expected);
        } catch (CustomRuntimeException e) {
            // do nothing
        }
        User actual = userService.findByName(expected.getName()).orElse(null);

        assertThat(actual).isNull();
    }

    @Test
    @Transactional
    @Rollback
    public void saveShouldNotRollBackWhenLogThrewRuntimeException() {
        User expected = new User("Sergii");
        try {
            userService.saveAndLog2(expected);
        } catch (CustomRuntimeException e) {
            // do nothing
        }
        User actual = userService.findByName(expected.getName()).orElse(null);

        assertThat(actual).isNotNull();
    }
}
