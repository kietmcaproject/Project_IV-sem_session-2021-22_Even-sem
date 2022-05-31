package com.kuliza.workbench;

import static com.kuliza.lending.common.utils.Constants.DATABASECHANGELOG;
import static com.kuliza.lending.common.utils.Constants.DATABASECHANGELOGLOCK;
import static com.kuliza.lending.common.utils.Constants.MASTER_CHANGELOG_PATH_PREFIX;
import static com.kuliza.workbench.util.WorkbenchConstants.JASYPT_ENCRYPTOR_PASSWORD;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kuliza"})
@EnableConfigurationProperties(LiquibaseProperties.class)
@PropertySources({@PropertySource("classpath:lend-in-modules.properties")})
@EnableEncryptableProperties
public class WorkbenchApplication extends SpringBootServletInitializer {

  @Autowired @Lazy private DataSource dataSource;

  @Autowired private LiquibaseProperties properties;

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(WorkbenchApplication.class);
  }

  public static void main(String[] args) {
    System.setProperty("jasypt.encryptor.password", JASYPT_ENCRYPTOR_PASSWORD);
    SpringApplication.run(WorkbenchApplication.class);
  }

  @Bean
  @DependsOn("liquibaseJourney")
  public SpringLiquibase liquibaseWorkbench() {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setShouldRun(this.properties.isEnabled());
    liquibase.setChangeLog(MASTER_CHANGELOG_PATH_PREFIX + "liquibase-workbench-changeLog.xml");
    liquibase.setDatabaseChangeLogTable("IMPL_ENGINE_" + DATABASECHANGELOG);
    liquibase.setDatabaseChangeLogLockTable("IMPL_ENGINE_" + DATABASECHANGELOGLOCK);
    liquibase.setDataSource(dataSource);
    return liquibase;
  }
}
