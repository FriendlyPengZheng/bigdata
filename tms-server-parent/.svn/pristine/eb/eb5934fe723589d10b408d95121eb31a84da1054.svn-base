package com.taomee.tms.storm.realtime;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class TestStatic {

	
	//private static final int expireSecs = 21600;
	// Redis服务器IP Redis的端口号
	static final String redisHosts = "10.1.1.35:6379,10.1.1.153:6379,10.1.1.151:6379"; 
	private JedisCluster jedisCluster = null;
	public void init()
	{
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();

		for (String hostPort : redisHosts.split(",")) {
			String[] host_port = hostPort.split(":");
			jedisClusterNodes.add(new HostAndPort(host_port[0], Integer.valueOf(host_port[1])));
		}
		jedisCluster = new JedisCluster(jedisClusterNodes);
	}
	
	public static void main(String[] args) {
		/*init();
		System.out.println("redis连接:"+jedisCluster);
		//jedisCluster.
		//jedisCluster = null;
		
		jedisCluster.close();
		jedisCluster = null;
		System.out.println("redis连接2:"+jedisCluster);*/
		TestStatic ts = new TestStatic();
		ts.init();
		System.out.println("redis连接1:" +ts.jedisCluster);
		ts.jedisCluster.close();
		System.out.println("redis连接2:" +ts.jedisCluster);
	}

}
