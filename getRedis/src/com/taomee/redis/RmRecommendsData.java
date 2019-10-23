package com.taomee.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RmRecommendsData {

	private static String PATTERN = "feeds:*:recommends";

	JedisCluster jedisClusterInner = RedisUtil.getConnToRedisInnerCluster();
	JedisCluster jedisClusterOuter = RedisUtil.getConnToRedisOuterCluster();
	
	//获取key ，相当于keys 命令
	public List<String> jedisKeysInner(String pattern){
		List<String> keys = new ArrayList<>(); 
		
		Map<String,JedisPool> nodesMap = jedisClusterInner.getClusterNodes();
		
		for(String node:nodesMap.keySet()){
			Jedis connection = nodesMap.get(node).getResource();
			try{
				keys.addAll(connection.keys(pattern));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				connection.close();
			}
		}
		return keys;
	}
	//获取key ，相当于keys 命令
	public List<String> jedisKeysOuter(String pattern){
		List<String> keys = new ArrayList<>();

		Map<String,JedisPool> nodesMap = jedisClusterOuter.getClusterNodes();
		
		for(String node:nodesMap.keySet()){
			Jedis connection = nodesMap.get(node).getResource();
			try{
				keys.addAll(connection.keys(pattern));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				connection.close();
			}
		}
		return keys;
	}
	
	public void rmDataInner(){
		List<String> keys = jedisKeysInner(PATTERN);
		if(keys.size() > 0){
			int i = 0;
			for (String key : keys) {
				System.out.println(i++ +":"+key);
				//jedisClusterInner.del(key);
			}
			jedisClusterInner.close();
		}
	}
	//注释
	public void rmDataOuter(){
		List<String> keys = jedisKeysOuter(PATTERN);
		if(keys.size() > 0){
			int i = 0;
			for (String key : keys) {
				//jedisClusterOuter.del(key);
				//jedisClusterInner.lrem(key,0,null);
				System.out.println(i++ +":"+key);
			}
			jedisClusterOuter.close();
		}
	}
	public static void main(String[] args) {
		new RmRecommendsData().rmDataOuter();
	}
}
