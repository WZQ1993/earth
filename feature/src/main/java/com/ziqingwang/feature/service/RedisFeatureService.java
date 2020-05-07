package com.ziqingwang.feature.service;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

/**
 * 1. 分布式锁 - 结合LUA脚本
 * 6. scan
 *
 * @author: Ziven
 * @date: 2020/04/20
 **/
@Service
@Slf4j
public class RedisFeatureService {

	@Autowired
	private RedisTemplate redisTemplate;

	public Boolean luaLock(String key, String value) {
		DefaultRedisScript<Boolean> lockScript = new DefaultRedisScript<Boolean>();
		lockScript.setScriptSource(
				new ResourceScriptSource(new ClassPathResource("redis_lock.lua")));
		lockScript.setResultType(Boolean.class);
		// 封装参数
		List<Object> keyList = new ArrayList<Object>();
		keyList.add(key);
		keyList.add(value);
		Boolean result = (Boolean) redisTemplate.execute(lockScript, keyList);
		return result;
	}

	public Boolean luaUnlock(String key, String value) {
		DefaultRedisScript<Boolean> lockScript = new DefaultRedisScript<Boolean>();
		lockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis_unlock.lua")));
		lockScript.setResultType(Boolean.class);
		// 封装参数
		List<Object> keyList = new ArrayList<Object>();
		keyList.add(key);
		keyList.add(value);
		Boolean result = (Boolean) redisTemplate.execute(lockScript, keyList);
		return result;
	}


}
