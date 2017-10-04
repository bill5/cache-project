package com.bill.cache.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bill.cache.kafka.KafkaConsumer;
import com.bill.cache.rebuild.RebuilCacheThread;
import com.bill.cache.spring.SpringContext;
import com.bill.cache.zk.ZookeeperSession;
/**
 * 
 * 初始化监听类
 * @author bill
 * @date 2017年8月25日 下午3:49:22
 */
public class InitListener implements ServletContextListener{
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		/**
		 * spring 上下文
		 */
		ServletContext servletContext = sce.getServletContext();
		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		SpringContext.setApplicationContext(applicationContext);
		
		/**
		 * 启动 kafka 消费者线程
		 */
		new Thread(new KafkaConsumer("cache-message")).start();
		/**
		 * 启动重建缓存消费队列线程
		 */
		new Thread(new RebuilCacheThread()).start();
		
		/**
		 *  初始化zookeeper 分布式锁
		 */
		ZookeeperSession.init();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
