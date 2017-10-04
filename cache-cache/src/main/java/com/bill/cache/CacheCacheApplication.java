package com.bill.cache;

import java.util.Set;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bill.cache.listener.InitListener;
import com.google.common.collect.Sets;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * 缓存服务启动类
 * @author bill
 * 2017年8月19日14:02:01
 */
@EnableTransactionManagement
@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("com.bill.mapper")
@ComponentScan
public class CacheCacheApplication {
	
   /**
    * 1、编写业务逻辑
    *	（1）两种服务会发送来数据变更消息：商品信息服务，商品店铺信息服务，每个消息都包含服务名以及商品id
    *	（2）接收到消息之后，根据商品id到对应的服务拉取数据，这一步，我们采取简化的模拟方式，就是在代码里面写死，会获取到什么数据，不去实际再写其他的服务去调用了
    *	（3）商品信息：id，名称，价格，图片列表，商品规格，售后信息，颜色，尺寸
    *	（4）商品店铺信息：其他维度，用这个维度模拟出来缓存数据维度化拆分，id，店铺名称，店铺等级，店铺好评率
    *	（5）分别拉取到了数据之后，将数据组织成json串，然后分别存储到ehcache中，和redis缓存中
    * 2、测试业务逻辑
    *	（1）创建一个kafka topic
    *	（2）在命令行启动一个kafka producer
    *	（3）启动系统，消费者开始监听kafka topic
    *	C:\Windows\System32\drivers\etc\hosts
    *	################################### 配置本地hosts #################### 很重要 ######################
    *	# 缓存架构方案
    *		192.168.0.16 my-cache1
    *		192.168.0.17 my-cache2
    *		192.168.0.18 my-cache3
    *   ################################### 配置本地hosts #################### 很重要 ######################
    *	（4）在producer中，分别发送两条消息，一个是商品信息服务的消息，一个是商品店铺信息服务的消息
    *	（5）能否接收到两条消息，并模拟拉取到两条数据，同时将数据写入ehcache中，并写入redis缓存中
    *	（6）ehcache通过打印日志方式来观察，redis通过手工连接上去来查询
    *
    *	kafka 创建消息，发布消息
    *	cd /usr/local/kafka && bin/kafka-topics.sh --zookeeper my-cache1:2181,my-cache2:2181,my-cache3:2181 --topic cache-message --replication-factor 1 --partitions 1 --create
    *	cd /usr/local/kafka && bin/kafka-console-producer.sh --broker-list my-cache1:9092,my-cache2:9092,my-cache3:9092 --topic cache-message
    *	cd /usr/local/kafka && bin/kafka-console-consumer.sh --zookeeper my-cache1:2181,my-cache2:2181,my-cache3:2181 --topic cache-message --from-beginning
    *
    *	{"serviceId":"productInfoService","productId":1}
    *	{"serviceId":"shopInfoService","productId":1,"shopId":1}
    *
    *
    *   确认是否启动 nginx
    *   	启动：/usr/local/servers/nginx/sbin/nginx
    *   	关闭：/usr/local/servers/nginx/sbin/nginx -s stop
    *   	重启：/usr/local/servers/nginx/sbin/nginx -s reload
    *   
    *   确认是否启动zookeeper
    *   	启动：zkServer.sh start
    *   	关闭：zkServer.sh stop
    *   
    *   确认是否启动kafka
    *   	启动：cd /usr/local/kafka && nohup bin/kafka-server-start.sh config/server.properties &
    *   	关闭：cd /usr/local/kafka && nohup bin/kafka-server-stop.sh
    *   
    *   
    *	zookeeper分布式锁测试
    *	cd /usr/local/kafka && bin/kafka-console-producer.sh --broker-list my-cache1:9092,my-cache2:9092,my-cache3:9092 --topic cache-message
    *	{"serviceId":"productInfoService","productId":10}
    *	http://localhost:81/getProductInfo?productId=10
	*/
	
	
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
	 * 初始化 listener 监听器
	 * @return
	 */
	@Bean
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ServletListenerRegistrationBean servletListenerRegistrationBean(){
		return new ServletListenerRegistrationBean(new InitListener());
	}

	public static void main(String[] args) {
		SpringApplication.run(CacheCacheApplication.class, args);
	}
}
