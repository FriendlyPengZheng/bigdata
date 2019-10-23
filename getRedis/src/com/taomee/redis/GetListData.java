package com.taomee.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class GetListData {

	private static String PATTERN = "feeds:*:list";
	private static String SEPARATOR = "#";
	
	public List<String> getData(){
		List<String> value = new ArrayList<String>();
		Jedis jedis = RedisUtil.getConnToRedis();
		Set<String> keys = jedis.keys(PATTERN);//获取所有的关注zset中的key集合
		for (String key : keys) {
			String[] items = key.split(":");
			String acid = items[1];
			long len = jedis.zcard(key);
			Set<String> acidSets = jedis.zrange(key, 0, len-1);//获取key对应的所有关注米米号集合
			StringBuffer myFollow = new StringBuffer();
			for (String s : acidSets) {
				if(myFollow.length()==0){
					myFollow.append(s);
				}else{
					myFollow.append(SEPARATOR+s);
				}
			}
			value.add(String.format("%s\t%s", acid,myFollow));
		}
		jedis.close();
		return value;
	}
}
