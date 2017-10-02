package com.bill.cache.service;

import com.bill.cache.bean.ProductInfo;
import com.bill.cache.bean.ShopInfo;

/**
 * 缓存service 接口
 * @author bill
 * 2017年8月20日01:07:56
 */
public interface CacheService {
	/**
	 * 将商品信息保存本地缓存
	 * @param productInfo 商品
	 * @return 商品
	 */
	public ProductInfo saveLocalCache(ProductInfo productInfo);
	
	/**
	 * 从本地缓存获取商品信息
	 * @param id 商品id
	 * @return 商品
	 */
	public ProductInfo getLocalCache(Long id);
	
	/**
	 * 将商品信息保存到本地缓存  ehcache
	 * @param productInfo 商品
	 * @return 商品
	 */
	public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo);
	
	/**
	 * 从本地缓存  ehcache 获取商品信息
	 * @param productId 商品id
	 * @return 商品信息
	 */
	public ProductInfo getProductInfoFromLocalCache(Long productId);
	
	/**
	 * 将店铺信息保存到本地缓存  ehcache
	 * @param ShopInfo  店铺信息
	 * @return 店铺
	 */
	public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo);
	
	/**
	 * 从本地缓存  ehcache 获取店铺信息
	 * @param shopId 店铺id
	 * @return 店铺信息
	 */
	public ShopInfo getShopInfoFromLocalCache(Long shopId);
	
	/**
	 * 将商品信息保存到 redis 缓存中
	 * @param productInfo 商品信息
	 */
	public void saveProductInfo2RedisCache(ProductInfo productInfo);
	
	/**
	 * 将店铺信息保存到 redis 缓存中
	 * @param shopInfo
	 */
	public void saveShopInfo2RedisCache(ShopInfo shopInfo);
	
	/**
	 * 从redis 缓存中获取 商品信息
	 * @param productId 商品id
	 * @return 商品信息
	 */
	public ProductInfo getProductInfoFromRedisCache(Long productId);
	
	/**
	 * 从redis 缓存中获取 店铺信息
	 * @param productId 商品id
	 * @return 商品信息
	 */
	public ShopInfo getShopInfoFromRedisCache(Long shopId);
	
	
}
