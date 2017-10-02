package com.bill.service.impl;

import java.util.concurrent.ArrayBlockingQueue;

import org.springframework.stereotype.Service;

import com.bill.request.Request;
import com.bill.request.RequestQueue;
import com.bill.service.RequestAsyncProcessService;

/** 请求异步执行service实现
 * @author bill
 * 2017年8月13日22:51:26
 */
@Service
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {

	@Override
	public void process(Request request) throws Exception {
		try {
			// 根据请求商品的id，计算路由到对应的内存队列
			ArrayBlockingQueue<Request> queue = getRoutingQueue(request.getProductId());
			//是否强制刷新
			/*if(!request.isForceRefresh()){
				//先做读请求的去重
				RequestQueue requestQueue = RequestQueue.getInstance();
				Map<Integer,Boolean> flagMap = requestQueue.getFlagMap();
				if(request instanceof ProductInventoryDBUpdateRequest){
					//如果是一个更新数据库请求，那么就将那个productId对应的标识设置为true
					flagMap.put(request.getProductId(), true);
				}else if(request instanceof ProductInventoryCacheRefreshRequest){
					Boolean flag = flagMap.get(request.getProductId());
					if(null == flag){
						flagMap.put(request.getProductId(), false);
					}
					//如果是缓存刷新的请求，那么就判断，如果标识为不空，而且是true,就说明之前有一个这个商品库存的数据库更新请求
					if(null != flag && flag){
						flagMap.put(request.getProductId(), false);
					}
					//如果是缓存刷新的请求，而且发现标识不为空，但是标识是false
					//说明前面已经有一个数据库更新请求 ＋一个缓存刷新请求了
					if(flag != null && !flag){
						//对应这种读请求，直接过滤掉，不需要放到内存队列里面
						return;
					}
				}
			}*/
			// 将请求放入对应的队列中，完成处理
			queue.put(request);
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	/**
	 * 根据productId计算，路由 到 某个内存队列
	 * @param productId 商品id
	 * @return 内存队列
	 */
	private ArrayBlockingQueue<Request> getRoutingQueue(Integer productId){
		RequestQueue requestQueue = RequestQueue.getInstance();
		String key = String.valueOf(productId);
		int h;
		int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
		// 对hash值取模，将hash值路由到指定的内存队列中，比如内存队列大小 size
		// 用内存队列的数量对hash值取模之后，结果一定是在0 ~ （ size - 1  ）之间
		// 所以任何一个商品id都会被固定路由到同样的一个内存队列中去的
		int index = (requestQueue.size() - 1) & hash;
		System.out.println("===========日志===========: 路由内存队列，商品id=" + productId + ", 队列索引=" + index);  
		return requestQueue.getQueue(index);
	}
}
