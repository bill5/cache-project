package com.bill.cache.zk;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * zookeeper 分布式锁工具类
 * @author bill
 * @date 2017年10月3日 上午11:39:08
 */
public class ZookeeperSession {
	
	private ZooKeeper zookeeper;
	//计数器（同步锁），连接信号量，用于控制并发请求时，确保 zookeeper client 与 server 已连接
	private static CountDownLatch connectSemaphore = new CountDownLatch(1);
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperSession.class);
	
	public ZookeeperSession(){
		try {
			// 连接 zookeeper server
			this.zookeeper = new ZooKeeper("192.168.0.16:2181,192.168.0.17:2181,192.168.0.18:2181", 50000, new ZookeeperWatcher());
			// 等待，保证 client、server连接
			connectSemaphore.await();
			LOGGER.debug(" zookeeper session established ...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  获取分布式锁
	 * @param productId 商品id
	 */
	public void acquireDistributedLock(Long productId){
		String path = "/product-lock-" + productId;
		try {
			// 尝试获取分布式锁
			zookeeper.create(path, "".getBytes(), Ids.READ_ACL_UNSAFE, CreateMode.EPHEMERAL);
			LOGGER.debug("success to acquire lock for productId [{}]", productId);
		} catch (Exception e) {
			// 如果报 nodeExitsException,说明已经有请求获取了锁，所有当前重复尝试获取锁，知道获取到锁为止
			int count = 0;
			while(true){
				try {
					// 睡眠一下下,为了测试效果，生产环境，可以20ms
					Thread.sleep(1000);
					// 再次尝试获取分布式锁
					zookeeper.create(path, "".getBytes(), Ids.READ_ACL_UNSAFE, CreateMode.EPHEMERAL);
				} catch (Exception e2) {
					// 如果报 nodeExitsException,说明已经有请求获取了锁，所有当前重复尝试获取锁，知道获取到锁为止
					LOGGER.debug("repeat to acquire lock for productId:[{}] - count:[{}] ...", productId, count);
					count ++;
					continue;
				}
				LOGGER.debug("success to acquire lock for productId:[{}] after count:[{}] repeat 。。。", productId, count);
				break;
			}
		}
	}
	
	/**
	 * 释放分布式锁
	 * @param productId 商品id
	 */
	public void releaseDistributedLock(Long productId){
		String path = "/product-lock-" + productId;
		try {
			// 删除node，释放锁
			zookeeper.delete(path, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建 zookeeper session watcher
	 * @author bill
	 * @date 2017年10月3日 下午12:05:21
	 */
	private class ZookeeperWatcher implements Watcher{

		@Override
		public void process(WatchedEvent evt) {
			LOGGER.debug("receive zookeeper watched event: {}", evt.getState());
			if(KeeperState.SyncConnected == evt.getState()){
				// client、server 已连接 是否等待信号量锁
				connectSemaphore.countDown();
			}
		}
		
	}
	
	
	/**
	 * 单例有很多种方式去实现，这里采取绝对线程安全的一种方式
	 * 静态内部类的方式，去初始化单例
	 */
	private static class Singleton {
		
		private static ZookeeperSession instance;
		
		static{
			instance = new ZookeeperSession();
		}
		
		public static ZookeeperSession getInstance(){
			return instance;
		}
	}
	
	/**
	 * jvm 的机制去保证多线程并发安全
	 * 内部类的初始化，一定只会发生一次，不管多少个线程并发去初始化
	 */
	public static ZookeeperSession getIntance(){
		return Singleton.getInstance();
	}
	
	/**
	 * 初始化单例 zookeeperSession
	 */
	public static void init(){
		getIntance();
	}

}
