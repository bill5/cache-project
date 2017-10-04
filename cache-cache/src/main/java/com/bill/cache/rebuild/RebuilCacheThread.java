package com.bill.cache.rebuild;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bill.cache.bean.ProductInfo;
import com.bill.cache.kafka.KafkaMessageProcessor;
import com.bill.cache.service.CacheService;
import com.bill.cache.spring.SpringContext;
import com.bill.cache.zk.ZookeeperSession;

/**
 * 
 * 重建缓存队列消费线程
 * @author bill
 * @date 2017年10月3日 上午11:52:40
 */
public class RebuilCacheThread implements Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageProcessor.class);
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

	@Override
	public void run() {
		// 获取重建缓存队列实例
		RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance(); 
		// 获取zookeeperSession 实例
		ZookeeperSession zkSession = ZookeeperSession.getIntance();
		CacheService cacheService = SpringContext.applicationContext.getBean(CacheService.class);
		while(true){
			ProductInfo productInfo = rebuildCacheQueue.takeProductInfo();
			// 获取zookeeper分布式锁
			zkSession.acquireDistributedLock(productInfo.getId());
			// 获取到了锁
			// 先从redis 中获取当前最新数据
			ProductInfo redisLastProductInfo = cacheService.getProductInfoFromRedisCache(productInfo.getId());
			if(null != redisLastProductInfo){
				//比较更新时间，redis中的时间 与现有数据比较.redis product info 比现有数据小则更新，否则不更新redis
				try {
					Date date = sdf.parse(productInfo.getModifiedTime());
					Date redisLastProductInfoDate = sdf.parse(redisLastProductInfo.getModifiedTime());
					if(date.before(redisLastProductInfoDate)){
						LOGGER.debug("无需更新  > 现有数据 date:[{}] - before redis 最新版本  date:[{}]", date, redisLastProductInfoDate);
						// 无需更新，直接返回
						continue;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				LOGGER.debug("current product info date is after redis product info date , to update redis");
			}else{
				LOGGER.debug("product Info is null, to update redis");
			}
			// 更新本地 ehcache 缓存
			cacheService.saveProductInfo2LocalCache(productInfo);
			// redis 缓存
			cacheService.saveProductInfo2RedisCache(productInfo);
			// 释放 zookeeper 分布式锁
			zkSession.releaseDistributedLock(productInfo.getId());
		}
	}

}
