package com.bill.service;

import org.apache.ibatis.annotations.Param;

import com.bill.bean.ProductInventory;

/**
 * 库存数量 service
 * @author bill
 * 2017年8月14日22:21:42
 */
public interface ProductInventoryService {
	/**
	 * 更新库存数量
	 * @param productInventory 商品库存
	 */
	void updateProductInventory(ProductInventory productInventory);
	
	/**
	 * 根据商品 id 获取商品库存信息
	 * @param productId 商品id
	 * @return 商品库存
	 */
	ProductInventory getProductInventory(@Param("productId") Integer productId);
	
	/**
	 * 删除redis 中商品的库存缓存
	 * @param productInventory  商品库存
	 */
	void removeProductInventoryCache(ProductInventory productInventory);
	
	/**
	 * 设置商品库存的redis 缓存
	 * @param productInventory 商品库存
	 */
	void setProductInventoryCache(ProductInventory productInventory);
	
	/**
	 * 获取商品库存缓存
	 * @param productId
	 * @return
	 */
	ProductInventory getProductInventoryCache(Integer productId);
}
