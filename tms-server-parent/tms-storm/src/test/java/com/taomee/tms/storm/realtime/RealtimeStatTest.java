package com.taomee.tms.storm.realtime;

//import storm.trident.*;
//import backtype.storm.Config;
//import backtype.storm.StormSubmitter;
//import backtype.storm.generated.AlreadyAliveException;
//import backtype.storm.generated.AuthorizationException;
//import backtype.storm.generated.Grouping;
//import backtype.storm.generated.InvalidTopologyException;
//import backtype.storm.grouping.CustomStreamGrouping;
//import backtype.storm.spout.SchemeAsMultiScheme;
//import backtype.storm.tuple.Fields;
//import storm.kafka.StringScheme;
//import storm.kafka.ZkHosts;
//import storm.kafka.trident.*;
//import storm.trident.operation.Aggregator;
//import storm.trident.operation.builtin.Count;
//import storm.trident.operation.builtin.Sum;
//import storm.trident.state.OpaqueValue;
//import storm.trident.state.StateFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.Grouping;
import org.apache.storm.generated.Grouping._Fields;
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
//import org.apache.storm.trident.operation.builtin.Sum;
import org.apache.storm.trident.state.OpaqueValue;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.tuple.Fields;









import com.taomee.tms.storm.filter.OpCountFilter;
import com.taomee.tms.storm.function.CountCacheParamGenFunction;
import com.taomee.tms.storm.function.LogSplitFunction;
import com.taomee.tms.storm.function.RedisStoreFunction;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * account monitor topology
 *
 */
public class RealtimeStatTest 
{
	/**
	 * 提交的配置参数
	 */
	/**
	 * 从kafka那边读取的参数
	 */
	static final String TOPIC_NAME = "tms-online-2";
	
	static final String STREAM_NAME = "tms-realtime-stream";
	
	/**
	 * 提交到storm集群的topology的名称
	 */
	static final String TOPO_NAME = "tms-realtime-stream-topology";
	
	
	//static final String STATE_DATA_NAME = "tms-realtime-stat-state-data";
	
	static final String zkHosts = "10.1.1.35:2181,10.1.1.153:2181,10.1.1.151:2181";
	static final String redisHosts = "10.1.1.35:6379,10.1.1.153:6379,10.1.1.151:6379";
	
	private static final Logger LOG = LoggerFactory.getLogger(RealtimeStatTest.class);
	
    public static void main( String[] args ) throws AlreadyAliveException, InvalidTopologyException
    {
        TridentTopology topology = new TridentTopology();
        
        /*
		 * kafka broker list and partitions will be gotten from default
		 * zkPath(/brokers/).
		 */
		TridentKafkaConfig kafkaConf = new TridentKafkaConfig(new ZkHosts(
				zkHosts), // default zkPath /brokers/
				TOPIC_NAME, "tms-client"); // topic
		
		// storm-kafka内置的类，从kafka中读取数据，作为字符串处理并在tuple中输入一个默认名称为"str"的字段
		// 增加吞吐量的方式包括（http://stackoverflow.com/questions/24510456/performance-issues-kafka-storm-trident-opaquetridentkafkaspout）：
		// - 有多少个partition就设置多少个spout
		// - topology.max.spout.pending：同时活跃的batch数量，你必须设置同时处理的batch数量，如果你不指定，默认是1。
		// - tridentConfig.fetchSizeBytes：
		// - topology.trident.batch.emit.interval.millis
		// - topology.message.timeout.secs
		// - use G1GC garbage collection
		
		kafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme()); // default fields named "str"
		// kafkaConf.forceFromStart = true;

		OpaqueTridentKafkaSpout kafkaSpout = new OpaqueTridentKafkaSpout(
				kafkaConf);

		Set<InetSocketAddress> nodes = new HashSet<InetSocketAddress>();
		for (String hostPort : redisHosts.split(",")) {
			String[] host_port = hostPort.split(":");
			nodes.add(new InetSocketAddress(host_port[0], Integer
					.valueOf(host_port[1])));
		}
		JedisClusterConfig clusterConfig = new JedisClusterConfig.Builder()
				.setNodes(nodes).build();
		// RedisDataTypeDescription dataTypeDescription = new
		// RedisDataTypeDescription(
		// RedisDataTypeDescription.RedisDataType.HASH,
		// STATE_DATA_NAME);
		RedisDataTypeDescription dataTypeDescription = new RedisDataTypeDescription(
				RedisDataTypeDescription.RedisDataType.STRING, null);
		
