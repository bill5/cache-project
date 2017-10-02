package com.bill.dao.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.bill.dao.RedisDao;

import redis.clients.jedis.JedisCluster;
/**
 * redis dao impl
 * @author bill
 * 2017年8月14日22:21:42
 */
@Repository
public class RedisDaoImpl implements RedisDao{
	
	@Resource
	private JedisCluster jedisCluster;

	@Override
	public void set(String key, String value) {
		jedisCluster.set(key, value);
	}
	@Override
	public String get(String key) {
		return jedisCluster.get(key);
	}
	@Override
	public void delete(String key) {
		jedisCluster.del(key);
	}

}
