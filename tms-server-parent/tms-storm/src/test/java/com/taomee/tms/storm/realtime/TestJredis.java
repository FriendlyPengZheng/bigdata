package com.taomee.tms.storm.realtime;

public class TestJredis {
	
	public static void main(String[] args) {
		RedisUtil redisUtil1 = RedisUtil.getInstance();
		RedisUtil redisUtil2 = RedisUtil.getInstance();
		System.out.println(redisUtil1 == redisUtil2);
		System.out.println("redisUtil1:"+redisUtil1);
		System.out.println("redisUtil2:"+redisUtil2);
		System.out.println("redisGetValue:"+redisUtil1.jedisGet("2017-08-14_|6_|14_|"));
	}

}
