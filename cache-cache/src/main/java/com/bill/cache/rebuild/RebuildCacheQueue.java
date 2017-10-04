package com.bill.cache.rebuild;

import java.util.concurrent.ArrayBlockingQueue;

import com.bill.cache.bean.ProductInfo;

/**
 * 
 * 重建缓存的内存队列
 * @author bill
 * @date 2017年10月3日 上午11:39:48
 */
public class RebuildCacheQueue {
	
	/**
	 * 内存队列
	 */
	private ArrayBlockingQueue<ProductInfo> queue = new ArrayBlockingQueue<ProductInfo>(1000);
	
	
	/**
	 * 将商品信息对象加入队列
	 * @param productInfo 商品信息对象
	 */
	public void putProductInfo(ProductInfo productInfo){
		try {
			queue.put(productInfo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  从队列中获取商品信息对象
	 * @return 商品信息对象
	 */
	public ProductInfo takeProductInfo(){
		try {
			return queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 单例有很多种方式去实现，这里采取绝对线程安全的一种方式
	 * 静态内部类的方式，去初始化单例
	 */
	private static class Singleton {
		
		private static RebuildCacheQueue instance;
		
		static {
			instance = new RebuildCacheQueue();
		}
		
		private static RebuildCacheQueue getInstance(){
			return instance;
		}
	}
	
	/**
	 * jvm 的机制去保证多线程并发安全
	 * 内部类的初始化，一定只会发生一次，不管多少个线程并发去初始化
	 */
	public static RebuildCacheQueue getInstance(){
		return Singleton.getInstance();
	}
}
