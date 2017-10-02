package com.bill.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.bill.thread.RequestProcessorThreadPool;

/**
 * 系统初始化监听器
 * @author bill
 * 2017年8月13日22:23:27
 */
public class InitListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// 初始化内存队列 + 工作线程池
		RequestProcessorThreadPool.init();
	}

}
