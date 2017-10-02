package com.bill.cache.dao;

/**
 * redis dao
 * @author bill
 * 2017年8月14日22:21:42
 */
public interface RedisDao {
	/**
	 * redis set key - value
	 * @param key 键
	 * @param value 值
	 */
	void set(String key, String value);
	
	/**
	 * redis get key 
	 * @param key 键
	 * @return value 值
	 */
	String get(String key);
	
	/**
	 * redis del key - value
	 * @param key 键
	 */
	void delete(String key);
}
