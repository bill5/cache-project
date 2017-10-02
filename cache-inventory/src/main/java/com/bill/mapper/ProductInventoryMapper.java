package com.bill.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.bill.bean.ProductInventory;

/**
 * 库存数量 mapper
 * @author bill
 * 2017年8月14日22:21:42
 */
@Repository
public interface ProductInventoryMapper {
	/**
	 * 更新库存数量
	 * @param productInventory 商品库存
	 */
	void updateProductInventory(ProductInventory productInventory);
	
	/**
	 * 根据商品 id 获取商品库存信息
	 * @param productId
	 * @return
	 */
	ProductInventory getProductInventory(@Param("productId") Integer productId);
}
