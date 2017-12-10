/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartloli.game.x.m.ubas.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 访问Redis数据库.
 * 
 * @author smartloli.
 *
 *         Created by Nov 12, 2017
 */
public class JedisUtils {
	/** 申明日志输出对象. */
	private static final Logger LOG = LoggerFactory.getLogger(JedisUtils.class.getName());
	/** 申明Redis数据库访问参数. */
	private static final int MAX_ACTIVE = 5000;
	private static final int MAX_IDLE = 800;
	private static final int MAX_WAIT = 10000;
	private static final int TIMEOUT = 10 * 1000;

	/** 创建一个Redis访问对象. */
	private static Map<String, JedisPool> jedisPools = new HashMap<String, JedisPool>();

	/** 初始化Redis连接池. */
	public static JedisPool initJedisPool(String jedisName) {
		JedisPool jPool = jedisPools.get(jedisName);
		if (jPool == null) {
			String host = SystemConfig.getProperty(jedisName + ".redis.host");
			int port = SystemConfig.getIntProperty(jedisName + ".redis.port");
			String[] hosts = host.split(",");
			for (int i = 0; i < hosts.length; i++) {
				try {
					jPool = newJeisPool(hosts[i], port);
					if (jPool != null) {
						break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			jedisPools.put(jedisName, jPool);
		}
		return jPool;
	}

	/** 获取Redis连接对象. */
	public static Jedis getJedisInstance(String jedisName) {
		LOG.debug("get jedis[name=" + jedisName + "]");
		JedisPool jedisPool = jedisPools.get(jedisName);
		if (jedisPool == null) {
			jedisPool = initJedisPool(jedisName);
		}

		Jedis jedis = null;
		for (int i = 0; i < 10; i++) {
			try {
				jedis = jedisPool.getResource();
				break;
			} catch (Exception e) {
				LOG.error("get resource from jedis pool error. times " + (i + 1) + ". retry...", e);
				jedisPool.returnBrokenResource(jedis);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					LOG.warn("sleep error", e1);
				}
			}
		}
		return jedis;
	}

	/** 创建一个新的Redis连接池. */
	private static JedisPool newJeisPool(String host, int port) {
		LOG.info("init jedis pool[" + host + ":" + port + "]");
		JedisPoolConfig config = new JedisPoolConfig();
		config.setTestOnReturn(false);
		config.setTestOnBorrow(false);
		config.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);
		config.setMaxActive(MAX_ACTIVE);
		config.setMaxIdle(MAX_IDLE);
		config.setMaxWait(MAX_WAIT);
		return new JedisPool(config, host, port, TIMEOUT);
	}

	/** 释放Redis连接池对象. */
	public static boolean release(String poolName, Jedis jedis) {
		LOG.debug("release jedis pool[name=" + poolName + "]");

		JedisPool jedisPool = jedisPools.get(poolName);
		if (jedisPool != null && jedis != null) {
			try {
				jedisPool.returnResource(jedis);
			} catch (Exception e) {
				jedisPool.returnBrokenResource(jedis);
			}
			return true;
		}
		return false;
	}

	/** 销毁Redis连接池中的所有对象. */
	public static void destroy() {
		LOG.debug("Destroy all pool");
		for (Iterator<JedisPool> itors = jedisPools.values().iterator(); itors.hasNext();) {
			try {
				JedisPool jedisPool = itors.next();
				jedisPool.destroy();
			} finally {
			}
		}
	}

	public static void destroy(String poolName) {
		try {
			jedisPools.get(poolName).destroy();
		} catch (Exception e) {
			LOG.warn("destory redis pool[" + poolName + "] error", e);
		}
	}
}
