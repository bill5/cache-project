package com.bill.cache.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bill.cache.bean.ProductInfo;
import com.bill.cache.bean.ShopInfo;
import com.bill.cache.dao.RedisDao;
import com.bill.cache.service.CacheService;
/**
 * 缓存service 实现
 * @author bill
 * 2017年8月20日01:07:56
 */
@Service
public class CacheServiceImpl implements CacheService {
	
	public static final String CACHE_NAME = "local";
	
	@Resource
	private RedisDao redisDao;

	@Override
	@CachePut(value = CACHE_NAME,key = "'key_'+#productInfo.getId()")
	public ProductInfo saveLocalCache(ProductInfo productInfo) {
		return productInfo;
	}

	@Override 
	@Cacheable(value = CACHE_NAME,key = "'key_'+#id")
	public ProductInfo getLocalCache(Long id) {
		return null;
	}

	@Override
	@CachePut(value = CACHE_NAME,key = "'product_info_'+#productInfo.getId()")
	public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
		return productInfo;
	}

	@Override
	@Cacheable(value = CACHE_NAME,key = "'product_info_'+#productId")
	public ProductInfo getProductInfoFromLocalCache(Long productId) {
		return null;
	}

	@Override
	@CachePut(value = CACHE_NAME,key = "'shop_info_'+#shopInfo.getId()")
	public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
		return shopInfo;
	}

	@Override
	@Cacheable(value = CACHE_NAME,key = "'shop_info_'+#shopId")
	public ShopInfo getShopInfoFromLocalCache(Long shopId) {
		return null;
	}

	@Override
	public void saveProductInfo2RedisCache(ProductInfo productInfo) {
		String key = "product:info:" + productInfo.getId();
		redisDao.set(key, JSONObject.toJSONString(productInfo));
	}

	@Override
	public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
		String key = "shop:info:" + shopInfo.getId();
		redisDao.set(key, JSONObject.toJSONString(shopInfo));
	}

	@Override
	public ProductInfo getProductInfoFromRedisCache(Long productId) {
		String key = "product:info:" + productId;
		String json = redisDao.get(key);
		if(StringUtils.isNotBlank(json)){
			return JSONObject.parseObject(json, ProductInfo.class);
		}else{
			return null;
		}
	}

	@Override
	public ShopInfo getShopInfoFromRedisCache(Long shopId) {
		String key = "shop:info:" + shopId;
		String json = redisDao.get(key);
		if(StringUtils.isNotBlank(json)){
			return JSONObject.parseObject(json, ShopInfo.class);
		}else{
			return null;
		}
	}

}
