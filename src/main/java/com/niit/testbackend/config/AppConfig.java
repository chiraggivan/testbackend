package com.niit.testbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages="com.niit")
public class AppConfig {
	private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
	
	@Bean
	public ViewResolver viewResolver(){
		log.debug("Starting of the method viewResolver");
		log.debug("Same as despatcher-servlet.xml");
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");
		log.debug("Ending of the method viewResolver");
		return viewResolver;
	}
	
}