package com.taomee.tms.storm.monitor;

//import storm.trident.operation.BaseFunction;
//import storm.trident.operation.TridentCollector;
//import storm.trident.operation.TridentOperationContext;
//import storm.trident.tuple.TridentTuple;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.storm.redis.common.config.JedisClusterConfig;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class CacheStoreFunction extends BaseFunction {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(CacheStoreFunction.class);
	private JedisClusterConfig jedisClusterConfig;
	private JedisCluster jedisCluster;
	
	public CacheStoreFunction(JedisClusterConfig jedisConfig)
	{
		this.jedisClusterConfig = jedisConfig;
	}
	
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map conf, TridentOperationContext context) {
		this.jedisCluster = new JedisCluster(
				jedisClusterConfig.getNodes(),
				jedisClusterConfig.getTimeout(),
				jedisClusterConfig.getMaxRedirections(),
				new JedisPoolConfig());
    }

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		String min_data_key = tuple.getString(0);
		Long min_data_value = tuple.getLong(1);
		
		// 保存到redis, 并设置过期时间。
		// 最后加"|"以示和stat区分
		String ret = jedisCluster.setex(min_data_key + "|", 600, min_data_value.toString());
		if(ret == null)
		{
			LOG.error("setex " + min_data_key + ", " + min_data_value + " to redis failed."); 
		}
	}

}
