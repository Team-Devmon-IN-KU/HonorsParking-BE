//package org.example.honorsparkingbe.config;
//
//import jakarta.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//    basePackages = "org.example.honorsparkingbe.repository.external",
//    entityManagerFactoryRef = "externalEntityManagerFactory",
//    transactionManagerRef = "externalTransactionManager"
//)
//public class ExternalDBconfig {
//
//  @Bean(name = "externalDataSource")
//  @ConfigurationProperties(prefix = "spring.datasource.external")
//  public DataSource dataSource() {
//    return DataSourceBuilder.create().build();
//  }
//
//  @Bean(name = "externalEntityManagerFactory")
//  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//      EntityManagerFactoryBuilder builder,
//      @Qualifier("externalDataSource") DataSource dataSource) {
//    return builder
//        .dataSource(dataSource)
//        .packages("org.example.honorsparkingbe.domain.external") // 외부 DB 엔티티 패키지
//        .persistenceUnit("external")
//        .build();
//  }
//
//  @Bean(name = "externalTransactionManager")
//  public PlatformTransactionManager transactionManager(
//      @Qualifier("externalEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
//    return new JpaTransactionManager(entityManagerFactory);
//  }
//
//}
