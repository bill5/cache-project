package com.bill.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * ehcache 缓存配置类
 * @author bill
 * 2017年8月19日15:36:44
 */
@Configuration
@EnableCaching
public class CacheConfiguration {
	
	/**
	 * 初始化ehcache factory
	 * @return
	 */
	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean(){
		EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
		//是否 ehcache 共享
		ehCacheManagerFactoryBean.setShared(true);
		return ehCacheManagerFactoryBean;
	}
	
	/**
	 * 初始化echcache 管理器
	 * @param bean
	 * @return
	 */
	@Bean
	public EhCacheCacheManager ehCacheManager(EhCacheManagerFactoryBean bean){
		return new EhCacheCacheManager(bean.getObject());
	}

}
