package com.bill.cache.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 店铺实体
 * @author bill
 * @date 2017年8月26日 下午1:16:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopInfo {
	
	private Long id;
	/**
	 * 店铺名称
	 */
	private String name;
	/**
	 * 等级
	 */
	private Integer level;
	/**
	 * 评分
	 */
	private Double rate;
}
