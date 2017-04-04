package learn.hibernate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import learn.hibernate.entity.User;


@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/db/scripts/db.sql")
@Sql("/db/scripts/schema.sql")
@Sql("/db/scripts/data.sql")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void findById_whenFirstLevelCacheIsWorking() {
        assertThat(userRepository).isNotNull();

        User user = userRepository.findOne(BigInteger.valueOf(1L));
        User cachedUser = userRepository.findOne(BigInteger.valueOf(1L));

        assertThat(user).isNotNull();
        assertThat(cachedUser).isNotNull();
        assertThat(user).isEqualTo(cachedUser);
    }
}
