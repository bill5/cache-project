package com.bill.request;

/**请求接口
 * @author bill
 * 2017年8月13日22:51:26
 */
public interface Request {
	/**
	 * 请求处理
	 */
	void process();
	
	/**
	 * 获取商品 id
	 * @return
	 */
	Integer getProductId();
	
	/**
	 * 是否强制刷新到redis缓存
	 * @return
	 */
	boolean isForceRefresh();
}


