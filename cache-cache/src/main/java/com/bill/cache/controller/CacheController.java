package com.bill.cache.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bill.cache.bean.ProductInfo;
import com.bill.cache.bean.ShopInfo;
import com.bill.cache.service.CacheService;

/**
 * 缓存Controller
 * @author bill
 * 2017年8月20日01:21:39
 */
@RestController
public class CacheController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
			logger.info("=================从redis中获取缓存，商品信息: {}", productInfo); 
			if(productInfo == null){
				productInfo = cacheService.getProductInfoFromLocalCache(productId);
				logger.info("=================从ehcache中获取缓存，商品信息: {}", productInfo);
			}
			if(productInfo == null){
				//走 数据源重新拉数据并重建缓存
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
			logger.info("=================从redis中获取缓存，店铺信息: {}", shopInfo);
			if(shopInfo == null){
				shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
				logger.info("=================从ehcache中获取缓存，店铺信息: {}", shopInfo); 
			}
			if(shopInfo == null){
				//走 数据源重新拉数据并重建缓存
			}
		} catch (Exception e) {}
		return shopInfo;
	}
	
	

}
