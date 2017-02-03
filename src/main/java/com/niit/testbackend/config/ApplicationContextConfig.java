package com.niit.testbackend.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.niit.testbackend.model.*;

@Configuration
@ComponentScan("com.niit")
@EnableTransactionManagement
public class ApplicationContextConfig {

	private static final Logger log = LoggerFactory.getLogger(ApplicationContextConfig.class);
	
	@Bean(name="datasource")
	public DataSource getOracleDataSource(){
		log.debug("Starting of getOracleDataSource in applicationContextConfig.java");
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		log.debug("Setting DriverClassName = 'oracle.jdbc.driver.OracleDriver' in applicationContextConfig.java");
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		log.debug("Setting URL = 'jdbc:oracle:thin:@localhost:1521:XE' in applicationContextConfig.java");
		dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:XE");
		log.debug("Setting Username = 'DB_SYZITO' and password ='root' in applicationContextConfig.java");
		dataSource.setUsername("DB_SYZITO");
		dataSource.setPassword("root");
		
		Properties connectionProperties = new Properties();
		log.debug("Setting connectionProperty of hibernate.dialect  = 'org.hibernate.dialect.Oracle10gDialect' in applicationContextConfig.java");
		connectionProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
		log.debug("Setting connectionProperty of hibernate.hbm2ddl.auto  = 'create' in applicationContextConfig.java");
		connectionProperties.setProperty("hibernate.hbm2ddl.auto", "create");
		log.debug("Setting above two properties to datasource of DriverManagerDataSource type");
		dataSource.setConnectionProperties(connectionProperties);
		log.debug("returning datasource with all the properties , connections, url, username , passsword,DriverClassName");
		return dataSource;
	}
	
	@Autowired
	@Bean(name="sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource){
		log.debug("Starting of getSessionFactory in applicationContextConfig.java");
		log.debug("Creating sessionBuilder with LocalSessionFactoryBuilder type");
		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
		log.debug("Created sessionBuilder with LocalSessionFactoryBuilder type");
		//sessionBuilder.addProperties(getHibernateProperties());
		sessionBuilder.addAnnotatedClass(User.class);
		sessionBuilder.addAnnotatedClass(Blog.class);
		sessionBuilder.addAnnotatedClass(BlogComment.class);
		sessionBuilder.addAnnotatedClass(Job.class);
		sessionBuilder.addAnnotatedClass(JobApplication.class);
		sessionBuilder.addAnnotatedClass(Friend.class);
		sessionBuilder.addAnnotatedClass(Forum.class);
		sessionBuilder.addAnnotatedClass(ForumComment.class);
		return sessionBuilder.buildSessionFactory();
		
	}
	
	
	
	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory){
		log.debug("Starting of getTransactionManager in applicationContextConfig.java");
		HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
		
		return transactionManager;
	}
}
