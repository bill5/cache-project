package com.bill;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bill.cache.dao.RedisDao;
import com.bill.cache.service.CacheService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheCacheApplicationTests {

	
	@Resource
	private RedisDao redisDao;
	
	@Resource
	private CacheService cacheService;
	
	@Test
	public void findCacheUserTest(){
		redisDao.set("user_cache_lisi","{\"name\": \"lisi\",\"age\":28}");
		System.out.println(redisDao.get("user_cache_lisi"));
	}
	
	@Test
	public void findCacheProductInfoTest(){
		/*ProductInfo productInfo = new ProductInfo(1L, "巧克力", 3.35);
		cacheService.saveLocalCache(productInfo);
		
		System.out.println(cacheService.getLocalCache(1L).toString());
		System.out.println(cacheService.getLocalCache(1L).toString());
		System.out.println(cacheService.getLocalCache(1L).toString());
		System.out.println(cacheService.getLocalCache(1L).toString());*/
	}
	
}
