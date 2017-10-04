package com.bill;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bill.cache.CacheCacheApplication;
import com.bill.cache.zk.ZookeeperSession;


/**
 * 
 * zookeeper 测试类
 * @author bill
 * @date 2017年10月3日 下午12:05:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=CacheCacheApplication.class, webEnvironment=WebEnvironment.DEFINED_PORT)
@EnableAutoConfiguration
public class ZookeeperSessionTest {
	
	@Test
	public void zookeeperTest(){
		ZookeeperSession.init();
	}
	
	public static void main(String[] args) {
		ZookeeperSession.init();
	}
	
	
}
