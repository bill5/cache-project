package com.bill.cache.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.bill.cache.bean.ProductInfo;
import com.bill.cache.bean.ShopInfo;
import com.bill.cache.rebuild.RebuildCacheQueue;
import com.bill.cache.service.CacheService;

/**
 * 缓存Controller
 * @author bill
 * 2017年8月20日01:21:39
 */
@RestController
public class CacheController {
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private CacheService cacheService;
	
	
	@GetMapping("/testPutCache")
	public String testPutCache(ProductInfo productInfo){
		cacheService.saveLocalCache(productInfo);
		return "success";
	}
	
	@GetMapping("/testGetCache")
	public ProductInfo testGetCache(Long id){
		return cacheService.getLocalCache(id);
	}
	
	/**
	 * 获取商品信息
	 * @param productId 商品id
	 * @return 商品信息
	 */
	@GetMapping("/getProductInfo")
	public ProductInfo getProductInfo(Long productId){
		ProductInfo productInfo = null;
		try {
			productInfo = cacheService.getProductInfoFromRedisCache(productId);
			LOGGER.debug("从redis中获取缓存，商品信息: {}", productInfo); 
			if(productInfo == null){
				productInfo = cacheService.getProductInfoFromLocalCache(productId);
				LOGGER.debug("从ehcache中获取缓存，商品信息: {}", productInfo);
			}
			if(productInfo == null){
				//走 数据源重新拉数据并重建缓存,注意这里笔者就直接写死数据了
				String productInfoJSON = "{\"id\": 10, \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-10-3 12:30:01\"}";
				productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
				// 将数据推送到一个内存队列中消费（重建缓存的内存队列）
				RebuildCacheQueue.getInstance().putProductInfo(productInfo);
			}
		} catch (Exception e) {}
		return productInfo;
	}
	
	/**
	 * 获取店铺信息
	 * @param shopId 店铺id
	 * @return 店铺信息
	 */
	@GetMapping("/getShopInfo")
	public ShopInfo getShopInfo(Long shopId){
		ShopInfo shopInfo = null;
		try {
			shopInfo = cacheService.getShopInfoFromRedisCache(shopId);
			LOGGER.info("从redis中获取缓存，店铺信息: {}", shopInfo);
			if(shopInfo == null){
				shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
				LOGGER.info("从ehcache中获取缓存，店铺信息: {}", shopInfo); 
			}
			if(shopInfo == null){
				//走 数据源重新拉数据并重建缓存
			}
		} catch (Exception e) {}
		return shopInfo;
	}
	
	

}
