package com.bill.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import com.google.common.collect.Maps;

/**
 * 请求的内存队列
 * @author bill
 * 2017年8月14日00:15:41
 */
public class RequestQueue {
	
	/**
	 * 内存队列
	 */
	private List<ArrayBlockingQueue<Request>> queues = new ArrayList<ArrayBlockingQueue<Request>>();
	
	/**
	 * 读去重优化标识位Map
	 */
	private Map<Integer,Boolean> flagMap = Maps.newConcurrentMap();

	public Map<Integer, Boolean> getFlagMap() {
		return flagMap;
	}

	/**
	 * 单例有很多种方式去实现，这里采取绝对线程安全的一种方式
	 * 静态内部类的方式，去初始化单例
	 *
	 */
	private static class Singleton{
		private static RequestQueue instance;
		
		static {
			instance = new RequestQueue();
		}
		
		public static RequestQueue getInstance(){
			return instance;
		}
	}
	
	/**
	 * jvm 的机制去保证多线程并发安全
	 * 内部类的初始化，一定只会发生一次，不管多少个线程并发去初始化
	 */
	public static RequestQueue getInstance(){
		return Singleton.getInstance();
	}
	
	/**
	 *  添加内存队列
	 * @param queue 队列
	 */
	public  void addQueue(ArrayBlockingQueue<Request> queue){
		this.queues.add(queue);
	}
	
	/**
	 * 获取内存队列数量
	 * @return 数量
	 */
	public int size(){
		return this.queues.size();
	}
	
	/**
	 * 获取内存队列
	 * @param index 索引
	 * @return 内存队列
	 */
	public ArrayBlockingQueue<Request> getQueue(int index){
		return this.queues.get(index);
	}
	
}
