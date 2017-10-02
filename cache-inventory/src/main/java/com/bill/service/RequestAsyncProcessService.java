package com.bill.service;

import com.bill.request.Request;

/** 请求异步执行service
 * @author bill
 * 2017年8月13日22:51:26
 */
public interface RequestAsyncProcessService {
	
	/**
	 * 异步处理请求
	 * @param request 请求
	 */
	void process(Request request)  throws Exception;
}
