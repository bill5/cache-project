package com.bill;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bill.dao.RedisDao;
import com.bill.mapper.UserMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheInventoryApplicationTests {

	@Resource
	private UserMapper userMapper;
	
	@Resource
	private RedisDao redisDao;
	
	@Test
	public void findUserTest() {
		System.out.println(userMapper.findUser().toString());
	}
	
	@Test
	public void findCacheUserTest(){
		//redisDao.set("user_cache_lisi","{\"name\": \"lisi\",\"age\":28}");
		System.out.println(redisDao.get("user_cache_lisi"));
	}

}
