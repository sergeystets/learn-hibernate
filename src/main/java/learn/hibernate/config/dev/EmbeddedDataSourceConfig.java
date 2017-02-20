package learn.hibernate.config.dev;

import learn.hibernate.config.Profiles;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * @author Sergii Stets
 *         Date 20.02.2017
 */
@Profile(Profiles.DEV)
public class EmbeddedDataSourceConfig {

    @Bean
    public DataSource hsqlDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(org.hsqldb.jdbcDriver.class.getName());
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:hsqldb:mem:mydb");
        return dataSource;
    }

}
