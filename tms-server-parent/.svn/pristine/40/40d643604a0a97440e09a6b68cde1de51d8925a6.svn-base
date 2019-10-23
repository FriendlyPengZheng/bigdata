package com.taomee.tms.storm.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.apache.storm.testing.TupleCaptureBolt;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.builtin.Count;
//import org.apache.storm.trident.operation.builtin.Sum;
import org.apache.storm.trident.state.OpaqueValue;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.tuple.Fields;

import com.taomee.bigdata.lib.TmsProperties;
import com.taomee.tms.storm.aggregator.Max;
import com.taomee.tms.storm.aggregator.Sum;
import com.taomee.tms.storm.filter.OpAssignFilter;
import com.taomee.tms.storm.filter.OpCountFilter;
import com.taomee.tms.storm.filter.OpMaxFilter;
import com.taomee.tms.storm.filter.OpSumFilter;
import com.taomee.tms.storm.function.BaseCacheParamGenFunction;
import com.taomee.tms.storm.function.CountCacheParamGenFunction;
import com.taomee.tms.storm.function.LogSplitFunctionCustom2;
import com.taomee.tms.storm.function.ReadRedisDayData2HBaseFunction;
import com.taomee.tms.storm.function.StidSStidGidRefLogIdFunction;
import com.taomee.tms.storm.function.TimeConvertFunction;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class RealtimeStatDiffClientIdConsumer 
{
	private static final Logger LOG = LoggerFactory.getLogger(RealtimeStatDiffClientIdConsumer.class);
	
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
        	tridentKafkaConf = new TridentKafkaConfig(new ZkHosts("10.1.1.187:2181,10.1.1.176:2181,10.1.1.175:2181"), // 配置brokers地址 ,默认值/brokers/
        			"storm-custom-local-test", //配置topic
        			"storm-local-custom-test-eclipse"); //配置consumer group
        }else{
        	tridentKafkaConf = new TridentKafkaConfig(new ZkHosts(properties.getProperty("zkHosts")), // 配置brokers地址 ,默认值/brokers/
        			properties.getProperty("customizeTopicName"), //配置topic
        			properties.getProperty("kafaCustomizeGroupName")); //配置consumer group
        }
        
		
		if(properties.get("running-mode") != null && properties.get("running-mode").equals("local")){
			tridentKafkaConf.ignoreZkOffsets = true;
			tridentKafkaConf.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
			/*通过此配置项强制每次本地调试时从topic的最新offset处读取
			如果不配置此项则在LocalCluster模式下总是从开头处的offset读取，容易导致在结果数据持久化阶段因为输入的txid与state里存储的当前txid不一致从而不予更新并报异常退出*/
		}
		
		 /*增加吞吐量的方式包括（http://stackoverflow.com/questions/24510456/performance-issues-kafka-storm-trident-opaquetridentkafkaspout）：
		 - 有多少个partition就设置多少个spout
		 - topology.max.spout.pending：同时活跃的batch数量，你必须设置同时处理的batch数量，如果你不指定，默认是1。
		 - tridentConfig.fetchSizeBytes：
		 - topology.trident.batch.emit.interval.millis
		 - topology.message.timeout.secs
		 - use G1GC garbage collection*/
		
		tridentKafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme()); // 默认输入的tuple名"str"
//		kafkaConf.forceFromStart = true;

		OpaqueTridentKafkaSpout kafkaSpout = new OpaqueTridentKafkaSpout(tridentKafkaConf);
		
		new TopologyBuilder().setBolt("", new TupleCaptureBolt()).setNumTasks(1);
		
		StateFactory factory_day = RedisClusterMapState.opaque(getJedisClusterConfig(properties.getProperty("redisHosts")), getOptions());
		
