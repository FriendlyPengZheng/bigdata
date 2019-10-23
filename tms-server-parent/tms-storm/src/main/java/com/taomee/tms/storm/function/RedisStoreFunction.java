package com.taomee.tms.storm.function;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.storm.redis.common.config.JedisClusterConfig;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;

import com.taomee.bigdata.lib.TmsProperties;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class RedisStoreFunction extends BaseFunction {
	private static final long serialVersionUID = 2241587157574977853L;
	private static final Logger LOG = LoggerFactory.getLogger(RedisStoreFunction.class);

	private JedisClusterConfig jedisClusterConfig;
	private JedisCluster jedisCluster;
	private Pattern pattern;
	private Integer partitionIndex;
	
	private static TmsProperties properties = new TmsProperties(System.getProperty("user.home")+"/storm/tms-storm.properties");
	
	private static int expireSecs;
	
	static{
		expireSecs = properties.getProperty("redis.minute.key.expire.seconds",2592000);
	}
	
	public RedisStoreFunction(JedisClusterConfig jedisConfig) {
		this.jedisClusterConfig = jedisConfig;
	}

	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map conf,TridentOperationContext context) {
		this.partitionIndex = context.getPartitionIndex();
		
		//连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setBlockWhenExhausted(true);
		//redis最大连接数,默认是8
		/*jedisPoolConfig.setMaxTotal(60);
		jedisPoolConfig.setMaxWaitMillis(10000);*/
		//jedisPoolConfig
		
		//jedisPoolConfig.setMaxWaitMillis(-1);
		//jedisPoolConfig

		
		this.jedisCluster = new JedisCluster(jedisClusterConfig.getNodes(),
				jedisClusterConfig.getTimeout(),
				jedisClusterConfig.getMaxRedirections(), jedisPoolConfig);
		LOG.info("partition {} initing RedisStoreFunction..." ,this.partitionIndex);
		pattern = Pattern.compile("([0-9\\-]+) ([\\d]{2}):([\\d]{2})(.*)");
		
	}
	
	@Override
    public void cleanup() {
		jedisCluster.close();
    }

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		String key = tuple.getString(0);// key类似"2017-01-22 00:01_14_10_"
		Long value = tuple.getLong(1);

//		LOG.info("RedisStoreFunction execute, key is " + key + ", value is "
//				+ value);

		// 第一版本，以<key,value>的形式保存到redis, 并设置过期时间。
		// 最后加"|"以示和stat区分
		// String ret = jedisCluster.setex("min_" + key, expireSecs, value.toString());
		// // TODO 抛出异常
		// if(ret == null)
		// {
		// LOG.error("setex " + key + ", " + value + " to redis failed.");
		// }
		
		// 第二版本，根据date设置到redis，一天一个hash对象，过期时间设置为30天
		Matcher matcher = pattern.matcher(key);
		if (!matcher.find() || matcher.groupCount() != 4) {
			LOG.error("RedisStoreFunction execute met illegal key[" + key + "]");
			return;
		}

		String hashName = matcher.group(1) + matcher.group(4);
		String hashKey = Integer.toString(Integer.parseInt(matcher.group(2)) * 60 + Integer.parseInt(matcher.group(3)));
		String hashValue = value.toString();
//		LOG.debug("RedisStoreFunction execute, hash name is " + hashName + 
//				", hashKey is " + hashKey + ", hashValue is " + hashValue);
		LOG.debug("partition {} hsetting {} with <{},{}> ", this.partitionIndex,hashName, hashKey, hashValue);
		
		// 设置hash对象，正常返回0或1
//		LOG.debug("execute run... {}",jedisCluster);
		Long ret = jedisCluster.hset(hashName, hashKey, hashValue);
//		LOG.debug("print redis ret hset:" +ret);
		if (ret != 0 && ret != 1) {
			LOG.error("RedisStoreFunction execute, jedisCluster hset ret [" + ret + "]");
			return;
		}
		
		// 设置超时时间，正常返回1
		ret = jedisCluster.expire(hashName, expireSecs);
//		LOG.debug("print redis ret expire:" +ret);
		if (ret != 1) {
			LOG.error("RedisStoreFunction execute, jedisCluster expire ret [" + ret + "]");
			return;
		}
	}

//	public static void main(String[] args) {
//
//		String redisHosts = "10.1.1.35:6379,10.1.1.153:6379,10.1.1.151:6379";
//		Set<InetSocketAddress> nodes = new HashSet<InetSocketAddress>();
//		for (String hostPort : redisHosts.split(",")) {
//			String[] host_port = hostPort.split(":");
//			nodes.add(new InetSocketAddress(host_port[0], Integer
//					.valueOf(host_port[1])));
//		}
//		JedisClusterConfig clusterConfig = new JedisClusterConfig.Builder()
//				.setNodes(nodes).build();
//		JedisCluster jedisCluster = new JedisCluster(clusterConfig.getNodes(),
//				clusterConfig.getTimeout(), clusterConfig.getMaxRedirections(),
//				new JedisPoolConfig());
//
//		String key = "2017-01-22 00:01_14_10_";
//		Long value = 101L;
//		Pattern pattern = Pattern
//				.compile("([0-9\\-]+) ([\\d]{2}):([\\d]{2})(.*)");
//		Matcher matcher = pattern.matcher(key);
//
//		if (!matcher.find() || matcher.groupCount() != 4) {
//			System.out.println("fail");
//		} else {
//			System.out.println("success");
//		}
//
//		System.out.println(matcher.group(1));
//		System.out.println(matcher.group(2));
//		System.out.println(matcher.group(3));
//		System.out.println(matcher.group(4));
//		Long ret = jedisCluster.hset(
//				matcher.group(1),
//				Integer.toString(Integer.parseInt(matcher.group(2)) * 60 + Integer.parseInt(matcher.group(3))), value.toString());
////		System.out.println("hset ret is [" + ret + "]");
//		ret = jedisCluster.expire(matcher.group(1), 10);
////		System.out.println("expire ret is [" + ret + "]");
//		jedisCluster.close();
//	}

}
