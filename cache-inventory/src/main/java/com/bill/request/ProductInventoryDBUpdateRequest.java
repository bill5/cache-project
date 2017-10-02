package com.bill.request;

import com.bill.bean.ProductInventory;
import com.bill.service.ProductInventoryService;

/**
 * 商品库存 修改请求
 * @author bill
 * 2017年8月14日22:51:43
 */
public class ProductInventoryDBUpdateRequest implements Request {
	
	/**
	 * 商品库存
	 */
	private ProductInventory productInventory;
	
	/**
	 * 商品库存 service
	 */
	private ProductInventoryService productInventoryService;
	
	public ProductInventoryDBUpdateRequest(ProductInventory productInventory, ProductInventoryService productInventoryService){
		this.productInventory = productInventory;
		this.productInventoryService = productInventoryService;
	}

	@Override
	public void process() {
		
		/** cache aside pattern
		 *  - 删除缓存
		 *  - 更新数据库
		 */
		System.out.println("===========日志===========: 数据库更新请求开始执行，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt());  
		// 删除  redis 中的缓存
		productInventoryService.removeProductInventoryCache(productInventory);
		// 为了模拟演示先删除了redis中的缓存，然后还没更新数据库的时候，读请求过来了，这里可以人工sleep一下
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 修改数据库中的库存
		productInventoryService.updateProductInventory(productInventory);
	}

	@Override
	public Integer getProductId() {
		return this.productInventory.getProductId();
	}

	@Override
	public boolean isForceRefresh() {
		return true;
	}

}
