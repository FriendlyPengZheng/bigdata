package com.taomee.redis;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class RedisUtil {

	private static String HOST = "10.30.100.2";
	private static int PORT = 6379;
	private static String PWD = "ta0mee@123";
	private static String redisHostsInner = "10.1.1.35:6379,10.1.1.153:6379,10.1.1.151:6379";
	private static String redisHostsOuter = "10.25.144.75:7000,10.25.144.75:7001,10.25.144.75:7002";
	
	public static Jedis getConnToRedis(){
		Jedis jedis = new Jedis(HOST, PORT);
		jedis.auth(PWD);
		return jedis;
	}
	
	public static JedisCluster getConnToRedisInnerCluster(){
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		for (String hostPort : redisHostsInner.split(",")) {
			String[] host_port = hostPort.split(":");
			jedisClusterNodes.add(new HostAndPort(host_port[0], Integer.valueOf(host_port[1])));
		}
		JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
		return jedisCluster;
	}
	
	public static JedisCluster getConnToRedisOuterCluster(){
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		for (String hostPort : redisHostsOuter.split(",")) {
			String[] host_port = hostPort.split(":");
			jedisClusterNodes.add(new HostAndPort(host_port[0], Integer.valueOf(host_port[1])));
		}
		JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
		return jedisCluster;
	}
}
