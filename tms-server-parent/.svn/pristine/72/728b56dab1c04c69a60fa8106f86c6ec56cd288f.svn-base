package com.taomee.tms.mgr.schema.detector;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.producer.ProducerConfig;

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.taomee.tms.mgr.core.opanalyser.BaseOpAnalyser;
import com.taomee.tms.mgr.core.opanalyser.OpAnalyserFactory;
import com.taomee.tms.mgr.core.schemaanalyser.PlainSchemaAnalyser;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.SchemaDiaryInfo;
import com.taomee.tms.mgr.api.LogMgrService;

public class SchemaDetector {
	private static final Logger LOG = LoggerFactory
			.getLogger(SchemaDetector.class);
	// 初始化时opId的三种获取方式：
	// 1. 通过zookeeper永久节点获取，在每次循环结束后会将当期处理完毕的最大opId写入此zookeeper永久节点（不需要隔一段时间写入）。
	// 2. 启动时zookeeper中必须要有次节点，否则启动失败。
	// 3. 每次只更新一条修改记录，以保证kafka中的修改记录，最多重复一个。
	// （废弃）2. 若zookeeper中没有此永久节点，表示程序首次启动，若不带指定参数程序会启动失败。
	// （废弃）3.
	// 当zookeeper中没有此永久节点，必须通过指定参数设置opId来冷启动，通过指定参数启动时不会先去读取zookeeper中的永久节点。启动后会会和步骤一一致，进入正常状态。
	// （废弃）4. 可通过指定参数，通过协议获取数据库中最大opId信息，并不启动程序（可以通过这样获取到的最大opId来指定opId启动）
	private static final String KAFKA_TOPIC = "tms-online-test";
	// TODO 是否可以设置成zookeeper
	private static final String KAFKA_BROKERS = "10.1.1.35:9092,10.1.1.151:9093,10.1.1.153:9094";
	private static final String KAFKA_CLIENT_ID = "tms-schema-detector";
	private static final int LOOP_INTERVAL_MS = 2000;
	private static final String ZOOKEEPER_NODE_NAME = "/tms/schema-detector";
	private static final String ZOOKEEPER_HOSTS = "10.1.1.35:2181,10.1.1.153:2181,10.1.1.151:2181";

