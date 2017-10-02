package com.bill.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bill.bean.ProductInventory;
import com.bill.request.ProductInventoryCacheRefreshRequest;
import com.bill.request.ProductInventoryDBUpdateRequest;
import com.bill.request.Request;
import com.bill.response.Response;
import com.bill.service.ProductInventoryService;
import com.bill.service.RequestAsyncProcessService;

/**
 * 商品库存 controller
 * @author bill
 * 2017年8月15日00:16:56
 */
@RestController
public class ProductInventoryController {
	
	@Resource
	private RequestAsyncProcessService requestAsyncProcessService;
	@Resource
	private ProductInventoryService productInventoryService;
	
	/**
	 * 更新商品库存
	 * @param productInventory 商品库存
	 * @return 更新状态
	 */
	@GetMapping("/updateProductInventory")
	public Response updateProductInventory(ProductInventory productInventory){
		try {
			System.out.println("===========日志===========: 接收到更新商品库存的请求，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt());
			Request request =  new ProductInventoryDBUpdateRequest(productInventory, productInventoryService);
			requestAsyncProcessService.process(request);
			return new Response(Response.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(Response.FAILURE);
		}
	}
	
	/**
	 * 获取商品库存
	 * @param productId 商品id
	 * @return 商品库存
	 */
	@GetMapping("/getProductInventory")
	public ProductInventory getProductInventory(Integer productId){
		System.out.println("===========日志===========: 接收到一个商品库存的读请求，商品id=" + productId);  
		ProductInventory productInventory = null;
		try {
			//刷新 redis 商品库存
			Request request = new ProductInventoryCacheRefreshRequest(productId, productInventoryService, false);
			requestAsyncProcessService.process(request);
			
			// 将请求扔给service异步去处理以后，就需要while(true)一会儿，在这里hang住
			// 去尝试等待前面有商品库存更新的操作，同时缓存刷新的操作，将最新的数据刷新到缓存中
			long startTime = System.currentTimeMillis();
			long endTime = 0L;
			long waitTime = 0L;
			
			// 等待超过200ms没有从缓存中获取到结果
			while(true){
				if(waitTime > 25000){
					break;
				}
				/*if(waitTime > 200){
					break;
				}*/
				// 尝试去redis中读取一次商品库存的缓存数据
				productInventory = productInventoryService.getProductInventoryCache(productId);
				// 如果读取到了结果，那么就返回
				if(null != productInventory){
					System.out.println("===========日志===========: 在200ms内读取到了redis中的库存缓存，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getInventoryCnt());  
					return productInventory;
				}else{
					// 如果没有读取到结果，那么等待一段时间
					Thread.sleep(20);
					endTime = System.currentTimeMillis();
					waitTime = endTime - startTime;
				}
			}
			// 直接尝试从数据库中读取数据
			productInventory = productInventoryService.getProductInventory(productId);
			if(null != productInventory){
				//代码运行到这里，有三种情况：
				//1、上一次也是读请求，数据刷入redis，但是redis LRU算法个清理掉了，标志位还是false，解决：此时下一个读请求是从缓存查不到数据的，再放一个读请求进队列，让数据去刷新一下
				//2、可能在200ms内，就是读请求在队列中一直积压着，没有等待到它执行（在实际生产环境中，基本只能升级机器或者优化sql）
				//所有就直接查一次库，然后给队列里塞进去一个刷新缓存的请求
				//3、数据库本身没有，缓存穿透，穿透redis，请求到达mysql库
				//强制刷新到缓存
				
				System.out.println("===========日志===========: 为什么队列不执行");
			    request = new ProductInventoryCacheRefreshRequest(productId, productInventoryService, true);
				requestAsyncProcessService.process(request);
				
				/*productInventoryService.setProductInventoryCache(productInventory);*/
				
				return productInventory;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//缓存 、 数据库都找不到，直接返回 -1L
		return new ProductInventory(productId, -1L);
	}

}
