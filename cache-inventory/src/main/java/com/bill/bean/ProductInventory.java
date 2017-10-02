package com.bill.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 库存实例 bean
 * @author bill
 * 2017年8月14日22:29:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventory {
	/**
	 * 商品 id
	 */
	private Integer productId;
	
	/**
	 * 库存数量
	 */
	private Long inventoryCnt;
}