//        //set parallelismHint() and partitionBy() to split stream.
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
        
		Stream trunkStream = topology.newStream(properties.getProperty("customizeStreamName"), kafkaSpout).parallelismHint(2).
				each(tridentKafkaConf.scheme.getOutputFields(),
						new StidSStidGidRefLogIdFunction(properties),
						new Fields("oldLogTranNewLog")).project(new Fields("oldLogTranNewLog"))
				.each(new Fields("oldLogTranNewLog"),
						new LogSplitFunctionCustom2(properties),
						new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("logSplit");
		
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
				persistentAggregate(factory_day, new Count(), new Fields("count")).
				newValuesStream().name("dcountPersistent").
				each(new Fields("dateDay_schemaId_serverId_cascadeValue", "count"),
						new ReadRedisDayData2HBaseFunction(), new Fields()).parallelismHint(3);
		
		Stream sumStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("sum");
		Stream sumSplitStream = sumStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpSumFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"),new BaseCacheParamGenFunction(),new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.project(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"));
		
		//day sum
		sumSplitStream.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"),
				new TimeConvertFunction(2), new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				project(new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				groupBy(new Fields("dateDay_schemaId_serverId_cascadeValue")).name("dSumGroupBy").		
				persistentAggregate(factory_day,new Fields("valueTmp"),new Sum(), new Fields("sum")).
				newValuesStream().name("dsumPersistent").
				each(new Fields("dateDay_schemaId_serverId_cascadeValue", "sum"),
						new ReadRedisDayData2HBaseFunction(), new Fields()).parallelismHint(3);
		
		Stream maxStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("max");
		Stream maxSplitStream = maxStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpMaxFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new BaseCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.project(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"));		
		
		//day max
		maxSplitStream.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"),
				new TimeConvertFunction(2), new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				project(new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
				groupBy(new Fields("dateDay_schemaId_serverId_cascadeValue")).name("dMaxGroupBy").
				persistentAggregate(factory_day,new Fields("valueTmp"), new Max(), new Fields("max")).
				newValuesStream().name("dmaxPersistent").
				each(new Fields("dateDay_schemaId_serverId_cascadeValue", "max"),
						new ReadRedisDayData2HBaseFunction(), new Fields()).parallelismHint(3);
		
//		//day min
//		Stream minStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("min");
//		Stream minSplitStream = minStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpMinFilter())
//				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
//				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"),
//						new BaseCacheParamGenFunction(),
//						new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
//				.project(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"));
//		
//		//day min
//		minSplitStream.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"),
//				new TimeConvertFunction(2), new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
//				project(new Fields("dateDay_schemaId_serverId_cascadeValue","valueTmp")).
//				groupBy(new Fields("dateDay_schemaId_serverId_cascadeValue")).name("dMinGroupBy").
//				persistentAggregate(factory_day,new Fields("valueTmp"),new Min(), new Fields("min")).
//				newValuesStream().name("dminPersistent").
//				each(new Fields("dateDay_schemaId_serverId_cascadeValue", "min"), 
//						new ReadRedisDayData2HBaseFunction(), new Fields()).parallelismHint(3);
		
		//assign
		Stream assignStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("assign");
		Stream assignSplitStream = assignStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpAssignFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new BaseCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue", "value"));
		
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
				StormSubmitter.submitTopology(properties.getProperty("customizeTopicName"), conf,topology.build());
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		}else if(properties.get("running-mode").equals("local")){//用于在IDE下调试storm，使用LocalCluster，不能用于生产环境！
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(properties.getProperty("customizeTopicName"), conf, topology.build());
		}
    }

    @SuppressWarnings("rawtypes")
	private static Options<OpaqueValue> getOptions() {
		Options<OpaqueValue> opts_day = new Options<OpaqueValue>();
		opts_day.dataTypeDescription = new RedisDataTypeDescription(RedisDataTypeDescription.RedisDataType.STRING, null);
		opts_day.expireIntervalSec = 86400+60*60*2; // 设置天数据在redis中的过期时间 1 day + 2 hour,避免数据过期过早，导致数据还没被计算完成。
		return opts_day;
	}

	private static JedisClusterConfig getJedisClusterConfig(String redisHosts) {
		Set<InetSocketAddress> nodes = new HashSet<InetSocketAddress>();
		for (String hostPort : redisHosts.split(",")) {
			String[] host_port = hostPort.split(":");
			nodes.add(new InetSocketAddress(host_port[0], Integer.valueOf(host_port[1])));
		}
		return new JedisClusterConfig.Builder().setNodes(nodes).build();
	}
}
