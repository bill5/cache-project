package com.bill.cache.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 商品实体
 * @author bill
 * 2017年8月20日01:06:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {
	private Long id;
	/**
	 * 商品名称
	 */
	private String name;
	/**
	 * 商品价格
	 */
	private Double price;
	/**
	 * 商品图片
	 */
	private String pictureList;
	/**
	 * 。。。
	 */
	private String specification;
	private String service;
	private String color;
	private String size;
	private Long shopId;
}