		@SuppressWarnings("rawtypes")
		Options<OpaqueValue> opts = new Options<OpaqueValue>();
		opts.dataTypeDescription = dataTypeDescription;
		opts.expireIntervalSec = 300; // 设置分钟数据在redis中的过期时间。
		StateFactory factory = RedisClusterMapState.opaque(clusterConfig, opts);
		       
		Stream trunkStream = topology.newStream(STREAM_NAME, kafkaSpout).parallelismHint(2)		// 后续改为读取相应kafka中topic，有多少partition就设置多少并行度
				.each(kafkaConf.scheme.getOutputFields(),
						new LogSplitFunction(),
						new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("logSplit");
		//trunkStream.partition(new Grouping(_Fields.FIELDS,"10"));
		//trunkStream.partitionAggregate(inputFields, agg, functionFields);
		//trunkStream.aggregate(inputFields, agg, functionFields);
		//trunkStream.persistentAggregate(stateFactory, agg, functionFields);
		
		// count done
		Stream countStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("count");
		countStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpCountFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new CountCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue"))
				.project(new Fields("dateTime_schemaId_serverId_cascadeValue"))
				.groupBy(new Fields("dateTime_schemaId_serverId_cascadeValue")).name("countGroupBy")
				.persistentAggregate(factory, new Count(), new Fields("count"))
				.newValuesStream().name("countPersistent")
				.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "count"), 
						new RedisStoreFunction(clusterConfig), new Fields()).parallelismHint(3);	// 保存分钟数据
		
		// sum done
		/*Stream sumStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("sum");
		sumStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpSumFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new BaseCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.project(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.groupBy(new Fields("dateTime_schemaId_serverId_cascadeValue")).name("sumGroupBy")
				.persistentAggregate(factory, new Fields("value"), new Sum(), new Fields("sum"))
				.newValuesStream().name("sumPersistent")
				.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "sum"), 
						new RedisStoreFunction(clusterConfig), new Fields()).parallelismHint(3);*/	// 保存分钟数据
		
		// max done
		/*Stream maxStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("max");
		maxStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpMaxFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new BaseCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.project(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.groupBy(new Fields("dateTime_schemaId_serverId_cascadeValue")).name("maxGroupBy")
				.persistentAggregate(factory, new Fields("value"), new Max(), new Fields("max"))
				.newValuesStream().name("maxPersistent")
				.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "max"), 
						new RedisStoreFunction(clusterConfig), new Fields()).parallelismHint(3);*/	// 保存分钟数据
		
		// min done
		/*Stream minStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("min");
		minStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpMinFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new BaseCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.project(new Fields("dateTime_schemaId_serverId_cascadeValue", "value"))
				.groupBy(new Fields("dateTime_schemaId_serverId_cascadeValue")).name("minGroupBy")
				.persistentAggregate(factory, new Fields("value"), new Min(), new Fields("min"))
				.newValuesStream().name("minPersistent")
				.each(new Fields("dateTime_schemaId_serverId_cascadeValue", "min"), 
						new RedisStoreFunction(clusterConfig), new Fields()).parallelismHint(3);*/	// 保存分钟数据
		
		// assign done
		/*Stream assignStream = trunkStream.project(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime")).name("assign");
		assignStream.each(new Fields("op", "schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), new OpAssignFilter())
				.project(new Fields("schemaId", "serverId", "cascadeValue",	"opValues", "dateTime"))
				.each(new Fields("schemaId", "serverId", "cascadeValue", "opValues", "dateTime"), 
						new BaseCacheParamGenFunction(),
						new Fields("dateTime_schemaId_serverId_cascadeValue", "value")).name("assignPersistent")
				.each(new Fields("dateTime_schemaId_serverId_cascadeValue",	"value"),
						new RedisStoreFunction(clusterConfig), new Fields()).parallelismHint(3);*/	// 保存分钟数据

		Config conf = new Config();
		try {
			StormSubmitter.submitTopology(TOPO_NAME, conf,
					topology.build());
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			LOG.error("submitTopology failed, " + e.toString());
			e.printStackTrace();
		}
    }
}
