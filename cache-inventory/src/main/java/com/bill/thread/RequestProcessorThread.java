package com.bill.thread;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import com.bill.request.ProductInventoryCacheRefreshRequest;
import com.bill.request.ProductInventoryDBUpdateRequest;
import com.bill.request.Request;
import com.bill.request.RequestQueue;

/**执行请求的工作线程
 * @author bill
 * 2017年8月14日00:12:10
 */
public class RequestProcessorThread implements Callable<Boolean>{

	
	//自己监控的内存队列
	private ArrayBlockingQueue<Request> queue;
	
	public RequestProcessorThread(ArrayBlockingQueue<Request> queue){
		this.queue = queue;
	}
	 
	@Override
	public Boolean call() throws Exception {
		try {
			while(true){
				// ArrayBlockingQueue 如果队列满了，或者是空的，那么都会在执行操作的时候，阻塞住
				Request request = queue.take();
				System.out.println("===========日志===========: 压根就没进来");
				//是否强制刷新
				if(!request.isForceRefresh()){
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
							return true;
						}
					}
				}
				System.out.println("===========日志===========: 工作线程处理请求，商品id=" + request.getProductId()); 
				//具体请求处理
				request.process();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
