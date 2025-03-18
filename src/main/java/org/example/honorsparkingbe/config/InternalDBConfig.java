package org.example.honorsparkingbe.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "org.example.honorsparkingbe.repository.internal",
    entityManagerFactoryRef = "internalEntityManagerFactory",
    transactionManagerRef = "internalTransactionManager"
)
public class InternalDBConfig {

  @Primary
  @Bean(name = "internalDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.internal")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Primary
  @Bean(name = "internalEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("internalDataSource") DataSource dataSource) {

    return builder
        .dataSource(dataSource)
        .packages("org.example.honorsparkingbe.domain.entity") // 내부 DB 엔티티 패키지
        .persistenceUnit("internal")
        .build();
  }

  @Primary
  @Bean(name = "internalTransactionManager")
  public PlatformTransactionManager transactionManager(
      @Qualifier("internalEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
