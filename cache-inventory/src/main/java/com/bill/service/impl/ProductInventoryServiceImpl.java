package com.bill.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bill.bean.ProductInventory;
import com.bill.dao.RedisDao;
import com.bill.mapper.ProductInventoryMapper;
import com.bill.service.ProductInventoryService;

/**
 * 库存数量 service
 * @author bill
 * 2017年8月14日22:21:42
 */
@Service
public class ProductInventoryServiceImpl implements ProductInventoryService{
	
	@Resource
	private ProductInventoryMapper productInventoryMapper;
	@Resource
	private RedisDao redisDao;

	@Override
	public void updateProductInventory(ProductInventory productInventory) {
		productInventoryMapper.updateProductInventory(productInventory);
		System.out.println("===========日志===========: 已修改数据库中的库存，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt());
	}

	@Override
	public ProductInventory getProductInventory(Integer productId) {
		return productInventoryMapper.getProductInventory(productId);
	}

	@Override
	public void removeProductInventoryCache(ProductInventory productInventory) {
		String key = "product:inventory:" + productInventory.getProductId();
		redisDao.delete(key);
		System.out.println("===========日志===========: 已删除redis中的缓存，key=" + key); 
	}

	@Override
	public void setProductInventoryCache(ProductInventory productInventory) {
		String key = "product:inventory:" + productInventory.getProductId();
		redisDao.set(key, String.valueOf(productInventory.getInventoryCnt()));
		System.out.println("===========日志===========: 已更新商品库存的缓存，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt() + ", key=" + key);  
	}

	@Override
	public ProductInventory getProductInventoryCache(Integer productId) {
		Long inventoryCnt = 0L;
		String key = "product:inventory:" + productId;
		String result = redisDao.get(key);
		if(StringUtils.isNotBlank(result)){
			try {
				inventoryCnt = Long.valueOf(result);
				return new ProductInventory(productId, inventoryCnt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
