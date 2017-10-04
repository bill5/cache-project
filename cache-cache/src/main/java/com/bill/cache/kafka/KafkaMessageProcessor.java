package com.bill.cache.kafka;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.bill.cache.bean.ProductInfo;
import com.bill.cache.bean.ShopInfo;
import com.bill.cache.service.CacheService;
import com.bill.cache.spring.SpringContext;
import com.bill.cache.zk.ZookeeperSession;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

/**
 * 
 * kafka message 处理线程
 * @author bill
 * @date 2017年8月26日 下午12:41:20
 */
public class KafkaMessageProcessor implements Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageProcessor.class);
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	
	@SuppressWarnings("rawtypes")
	private KafkaStream kafkaStream;
	private CacheService cacheService;
	
	@SuppressWarnings("rawtypes")
	public KafkaMessageProcessor(KafkaStream kafkaStream){
		this.kafkaStream = kafkaStream;
		this.cacheService = SpringContext.applicationContext.getBean(CacheService.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		//消息处理
		ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
		while(it.hasNext()){
			String message = new String(it.next().message());
			//首先将message 转换 json 对象
			JSONObject messageJsonObject = JSONObject.parseObject(message);
			//从消息中获取对应的服务标识，用于服务调用
			String serviceId = messageJsonObject.getString("serviceId");
			if(StringUtils.equals("productInfoService", serviceId)){
				processProductInfoUpdateMessage(messageJsonObject);
			}else if(StringUtils.equals("shopInfoService", serviceId)){
				processShopInfoUpdateMessage(messageJsonObject);
			} 
		}
	}
	 
	/**
	 * 处理商品信息变更消息
	 * @param jsonObject 消息对象
	 */
	private void processProductInfoUpdateMessage(JSONObject jsonObject){
		//提取商品id
		Long productId = jsonObject.getLong("productId");
		
		// 调用商品信息服务的接口,这里注意，为了方便笔者直接将数据写死了，你需要到mysql 源数据拉取即可
		// 直接用注释模拟：getProductInfo?productId=1，传递过去  httpClient
		// 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来
		String productInfoJSON = "{\"id\": 10, \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-10-3 12:30:00\"}";
		ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
		
		// 在数据写入redis 缓存之前，先获取 zookeeper 分布式锁，确保缓存重建冲突
		ZookeeperSession zkSession = ZookeeperSession.getIntance();
		zkSession.acquireDistributedLock(productId);
		
		// 获取到了锁
		// 先从redis 中获取当前最新数据
		ProductInfo redisLastProductInfo = cacheService.getProductInfoFromRedisCache(productId);
		if(null != redisLastProductInfo){
			//比较更新时间，redis中的时间 与现有数据比较.redis product info 比现有数据小则更新，否则不更新redis
			try {
				Date date = sdf.parse(productInfo.getModifiedTime());
				Date redisLastProductInfoDate = sdf.parse(redisLastProductInfo.getModifiedTime());
				if(date.before(redisLastProductInfoDate)){
					LOGGER.debug("无需更新  > 现有数据 date:[{}] - before redis 最新版本  date:[{}]", date, redisLastProductInfoDate);
					// 无需更新，直接返回
					return;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			LOGGER.debug("current product info date is after redis product info date , to update redis");
		}else{
			LOGGER.debug("product Info is null, to update redis");
		}
		
		/** 此休眠为了延迟，更好的查看打印效果 -----生产环境去掉即可----- start*/
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/** 此休眠为了延迟，更好的查看打印效果 -----生产环境去掉即可----- end*/
		
		// 更新本地 ehcache 缓存
		cacheService.saveProductInfo2LocalCache(productInfo);
		LOGGER.debug("获取刚保存到本地缓存的商品信息：[{}]", cacheService.getProductInfoFromLocalCache(productId));
		
		// 更新redis 缓存
		cacheService.saveProductInfo2RedisCache(productInfo);
		// 释放 zookeeper 分布式锁
		zkSession.releaseDistributedLock(productId);
	}

	/**
	 * 处理店铺信息变更信息
	 * @param jsonObject 消息对象
	 */
	private void processShopInfoUpdateMessage(JSONObject jsonObject){
		//提取店铺id
		Long shopId = jsonObject.getLong("shopId");
		//提取商品id
		Long productId = jsonObject.getLong("productId");
		
		// 调用商品信息服务的接口
		// 直接用注释模拟：getProductInfo?productId=1，传递过去 httpClient
		// 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来
		String shopInfoJSON = "{\"id\": 1, \"name\": \"小王的手机店\", \"level\": 5, \"rate\":0.99}";
		ShopInfo shopInfo = JSONObject.parseObject(shopInfoJSON, ShopInfo.class);
		// 本地ehcache 缓存
		cacheService.saveShopInfo2LocalCache(shopInfo);
		LOGGER.debug("获取刚保存到本地缓存的店铺信息：[{}]", cacheService.getShopInfoFromLocalCache(shopId));   
		//redis 缓存
		cacheService.saveShopInfo2RedisCache(shopInfo);
		
	}
	
}
