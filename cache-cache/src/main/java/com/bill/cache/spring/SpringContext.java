package com.bill.cache.spring;

import org.springframework.context.ApplicationContext;

/**
 * 
 * spring 上下文
 * @author bill
 * @date 2017年8月26日 下午12:44:25
 */
public class SpringContext {
	
	public static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringContext.applicationContext = applicationContext;
	}
}