	private LogMgrService logMgrService;
	private int lastUpdatedRecordId = -1;
//	private List<SchemaDiaryInfo> schemaDiaryInfoList = null;
	private ZooKeeper zooKeeper = null;
	private KafkaSyncProducer kafkaSyncProducer = null;
	private Watcher watcher = new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			LOG.info("SchemaDetector zookeeper watcher process: ",
					event.getType());
		}
	};

	private static final int SESSION_TIMEOUT = 30000;

	public void setLogMgrService(LogMgrService logMgrService) {
		this.logMgrService = logMgrService;
	}

	public boolean Init() {
		// 初始化zookeeper
		if (!InitZookeeper()) {
			LOG.error("SchemaDetector Init, InitZookeeper failed");
			return false;
		}
		
		LOG.info("SchemaDetector Init, zookeeper init success");

		if (!InitKafka()) {
			LOG.error("SchemaDetector Init, InitKafka failed");
			return false;
		}
		
		LOG.info("SchemaDetector Init, kafka init success");

		// 从zookeeper中获取最后修改记录id，后续会作为参数从数据库拉取此id后所有的修改
		if (!GetLastUpdatedRecordIdFromZookeeper()) {
			LOG.error("SchemaDetector Init, GetLastUpdatedRecordIdFromZookeeper failed");
			return false;
		}

		return true;
	}

	private boolean InitZookeeper() {
		// 连接zookeeper
		try {
			// TODO 没有close
			zooKeeper = new ZooKeeper(ZOOKEEPER_HOSTS, SESSION_TIMEOUT,
					watcher);
		} catch (IOException ex) {
			LOG.info("SchemaDetector Init, IOException occured when connect zookeeper, "
					+ ex.getMessage());
			return false;
		} catch (Exception ex) {
			LOG.info("SchemaDetector Init, unknown exception occured when connect zookeeper, "
					+ ex.getMessage());
			return false;
		}

		return true;
	}

	private boolean InitKafka() {
		try {
			kafkaSyncProducer = new KafkaSyncProducer(
					KAFKA_TOPIC, KAFKA_BROKERS, KAFKA_CLIENT_ID);
		} catch (KafkaException ex) {
			LOG.info("SchemaDetector InitKafka, KafkaException occured, " + ex.getMessage());
			return false;
		} catch (Exception ex) {
			LOG.info("SchemaDetector InitKafka, unknown exception occured, " + ex.getMessage());
			return false;
		}
		return true;
	}

	public void start() {
		if (!Init()) {
			LOG.error("SchemaDetector start, Init failed");
			return;
		}

		Long lastTimestamp = System.currentTimeMillis();
		while (true) {
			List<SchemaDiaryInfo> schemaDiaryInfoList = null;

			// 获取schema修改记录
			try {
				schemaDiaryInfoList = logMgrService
						.getSchemaDiaryInfoBydiaryId(lastUpdatedRecordId);
			} catch (Exception ex) {
				LOG.error("SchemaDetector loop, logMgrService getSchemaDiaryInfoBydiaryId failed, "
						+ ex.getMessage());
			}

			if (schemaDiaryInfoList == null || schemaDiaryInfoList.size() == 0) {
				LOG.info("SchemaDetector loop, logMgrService getSchemaDiaryInfoBydiaryId get null list");
			} else {
				for (SchemaDiaryInfo schemaDiaryInfo : schemaDiaryInfoList) {
					if (schemaDiaryInfo == null || schemaDiaryInfo.getDiaryId() == null) {
						LOG.error("SchemaDetector loop, null schemaDiaryInfo or DiaryId");
						continue;
					}
					
					if (schemaDiaryInfo.getDiaryId().intValue() <= lastUpdatedRecordId) {
						LOG.error("SchemaDetector loop, DiaryId in schemaDiaryInfo " + schemaDiaryInfo.getDiaryId().intValue() + " < lastUpdatedRecordId" + lastUpdatedRecordId);
						continue;
					}
					SetLastUpdatedRecordId(schemaDiaryInfo.getDiaryId().intValue());

					do {
						// 借助于已有的方法来做验证
						SchemaInfo schemaInfo = new SchemaInfo();
						schemaInfo.setLogId(schemaDiaryInfo.getLogId());
						schemaInfo.setSchemaId(schemaDiaryInfo.getSchemaId());
						schemaInfo.setOp(schemaDiaryInfo.getOp());
						schemaInfo.setCascadeFields(schemaDiaryInfo
								.getCascadeFields());
	
						// 先处理op，看是否合法
						BaseOpAnalyser opAnalyser = OpAnalyserFactory
								.createOpAnalyser(schemaDiaryInfo.getOp());
						if (opAnalyser == null) {
							LOG.error("SchemaDetector loop, OpAnalyserFactory create null OpAnalyser, schemaInfo is "
									+ schemaInfo.toString());
							break;
						}
	
						// 过滤掉不需要实时计算的
						if (!opAnalyser.IsRealtime()) {
							LOG.info("SchemaDetector loop, opAnalyser IsRealTime false");
							break;
						}
	
						PlainSchemaAnalyser newSchemaAnalyser = new PlainSchemaAnalyser();
						if (!newSchemaAnalyser.Init(schemaInfo, opAnalyser)) {
							LOG.error("SchemaDetector loop, PlainSchemaAnalyser Init failed, schemaInfo is "
									+ schemaInfo.toString());
							break;
						}
	
						String changeLog = GenSchemaChangeLog(schemaDiaryInfo);
						if (changeLog == null) {
							// 此错误可能会导致新的实时schema数据丢失
							LOG.error("SchemaDetector loop, GenSchemaChangeLog failed, schemaDiaryInfo is "
									+ schemaDiaryInfo.toString());
							break;
						}
	
						LOG.info("SchemaDetector loop, GenSchemaChangeLog: "
								+ changeLog);
						// 写入kafka数据topic的所有partition，必须全部写成功，否则无限循环写
						PushLogToKafka(changeLog);
					} while (false);

					// 此时kafka一定已经写成功了
					// zookeeper也必须要写成功，否则无限循环写
					SetLastUpdatedRecordIntoToZookeeper();
				}
			}

			// 计算需要sleep的时间，保持2秒钟循环一次
			Long timeToSleepMs = LOOP_INTERVAL_MS
					- (System.currentTimeMillis() - lastTimestamp);
			LOG.info("SchemaDetector start, time to sleep is "
					+ timeToSleepMs.toString() + "ms");

			if (timeToSleepMs > 0) {
				SafeSleep(timeToSleepMs);
			}

			// 获取当前毫秒时间戳
			lastTimestamp = System.currentTimeMillis();
		}
	}

	private String GenSchemaChangeLog(SchemaDiaryInfo schemaDiaryInfo) {
		// 进入时schemaDiaryInfo不为null，切op合法
		if (schemaDiaryInfo.getCurd() == null) {
			LOG.error("SchemaDetector GenSchemaChangeLog, null curd");
			return null;
		}

		if (schemaDiaryInfo.getSchemaId() == null
				|| schemaDiaryInfo.getSchemaId().intValue() <= 0) {
			LOG.error("SchemaDetector GenSchemaChangeLog, schemaId null or schemaId < 0");
			return null;
		}

		StringBuffer buf = new StringBuffer();
		switch (schemaDiaryInfo.getCurd()) {
		case 1: {
			// 增加
			buf.append("_logid_=-1 _setlogid=");
			buf.append(schemaDiaryInfo.getLogId());
			buf.append(" _setschemaid_=");
			buf.append(schemaDiaryInfo.getSchemaId());
			buf.append(" _setop_=");
			buf.append(schemaDiaryInfo.getOp());
			buf.append(" _setcascadefields_=");
			buf.append(schemaDiaryInfo.getCascadeFields());
			break;
		}
		case 2: {
			// 修改
			buf.append("_logid_=-2 _setlogid=");
			buf.append(schemaDiaryInfo.getLogId());
			buf.append(" _setschemaid_=");
			buf.append(schemaDiaryInfo.getSchemaId());
			buf.append(" _setop_=");
			buf.append(schemaDiaryInfo.getOp());
			buf.append(" _setcascadefields_=");
			buf.append(schemaDiaryInfo.getCascadeFields());
			break;
		}
		case 3: {
			// 删除
			buf.append("_logid_=-3 _setlogid=");
			buf.append(schemaDiaryInfo.getLogId());
			buf.append(" _setschemaid_=");
			buf.append(schemaDiaryInfo.getSchemaId());
			break;
		}
		default:
			LOG.error("SchemaDetector GenSchemaChangeLog, unknown curd "
					+ schemaDiaryInfo.getCurd());
			return null;
		}

		return buf.toString();
	}

	private void SetLastUpdatedRecordId(int lastUpdatedRecordId) {
		this.lastUpdatedRecordId = lastUpdatedRecordId;
	}

	// 从zookeeper中获取lastUpdatedRecordId，只会在初始化中调用
	private boolean GetLastUpdatedRecordIdFromZookeeper() {
		String strResult = null;
		try {
			byte[] bytes = zooKeeper.getData(ZOOKEEPER_NODE_NAME, null, null);
			strResult = new String(bytes);
			lastUpdatedRecordId = Integer.parseInt(strResult);
		} catch (NullPointerException ex) {
			LOG.error("SchemaDetector GetLastUpdatedRecordIdFromZookeeper failed, NullPointerException occured");
			return false;
		} catch (NumberFormatException ex) {
			LOG.error("SchemaDetector GetLastUpdatedRecordIdFromZookeeper failed, NumberFormatException occured, strResult is "
					+ strResult);
			return false;
		} catch (KeeperException ex) {
			LOG.error("SchemaDetector GetLastUpdatedRecordIdFromZookeeper failed, KeeperException occured");
			LOG.error("ex.code() " + ex.code());
			LOG.error("ex.toString()" + ex.toString());
			LOG.error("ex.getMessage()" + ex.getMessage());
		} catch (Exception ex) {
			LOG.error("SchemaDetector GetLastUpdatedRecordIdFromZookeeper failed, "
					+ ex.getMessage());
			return false;
		}

		if (lastUpdatedRecordId < 0) {
			LOG.error("SchemaDetector GetLastUpdatedRecordIdFromZookeeper failed, get lastUpdatedRecordId ["
					+ lastUpdatedRecordId + "] from zookeeper <0");
			return false;
		}

		return true;
	}

	private void SafeSleep(Long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LOG.error("SchemaDetector start, InterruptedException catched");
		}
	}

	// 在循环中调用，并且直到成功才返回
	private void SetLastUpdatedRecordIntoToZookeeper() {
		while (true) {
			try {
				// result = zooKeeper.create(ZOOKEEPER_NODE_NAME,
				// "10".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				zooKeeper.setData(ZOOKEEPER_NODE_NAME,
						Integer.toString(lastUpdatedRecordId).getBytes(), -1);
			} catch (Exception ex) {
				LOG.error("SchemaDetector SetLastUpdatedRecordIntoToZookeeper, zookeeper create failed, "
						+ ex.getMessage());
				// 失败则sleep 1秒
				SafeSleep(1000L);
				continue;
			}
			// 退出循环
			break;
		}
	}

	// 写入kafka数据topic的所有partition
	private void PushLogToKafka(String changeLog) {
		while (true) {
			List<PartitionInfo> partitionInfos = kafkaSyncProducer.GetTopicPartitions(KAFKA_TOPIC);
			if (partitionInfos == null || partitionInfos.size() == 0) {
				// 没有获取到partition或者partition为空
				LOG.error("SchemaDetector PushLogToKafka, null or empty partitionInfos of topic " + KAFKA_TOPIC);
				SafeSleep(1000L);
				continue;
			}
			
			for (PartitionInfo partitionInfo: partitionInfos) {
				while (true) {
					if (!kafkaSyncProducer.SendMessage(changeLog, partitionInfo.partition())) {
						LOG.error("SchemaDetector PushLogToKafka, SendMessage to partition " + partitionInfo.partition() + " failed");
						SafeSleep(1000L);
						continue;
					}
					// 本partition发送成功
					break;
				}
			}
			// 全部发送成功
			break;
		}
	}

	// 此main函数在运行时并未调用，而是由dubbo的spring容器通过调用Start()函数拉起的
	public static void main(String[] args) {
		Long curTimestamp = System.currentTimeMillis();
		System.out.println("curTimestamp is " + curTimestamp.toString());

		// 初始化完毕后进入无限循环
		// 每次循环首先请求schema修改日志，若没有修改则等待2秒
		// 若有修改，则将此修改构造成日志，写入kafka数据对应的topic的所有partition中，以供所有的storm的LogSplitfunction使用，此日志在离线计算阶段需要忽略掉
		// 一次循环完毕以后会将最后处理成功的opId写入本地配置文件，当然重复操作在storm中是幂等的，只需要保证操作顺序正确即可
		// 倘若有partition写入失败或超时，则反复写入，阻塞后续操作直至成功

		// 程序退出时，先写本地配置文件（暂不实现）

		// System.out.println(logMgrService.getSchemaDiaryInfoBydiaryId(0));

		SchemaDetector schemaDetector = new SchemaDetector();
		SchemaDiaryInfo schemaDiaryInfo = new SchemaDiaryInfo();
		schemaDiaryInfo.setCurd(1);
		schemaDiaryInfo.setLogId(100);
		schemaDiaryInfo.setSchemaId(120);
		
		schemaDiaryInfo.setOp("count()");
		schemaDiaryInfo.setCascadeFields("a|b|c");
		System.out.println(schemaDetector.GenSchemaChangeLog(schemaDiaryInfo));
		schemaDiaryInfo.setCurd(2);
		System.out.println(schemaDetector.GenSchemaChangeLog(schemaDiaryInfo));
		schemaDiaryInfo.setCurd(3);
		System.out.println(schemaDetector.GenSchemaChangeLog(schemaDiaryInfo));

//		if (!schemaDetector.InitZookeeper()) {
//			System.out.println("InitZookeeper failed");
//		}
//
//		schemaDetector.SetLastUpdatedRecordId(30);
//		schemaDetector.SetLastUpdatedRecordIntoToZookeeper();
//		System.out.println("SetLastUpdatedRecordIntoToZookeeper success");
//
//		schemaDetector.GetLastUpdatedRecordIdFromZookeeper();
//		System.out
//				.println("GetLastUpdatedRecordIdFromZookeeper lastUpdatedRecordId is "
//						+ schemaDetector.lastUpdatedRecordId);
		
		System.out.println("-----------------------------------------------------------------------------");
		
//		schemaDetector.InitKafka();
//		schemaDetector.PushLogToKafka("1111111111111111111");
		
		System.out.println("-----------------------------------------------------------------------------");
		// 用于测试
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		context.start();
		LogMgrService logMgrService = (LogMgrService) context
				.getBean("LogMgrService"); // 获取bean
		schemaDetector.setLogMgrService(logMgrService);
		if (schemaDetector.Init()) {
			schemaDetector.start();
		}
	}
}
