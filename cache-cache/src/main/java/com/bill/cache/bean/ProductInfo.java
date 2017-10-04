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
	 * 商品规格
	 */
	private String specification;
	/**
	 * 商品售后服务
	 */
	private String service;
	/**
	 * 商品颜色
	 */
	private String color;
	/**
	 * 商品大小
	 */
	private String size;
	/**
	 * 店铺id
	 */
	private Long shopId;
	/**
	 * 最后更新时间
	 */
	private String modifiedTime;
}
