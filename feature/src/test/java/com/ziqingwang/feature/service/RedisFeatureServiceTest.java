package com.ziqingwang.feature.service;

import com.ziqingwang.feature.App;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@Slf4j
public class RedisFeatureServiceTest {

	@Autowired
	private RedisFeatureService redisFeatureService;
	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void testLuaLock() {
		String key = "k";
		String value = "v";
		redisTemplate.delete(key);
		Assert.assertTrue(redisFeatureService.luaLock(key, value));
		Assert.assertFalse(redisFeatureService.luaUnlock(key, "v1"));
		Assert.assertTrue(redisFeatureService.luaUnlock(key, value));
	}
}