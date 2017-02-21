package learn.hibernate.repository;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import learn.hibernate.config.TestDBConfig;

@ContextConfiguration(classes = TestDBConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void findAll() {
        assertThat(userRepository, is(notNullValue()));

        userRepository.findOne(BigInteger.valueOf(3L));
        userRepository.findOne(BigInteger.valueOf(3L));
        userRepository.findOne(BigInteger.valueOf(3L));
    }

}
