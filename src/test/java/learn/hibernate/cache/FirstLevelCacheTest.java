package learn.hibernate.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import learn.hibernate.repository.UserRepository;
import learn.hibernate.entity.User;


@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql({"/db/scripts/schema.sql", "/db/scripts/data.sql"})
public class FirstLevelCacheTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void findById_whenFirstLevelCacheIsWorking() {
        assertThat(userRepository).isNotNull();

        User user = userRepository.findOne(1L);
        User cachedUser = userRepository.findOne(1L);

        assertThat(user).isNotNull();
        assertThat(cachedUser).isNotNull();
        assertThat(user).isEqualTo(cachedUser);
    }
}
