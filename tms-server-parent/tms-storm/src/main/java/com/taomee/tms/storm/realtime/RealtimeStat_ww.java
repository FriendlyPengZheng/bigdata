package com.taomee.tms.storm.realtime;

import com.taomee.bigdata.lib.TmsProperties;
import com.taomee.tms.storm.aggregator.Max;
import com.taomee.tms.storm.aggregator.Min;
import com.taomee.tms.storm.aggregator.Sum;
import com.taomee.tms.storm.filter.OpAssignFilter;
import com.taomee.tms.storm.filter.OpCountFilter;
import com.taomee.tms.storm.filter.OpMaxFilter;
import com.taomee.tms.storm.filter.OpMinFilter;
import com.taomee.tms.storm.filter.OpSumFilter;
import com.taomee.tms.storm.function.BaseCacheParamGenFunction;
import com.taomee.tms.storm.function.CountCacheParamGenFunction;
import com.taomee.tms.storm.function.LogSplitFunctionBasic2;
import com.taomee.tms.storm.function.ReadRedisDayData2HBaseFunction;
import com.taomee.tms.storm.function.RedisStoreFunction;
import com.taomee.tms.storm.function.TimeConvertFunction;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.OpaqueTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.trident.state.RedisClusterMapState;
import org.apache.storm.redis.trident.state.Options;
import org.apache.storm.redis.common.config.JedisClusterConfig;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.builtin.Count;
import org.apache.storm.trident.state.OpaqueValue;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.tuple.Fields;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class RealtimeStat_ww 
{
    public static void main( String[] args ) throws AlreadyAliveException, InvalidTopologyException{
    	TmsProperties properties = new TmsProperties(System.getProperty("user.home")+"/storm/tms-storm.properties");
    	
    	for(int i = 0 ;i<args.length;i++){
    		if(args[i].equals("--running-mode")){
    			properties.setProperty("running-mode", args[++i]);
    		}
    	}
    	
    	TridentTopology topology = new TridentTopology();
    	
    	TridentKafkaConfig tridentKafkaConf;
    	if(properties.get("running-mode") != null && properties.get("running-mode").equals("local")){
    		tridentKafkaConf = new TridentKafkaConfig(new ZkHosts("10.1.1.187:2181,10.1.1.176:2181,10.1.1.175:2181"), // default zkPath /brokers/
    				"storm-basic-local-test", "storm-local-basic-test-eclipse"); // topic
    	}else{
    		tridentKafkaConf = new TridentKafkaConfig(new ZkHosts(properties.getProperty("zkHosts")), // default zkPath /brokers/
    				properties.getProperty("commonTopicName"), properties.getProperty("kafaCommonGroupName")); // topic
    	}
		
		
		if(properties.get("running-mode") != null && properties.get("running-mode").equals("local")){
			tridentKafkaConf.ignoreZkOffsets = true;
			tridentKafkaConf.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
			/* 
			 * 通过此配置项强制每次本地调试时从topic的最新offset处读取
			 * 如果不配置此项则在LocalCluster模式下总是从开头处的offset读取，会导致在持久化时因为输入的txid太旧不予更新state而报异常退出
			 */
		}
		
		// storm-kafka内置的类，从kafka中读取数据，作为字符串处理并在tuple中输入一个默认名称为"str"的字段
		// 增加吞吐量的方式包括（http://stackoverflow.com/questions/24510456/performance-issues-kafka-storm-trident-opaquetridentkafkaspout）：
		// - 有多少个partition就设置多少个spout
		// - topology.max.spout.pending：同时活跃的batch数量，你必须设置同时处理的batch数量，如果你不指定，默认是1。
		// - tridentConfig.fetchSizeBytes：
		// - topology.trident.batch.emit.interval.millis
		// - topology.message.timeout.secs
		// - use G1GC garbage collection
		
		tridentKafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme()); // default fields named "str"
		OpaqueTridentKafkaSpout kafkaSpout = new OpaqueTridentKafkaSpout(tridentKafkaConf);
		
		RedisDataTypeDescription dataTypeDescription = new RedisDataTypeDescription(RedisDataTypeDescription.RedisDataType.STRING, null);
		JedisClusterConfig clusterConfig = getClusterConfig(properties.getProperty("redisHosts"));
		StateFactory minuteStateFactory = RedisClusterMapState.opaque(clusterConfig, getOptions(dataTypeDescription,properties.getProperty("redis.storm.minute.state.expiresecs", 300)));
		StateFactory dayStateFactory = RedisClusterMapState.opaque(clusterConfig, getOptions(dataTypeDescription,properties.getProperty("redis.storm.day.state.expiresecs",129600)));
		
        //RedisClusterState.Factory factory = new RedisClusterState.Factory(clusterConfig);

        /*
         * set parallelismHint() and partitionBy() to split stream.
         */
//        CustomStreamGrouping myCustomStreamGrouping = new MyCustomStreamGrouping();
//        topology.newStream(ACCOUNT_LOGIN_LOGOUT_STREAM, kafkaSpout).partition(myCustomStreamGrouping)
//        .each(kafkaConf.scheme.getOutputFields(), new DebugLogFilter()).parallelismHint(5)
//        .each(kafkaConf.scheme.getOutputFields(), 
//        		new LoginLogoutSplitFunction(), 
//        		new Fields("timestamp", "cmd_return_status", "return_code", "account_id"))
//        .project(new Fields("timestamp", "cmd_return_status", "return_code", "account_id"))
//        .each(new Fields("timestamp", "cmd_return_status"), 
//        		new MovingAverageFunction(EWMA.Time.SECONDS),
//        		new Fields("avg"))
//        .each(new Fields("cmd_return_status", "avg"), new ThresholdAlertFunction(3.0), new Fields("alert"))
//        .groupBy(new Fields("cmd_return_status"))
//        .persistentAggregate(factory, new Count(), new Fields("count"))
//        .newValuesStream()
//        .each(new Fields("cmd_return_status", "count"), new CacheStoreFunction(clusterConfig), new Fields()); // 保存分钟数据
        
		Stream trunkStream = topology.newStream(properties.getProperty("commonStreamName"), kafkaSpout).parallelismHint(2)		// 后续改为读取相应kafka中topic，有多少partition就设置多少并行度
				.each(tridentKafkaConf.scheme.getOutputFields(),new LogSplitFunctionBasic2(properties),new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("logSplit");
		
		//count
		Stream countStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("count");
		Stream countSplitStream = countStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpCountFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new CountCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue"))
				.project(new Fields("dateTime_schemaId_serverId_cascadeValue"));
		/*
		 * day count,因为分钟最后存储的结果也是存储在redis上，也为"2017-06-30_107_14_"这种格式，但是其value的值是不一样的，
		 * 导致在redisstate需要更新持久化的值的时候因为相同key的value类型不一致，出现数据无法更新。报错:
		 * redis.clients.jedis.exceptions.JedisDataException: 
		 * WRONGTYPE Operation against a key holding the wrong kind of value,因此存储在redis天上的值的key的格式需要做下调整
		 */
		countSplitStream.each(new Fields("dateTime_schemaId_serverId_cascadeValue"),
				new TimeConvertFunction(2),
				new Fields("dateDay_schemaId_serverId_cascadeValue")).
				project(new Fields("dateDay_schemaId_serverId_cascadeValue")).
				groupBy(new Fields("dateDay_schemaId_serverId_cascadeValue")).name("dCountGroupBy").
				persistentAggregate(dayStateFactory, new Count(), new Fields("count")).
				newValuesStream().name("dcountPersistent").
				each(new Fields("dateDay_schemaId_serverId_cascadeValue", "count"),
						new ReadRedisDayData2HBaseFunction(), new Fields()).parallelismHint(3);
		
		//minute count
		countSplitStream.groupBy(new Fields("dateTime_schemaId_serverId_cascadeValue")).name("mCountGroupBy")		
				.persistentAggregate(minuteStateFactory, new Count(), new Fields("count"))
				.newValuesStream().name("countPersistent")
				.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "count"), 
						new RedisStoreFunction(clusterConfig), new Fields()).parallelismHint(3);	// 保存分钟数据
		
		//sum
		Stream sumStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("sum");
		Stream sumSplitStream = sumStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpSumFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new BaseCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.project(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"));
		//minute sum
		sumSplitStream.groupBy(new Fields("dateTime_schemaId_serverId_cascadeValue")).name("sumGroupBy")
				.persistentAggregate(minuteStateFactory, new Fields("value"), new Sum(), new Fields("sum"))
				.newValuesStream().name("msumPersistent")
				.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "sum"), 
						new RedisStoreFunction(clusterConfig), new Fields()).parallelismHint(3);	// 保存分钟数据
		
		//day sum
		sumSplitStream.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"),
				new TimeConvertFunction(2), new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				project(new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				groupBy(new Fields("dateDay_schemaId_serverId_cascadeValue")).name("dSumGroupBy").			
				persistentAggregate(dayStateFactory,new Fields("valueTmp"),new Sum(), new Fields("sum")).
				newValuesStream().name("dsumPersistent").
				each(new Fields("dateDay_schemaId_serverId_cascadeValue", "sum"), 
						new ReadRedisDayData2HBaseFunction(), new Fields()).parallelismHint(3);
		
		// max done
		Stream maxStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("max");
		Stream maxSplitStream = maxStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpMaxFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new BaseCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.project(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"));		
		
		 //minute max		
		maxSplitStream.groupBy(new Fields("dateTime_schemaId_serverId_cascadeValue")).name("maxGroupBy")
				.persistentAggregate(minuteStateFactory, new Fields("value"), new Max(), new Fields("max"))
				.newValuesStream().name("mmaxPersistent")
				.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "max"), 
						new RedisStoreFunction(clusterConfig), new Fields()).parallelismHint(3);// 保存分钟数据
		//day max
		maxSplitStream.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"),
				new TimeConvertFunction(2), new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				project(new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				groupBy(new Fields("dateDay_schemaId_serverId_cascadeValue")).name("dMaxGroupBy").			
				persistentAggregate(dayStateFactory,new Fields("valueTmp"), new Max(), new Fields("max")).
				newValuesStream().name("dmaxPersistent").
				each(new Fields("dateDay_schemaId_serverId_cascadeValue", "max"), 
						new ReadRedisDayData2HBaseFunction(), new Fields()).parallelismHint(3);
		
		//min done
		Stream minStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("min");
		Stream minSplitStream = minStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpMinFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new BaseCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.project(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"));
		
		//minute min
		minSplitStream.groupBy(new Fields("dateTime_schemaId_serverId_cascadeValue")).name("minGroupBy")
				.persistentAggregate(minuteStateFactory, new Fields("value"), new Min(), new Fields("min"))
				.newValuesStream().name("mminPersistent")
				.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "min"), 
						new RedisStoreFunction(clusterConfig), new Fields()).parallelismHint(3);	// 保存分钟数据
		
		//day min
		minSplitStream.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"),
				new TimeConvertFunction(2), new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				project(new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				groupBy(new Fields("dateDay_schemaId_serverId_cascadeValue")).name("dMinGroupBy").			
				persistentAggregate(dayStateFactory,new Fields("valueTmp"),new Min(), new Fields("min")).
				newValuesStream().name("dminPersistent").
				each(new Fields("dateDay_schemaId_serverId_cascadeValue", "min"), 
						new ReadRedisDayData2HBaseFunction(), new Fields()).parallelismHint(3);
		
		//assign done
		Stream assignStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("assign");
		Stream assignSplitStream = assignStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpAssignFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new BaseCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue", "value"));
		//minute assign
		assignSplitStream.each(new Fields("dateTime_schemaId_serverId_cascadeValue","value"),
						new RedisStoreFunction(clusterConfig), new Fields()).parallelismHint(3);	// 保存分钟数据
		
		//day assign
		assignSplitStream.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"),
				new TimeConvertFunction(2), new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				project(new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				each(new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp"),
						new ReadRedisDayData2HBaseFunction(), new Fields()).parallelismHint(3);
		
		Config conf = new Config();
		conf.setMessageTimeoutSecs(60);
		
		if(properties.get("running-mode") == null || properties.get("running-mode").equals("cluster")){
			try {
				StormSubmitter.submitTopology(properties.getProperty("commonTopicName"), conf,topology.build());
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		}else if(properties.get("running-mode").equals("local")){
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(properties.getProperty("commonTopicName"), conf, topology.build());
		}
    }

	private static JedisClusterConfig getClusterConfig(String redisHosts) {
		Set<InetSocketAddress> nodes = new HashSet<InetSocketAddress>();
		for (String hostPort : redisHosts.split(",")) {
			String[] host_port = hostPort.split(":");
			nodes.add(new InetSocketAddress(host_port[0], Integer.valueOf(host_port[1])));
		}
		return new JedisClusterConfig.Builder().setNodes(nodes).build();
	}

	private static Options<OpaqueValue> getOptions(RedisDataTypeDescription dataTypeDescription, int expireSeconds) {
		Options<OpaqueValue> opts = new Options<OpaqueValue>();
		opts.dataTypeDescription = dataTypeDescription;
		opts.expireIntervalSec = expireSeconds; // 设置分钟数据在redis中的过期时间。
		return opts;
	}
}
