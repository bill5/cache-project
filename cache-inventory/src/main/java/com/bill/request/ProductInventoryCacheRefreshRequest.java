package com.bill.request;

import com.bill.bean.ProductInventory;
import com.bill.service.ProductInventoryService;

/**
 * 商品库存 redis 缓存刷新请求
 * @author bill
 * 2017年8月14日22:51:43
 */
public class ProductInventoryCacheRefreshRequest implements Request {
	
	/**
	 * 商品库存 Id
	 */
	private Integer productId;
	
	/**
	 * 商品库存 service
	 */
	private ProductInventoryService productInventoryService;
	
	/**
	 * 是否强制刷新到redis，默认false
	 */
	private Boolean forceRefresh;
	
	public ProductInventoryCacheRefreshRequest(Integer productId, ProductInventoryService productInventoryService, Boolean forceRefresh){
		this.productId = productId;
		this.productInventoryService = productInventoryService;
		this.forceRefresh = forceRefresh;
	}

	@Override
	public void process() {
		
		/** cache aside pattern
		 *  - 查询数据库
		 *  - 设置 redis 缓存
		 */
		
		// 查询数据库商品库存
		ProductInventory productInventory = productInventoryService.getProductInventory(productId);
		System.out.println("===========日志===========: 已查询到商品最新的库存数量，商品id=" + productId + ", 商品库存数量=" + productInventory.getInventoryCnt());  
		// 商品库存 刷新到 redis 缓存
		productInventoryService.setProductInventoryCache(productInventory);
	}

	@Override
	public Integer getProductId() {
		return productId;
	}

	@Override
	public boolean isForceRefresh() {
		return forceRefresh;
	}

}
