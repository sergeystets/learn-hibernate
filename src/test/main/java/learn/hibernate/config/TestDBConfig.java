package learn.hibernate.config;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.common.collect.ImmutableMap;

@Configuration
@ComponentScan(basePackages = "learn.hibernate.repository")
@EnableJpaRepositories({"learn.hibernate.repository"})
@EnableTransactionManagement
public class TestDBConfig {

    @Value("${db.user.name}")
    private String userName;
    @Value("${db.user.password}")
    private String password;
    @Value("")

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(org.hsqldb.jdbcDriver.class.getName());
        dataSource.setUsername(userName);
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:mariadb://localhost:3306/learn");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("learn.hibernate.entity");
        factoryBean.setJpaVendorAdapter(adapter);
        factoryBean.setJpaPropertyMap(jpaProperties());
        factoryBean.setDataSource(dataSource);
        factoryBean.afterPropertiesSet();

        return factoryBean;
    }

    private Map<String, ?> jpaProperties() {
        return ImmutableMap.of(
                "hibernate.show_sql", true,
                "hibernate.format_sql", true,
                "hibernate.generate_statistics", true);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

}
