package com.niit.testbackend.config;

//web.xml -- java based configuration
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer{
	
	private static final Logger log = LoggerFactory.getLogger(AppInitializer.class);
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		log.debug("Starting of the method getRootConfigClass in AppInitalizer.java");
		return new Class[] {AppConfig.class, WebSocketConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		log.debug("Starting of the method getServletConfigClasses in AppInitalizer.java");
		return  null; // new Class[] {AppConfig.class};                                         //  return null;
	}

	@Override
	protected String[] getServletMappings() {
		log.debug("Starting of the method getServletMappings in AppInitalizer.java");
		return new String[]{"/"};
	}
}
