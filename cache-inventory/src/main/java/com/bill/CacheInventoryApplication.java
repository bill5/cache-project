package com.bill;

import java.util.Set;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bill.listener.InitListener;
import com.google.common.collect.Sets;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;


/**
 * 库存服务启动类
 * @author bill
 * 2017年7月18日00:06:06
 */
@EnableTransactionManagement
@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("com.bill.mapper")
@ComponentScan
public class CacheInventoryApplication {
	
	/**
	 * 初始化jedisCluster
	 */
	@Bean
	public JedisCluster JedisClusterFactory(){
		Set<HostAndPort> jedisClusterNodes = Sets.newHashSet();
		jedisClusterNodes.add(new HostAndPort("192.168.0.16", 7001));
		jedisClusterNodes.add(new HostAndPort("192.168.0.17", 7003));
		jedisClusterNodes.add(new HostAndPort("192.168.0.18", 7005));
		JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
		return jedisCluster;
	}
	
	/**
	 *	注册监听器 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	public ServletListenerRegistrationBean servletListenerRegistrationBean(){
		return new ServletListenerRegistrationBean(new InitListener());
	}

	public static void main(String[] args) {
		SpringApplication.run(CacheInventoryApplication.class, args);
	}
}
