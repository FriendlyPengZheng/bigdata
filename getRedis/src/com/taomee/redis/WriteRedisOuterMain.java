package com.taomee.redis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import redis.clients.jedis.JedisCluster;

public class WriteRedisOuterMain {

	public static void main(String[] args) {
		RmRecommendsData rrd = new RmRecommendsData();
		rrd.rmDataOuter();
		File file = null;
		if(args.length >= 1){
			file = new File(args[0]);
		}else {
			new Throwable("args[] is empty,please enter a fileName").printStackTrace();
			return;
		}
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(in);
			br = new BufferedReader(reader);String line = null;
			while((line = br.readLine()) != null){
				String[] items = line.split("\t");
				String key = items[0];
				String[] recommends = items[1].split("#");
				JedisCluster jedisClusterouter = RedisUtil.getConnToRedisOuterCluster();
				jedisClusterouter.rpush("feeds:"+key+":recommends", recommends);
				jedisClusterouter.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
