package io.idstudios.flink.kafka.weborder;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({"classpath:weborder-processor.properties"})
@EnableJpaRepositories(basePackages = "io.idstudios.flink.kafka.weborder.dao.shipdb", 
    entityManagerFactoryRef = "shipdbEntityManager", 
    transactionManagerRef = "shipdbTransactionManager")
@Profile("!tc")
public class PersistenceShipDBAutoConfiguration {
    @Autowired
    private Environment env;

    public PersistenceShipDBAutoConfiguration() {
        super();
    }

    //
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean shipdbEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(shipdbDataSource());
        em.setPackagesToScan("io.idstudios.flink.kafka.weborder.model.shipdb");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("spring.jpa.properties.hibernate.dialect", env.getProperty("spring.shipdb.datasource.hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix="spring.shipdb.datasource")
    public DataSource shipdbDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager shipdbTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(shipdbEntityManager().getObject());
        return transactionManager;
    }

}