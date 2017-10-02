package com.bill.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bill.request.Request;
import com.bill.request.RequestQueue;

/**
 * 线程池-单例
 * @author bill
 * 2017年8月13日22:34:55
 */
public class RequestProcessorThreadPool {
	
	/**
	 * 在实际项目中，你设置线程池大小是多少，每个线程监控的那个内存队列的大小是多少
	 * 都可以做到一个外部的配置文件中
	 * 这里就不那么做了，直接写死
	 */
	private ExecutorService threadPool = Executors.newFixedThreadPool(10);
	public RequestProcessorThreadPool(){
		RequestQueue requestQueue = RequestQueue.getInstance();
		for(int i = 0; i < 10; i ++){
			ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<Request>(100);
			// 添加内存队列
			requestQueue.addQueue(queue);
			// 处理队列工作线程
			threadPool.submit(new RequestProcessorThread(queue));
		}
	}
	
	/**
	 * 单例有很多种方式去实现，这里采取绝对线程安全的一种方式
	 * 静态内部类的方式，去初始化单例
	 *
	 */
	private static class Singleton {
		
		private static RequestProcessorThreadPool instance;
		
		static{
			instance = new RequestProcessorThreadPool();
		}
		
		public static RequestProcessorThreadPool getInstance(){
			return instance;
		}
	}
	
	/**
	 * jvm 的机制去保证多线程并发安全
	 * 内部类的初始化，一定只会发生一次，不管多少个线程并发去初始化
	 */
	public static RequestProcessorThreadPool getInstance(){
		return Singleton.getInstance();
	}
	
	/**
	 * 初始化内存队列 + 工作线程
	 */
	public static void init(){
		getInstance();
	}
}
