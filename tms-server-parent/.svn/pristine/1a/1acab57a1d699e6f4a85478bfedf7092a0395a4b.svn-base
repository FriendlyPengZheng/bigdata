package com.taomee.tms.storm.realtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public  class RedisUtil {
	private static final Logger LOG = LoggerFactory.getLogger(RedisUtil.class);

	//private JedisClusterConfig jedisClusterConfig;
	private JedisCluster jedisCluster;
	
	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}
	
	//private static final int expireSecs = 21600;
	// Redis服务器IP Redis的端口号
	static final String redisHosts = "10.1.1.35:6379,10.1.1.153:6379,10.1.1.151:6379"; 
	//static final String redisHosts = "10.25.11.107:6379";
	/**
	 * 初始化Redis连接  获取Jedis实例
	 */
	private static volatile RedisUtil instance = null;
	
	//初始化不可重入，重入后导致每个线程都会创建jedisCluster的实例，就会消耗内存，而且这块内存又没有被及时地释放掉，导致多用户并发以后，快速吃光了服务器的内存。
	private RedisUtil(){
		try {
			JedisPoolConfig config = new JedisPoolConfig();
		   /* config.setMaxTotal(MAX_ACTIVE); //可用连接实例的最大数目，默认值为8；
		    config.setMaxIdle(MAX_IDLE);  //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
		    config.setMaxWaitMillis(MAX_WAIT); //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时
		    config.setTestOnBorrow(TEST_ON_BORROW);*/
		    
			Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();

			for (String hostPort : redisHosts.split(",")) {
				String[] host_port = hostPort.split(":");
				jedisClusterNodes.add(new HostAndPort(host_port[0], Integer.valueOf(host_port[1])));
			}
			jedisCluster = new JedisCluster(jedisClusterNodes, config);
			//return true;
		} catch (Exception e) {
			e.printStackTrace();
			//return false;
		}
		//return true;
	}
	
	public static RedisUtil getInstance() {
		if(instance == null) {
			synchronized (RedisUtil.class) {
				if(instance == null) {
					instance  = new RedisUtil();
				}
			}
		}
		return instance;
	}

/*	*//**
	 * 释放jedis资源
	 * 
	 * 
	 *//*
	public static void cleanup() {
		jedisCluster.close();
	}*/

	public  String jedisSet(String key, String value) {
		// TODO Auto-generated method stub
		String values = "fail";
		try {
			values = jedisCluster.set(key, value);
		} catch (Exception e) {
			LOG.error("redis set fail" + e.getMessage());
			e.printStackTrace();
		}
		return values;
	}

	public  String jedisGet(String key) {
		// TODO Auto-generated method stub
		String values = "fail";
		try {
			values = jedisCluster.get(key);
		} catch (Exception e) {
			LOG.error("redis get fail" + e.getMessage());
			e.printStackTrace();
		}
		return values;
	}

	public  Map<String, String> jedishGetAll(String key) {
		// TODO Auto-generated method stub
		Map<String, String> mapInfos = new HashMap<String, String>();
		try {
			mapInfos = jedisCluster.hgetAll(key);
			for (int i = 0; i < 1440; i++) {
				if (mapInfos.containsKey(String.valueOf(i))) {
					;
				} else {
					mapInfos.put(String.valueOf(i), null);
				}
			}
			return mapInfos;
		} catch (Exception e) {
			LOG.error("redis数据未获取到" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
