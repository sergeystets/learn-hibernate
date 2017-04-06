package learn.hibernate.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import learn.hibernate.config.AppConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/scripts/schema.sql")
@WebAppConfiguration
public class IsolationLevelsTest {

    @Test
    public void readUncommitted() {

    }
}
