package com.taomee.tms.mgr.server.detector;

import java.io.IOException;
import java.util.List;

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.core.opanalyser.BaseOpAnalyser;
import com.taomee.tms.mgr.core.opanalyser.OpAnalyserFactory;
import com.taomee.tms.mgr.core.schemaanalyser.PlainSchemaAnalyser;
import com.taomee.tms.mgr.core.serveridanalyser.ServerIdAnalyzer;
import com.taomee.tms.mgr.entity.SchemaDiaryInfo;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerDiaryInfo;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.schema.detector.KafkaSyncProducer;


/**
 * 
 * @author looper
 * @date 2017年6月9日 下午2:44:46
 * @project tms-log-schema-detector ServerDetector
 */
public class ServerDetector {
	private static final Logger LOG = LoggerFactory
			.getLogger(ServerDetector.class);
	// 初始化时opId的三种获取方式：
	// 1. 通过zookeeper永久节点获取，在每次循环结束后会将当期处理完毕的最大opId写入此zookeeper永久节点（不需要隔一段时间写入）。
	// 2. 启动时zookeeper中必须要有次节点，否则启动失败。
	// 3. 每次只更新一条修改记录，以保证kafka中的修改记录，最多重复一个。
	// （废弃）2. 若zookeeper中没有此永久节点，表示程序首次启动，若不带指定参数程序会启动失败。
	// （废弃）3.
	// 当zookeeper中没有此永久节点，必须通过指定参数设置opId来冷启动，通过指定参数启动时不会先去读取zookeeper中的永久节点。启动后会会和步骤一一致，进入正常状态。
	// （废弃）4. 可通过指定参数，通过协议获取数据库中最大opId信息，并不启动程序（可以通过这样获取到的最大opId来指定opId启动）
	private static final String KAFKA_TOPIC = "tms-online-2";
	// TODO 是否可以设置成zookeeper
	private static final String KAFKA_BROKERS = "10.1.1.35:9092,10.1.1.151:9093,10.1.1.153:9094";
	private static final String KAFKA_CLIENT_ID = "tms-server-detector";
	private static final int LOOP_INTERVAL_MS = 2000;
	private static final String ZOOKEEPER_NODE_NAME = "/tms/server-detector";
	private static final String ZOOKEEPER_HOSTS = "10.1.1.35:2181,10.1.1.153:2181,10.1.1.151:2181";
	private static final int SESSION_TIMEOUT = 30000;
	private LogMgrService logMgrService;
	private int lastUpdatedRecordId = -1;
	// private List<SchemaDiaryInfo> schemaDiaryInfoList = null;
	private ZooKeeper zooKeeper = null;
	private KafkaSyncProducer kafkaSyncProducer = null;
	
	
	public void setLogMgrService(LogMgrService logMgrService) {
		this.logMgrService = logMgrService;
	}
	
	public void start() {
		if (!Init()) {
			LOG.error("ServerDetector start, Init failed");
			return;
		}

		Long lastTimestamp = System.currentTimeMillis();
		while (true) {
			List<ServerDiaryInfo> serverDiaryInfoList = null;
			try {
				serverDiaryInfoList = logMgrService.getServerDiaryInfoBydiaryId(lastUpdatedRecordId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("ServerDetector loop, logMgrService getServerDiaryInfoBydiaryId failed, "
						+ e.getMessage());
			}
			if(serverDiaryInfoList == null)
			{
				LOG.info("ServerDetector loop, logMgrService getServerDiaryInfoBydiaryId get null list");
			}else
			{
				for (ServerDiaryInfo serverDiaryInfo : serverDiaryInfoList)
				{
					if (serverDiaryInfo == null
							|| serverDiaryInfo.getDiaryId() == null) {
						LOG.error("ServerDetector loop, null ServerDiaryInfo or DiaryId");
						continue;
					}
					if (serverDiaryInfo.getDiaryId().intValue() <= lastUpdatedRecordId) {
						LOG.error("ServerDetector loop, DiaryId in serverDiaryInfo "
								+ serverDiaryInfo.getDiaryId().intValue()
								+ " < lastUpdatedRecordId"
								+ lastUpdatedRecordId);
						continue;
					}
					SetLastUpdatedRecordId(serverDiaryInfo.getDiaryId().intValue());
					
					do {
						
						/**
						 * diary_id | curd | server_id | server_name | game_id | parent_id | is_leaf | status | add_time            |
						+----------+------+-----------+-------------+---------+-----------+---------+--------+---------------------+
						|        1 |    1 |        18 | test        |      12 |         1 |       0 |      0 | 2017-02-16 21:16:40
						 */
						/*ServerInfo serverInfo = new ServerInfo();
						serverInfo.setServerId(serverDiaryInfo.getServerId());
						serverInfo.setGameId(serverDiaryInfo.getGameId());
						serverInfo.setParentId(serverDiaryInfo.getParentId());*/
						/**
						 * 暂时这里对severInfo信息的合法性不做判断，先直接发送到kafka当中
						 */
					

						String changeLog = GenServerChangeLog(serverDiaryInfo);
						if (changeLog == null) {
							// 
							LOG.error("ServerDetector loop, GenServerInfoChangeLog failed, serverDiaryInfo is "
									+ serverDiaryInfo.toString());
							break;
						}

						LOG.info("ServerDetector loop, GenServerChangeLog: "
								+ changeLog);
						// 写入kafka数据topic的所有partition，必须全部写成功，否则无限循环写
						PushLogToKafka(changeLog);
					} while (false);

					// 此时kafka一定已经写成功了
					// zookeeper也必须要写成功，否则无限循环写
					SetLastUpdatedRecordIntoToZookeeper();
					
				}
			}
			Long timeToSleepMs = LOOP_INTERVAL_MS
					- (System.currentTimeMillis() - lastTimestamp);
			LOG.info("ServerDetector start, time to sleep is "
					+ timeToSleepMs.toString() + "ms");

			if (timeToSleepMs > 0) {
				SafeSleep(timeToSleepMs);
			}
			// 获取当前毫秒时间戳
			lastTimestamp = System.currentTimeMillis();	
		}
	}

	/**
	 * 初始化zk、kafka、以及从zk服务当中拉取最后更改的ID信息
	 * 
	 * @return
	 */
	public boolean Init() {

		if (!InitZookeeper()) {
			LOG.error("ServerDetector Init, InitZookeeper failed");
			return false;
		}

		LOG.info("ServerDetector Init, zookeeper init success");

		if (!InitKafka()) {
			LOG.error("ServerDetector Init, InitKafka failed");
			return false;
		}

		LOG.info("ServerDetector Init, kafka init success");

		// 从zookeeper中获取最后修改记录id，后续会作为参数从数据库拉取此id后所有的修改
		if (!GetLastUpdatedRecordIdFromZookeeper()) {
			LOG.error("ServerDetector Init, GetLastUpdatedRecordIdFromZookeeper failed");
			return false;
		}

		return true;
	}

	
	private Watcher watcher = new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			LOG.info("ServerDetector zookeeper watcher process: ",
					event.getType());
		}
	};
	
	private boolean InitZookeeper() {
		// 连接zookeeper
		try {
			// TODO 没有close
			zooKeeper = new ZooKeeper(ZOOKEEPER_HOSTS, SESSION_TIMEOUT, watcher);
		} catch (IOException ex) {
			LOG.info("ServerDetector Init, IOException occured when connect zookeeper, "
					+ ex.getMessage());
			return false;
		} catch (Exception ex) {
			LOG.info("ServerDetector Init, unknown exception occured when connect zookeeper, "
					+ ex.getMessage());
			return false;
		}

		return true;
	}

	private boolean InitKafka() {
		try {
			kafkaSyncProducer = new KafkaSyncProducer(KAFKA_TOPIC,
					KAFKA_BROKERS, KAFKA_CLIENT_ID);
		} catch (KafkaException ex) {
			LOG.info("ServerDetector InitKafka, KafkaException occured, "
					+ ex.getMessage());
			return false;
		} catch (Exception ex) {
			LOG.info("ServerDetector InitKafka, unknown exception occured, "
					+ ex.getMessage());
			return false;
		}
		return true;
	}

	// 从zookeeper中获取lastUpdatedRecordId，只会在初始化中调用
	private boolean GetLastUpdatedRecordIdFromZookeeper() {
		String strResult = null;
		try {
			byte[] bytes = zooKeeper.getData(ZOOKEEPER_NODE_NAME, null, null);
			strResult = new String(bytes);
			System.out.println("strResult:"+strResult);
			lastUpdatedRecordId = Integer.parseInt(strResult.trim());
			System.out.println("lastUpdatedRecordId:" +lastUpdatedRecordId);
		} catch (NullPointerException ex) {
			LOG.error("ServerDetector GetLastUpdatedRecordIdFromZookeeper failed, NullPointerException occured");
			return false;
		} catch (KeeperException ex) {
			LOG.error("ServerDetector GetLastUpdatedRecordIdFromZookeeper failed, KeeperException occured");
			LOG.error("ex.code() " + ex.code());
			LOG.error("ex.toString()" + ex.toString());
			LOG.error("ex.getMessage()" + ex.getMessage());
		} catch (Exception ex) {
			LOG.error("ServerDetector GetLastUpdatedRecordIdFromZookeeper failed, "
					+ ex.getMessage());
			return false;
		}

		/*if (lastUpdatedRecordId < 0) {
			LOG.error("ServerDetector GetLastUpdatedRecordIdFromZookeeper failed, get lastUpdatedRecordId ["
					+ lastUpdatedRecordId + "] from zookeeper <0");
			return false;
		}*/

		return true;
	}

	/**
	 * 此方法需要修改
	 * 
	 * @param schemaDiaryInfo
	 * @return
	 */
	private String GenServerChangeLog(ServerDiaryInfo serverDiaryInfo) {
		// 进入时schemaDiaryInfo不为null，切op合法
		if (serverDiaryInfo.getCurd() == null) {
			LOG.error("ServerDetector GenServerChangeLog, null curd");
			return null;
		}

		if (serverDiaryInfo.getServerId() == null
				|| serverDiaryInfo.getServerId().intValue() <= 0) {
			LOG.error("ServerDetector GenServerInfoChangeLog, serverId null or serverId < 0");
			return null;
		}
		
		if(serverDiaryInfo.getParentId() < 0)
		{
			LOG.error("ServerDetector GenServerInfoChangeLog, or ParentId < 0");
			return null;
		}
		
		if(serverDiaryInfo.getGameId() < 0)
		{
			LOG.error("GameId < 0");
			return null;
		}

		StringBuffer buf = new StringBuffer();
		/**
		 * 这边根据com.taomee.tms.mgr.core.loganalyser.RealtimeLogAnalyser.UpdateConfig的处理流程
		 * 设置日志格式以及logid信息
		 */
		switch (serverDiaryInfo.getCurd()) {
		case 1: {
			// 增加
			buf.append("_logid_=-4\t_setserverid_=");
			buf.append(serverDiaryInfo.getServerId());
			buf.append("\t");
			buf.append("_setgameid_=");
			buf.append(serverDiaryInfo.getGameId());
			buf.append("\t");
			buf.append("_setparentid_=");
			buf.append(serverDiaryInfo.getParentId());
			break;
		}
		case 2: {
			// 修改
			break;
		}
		case 3: {
			// 删除
			buf.append("_logid_=-6\t_setserverid_=");
			buf.append(serverDiaryInfo.getServerId());
			//_setserverid_
			break;
		}
		default:
			LOG.error("ServerDetector GenSchemaChangeLog, unknown curd "
					+ serverDiaryInfo.getCurd());
			return null;
		}

		return buf.toString();
	}

	// 写入kafka数据topic的所有partition
	private void PushLogToKafka(String changeLog) {
		while (true) {
			List<PartitionInfo> partitionInfos = kafkaSyncProducer
					.GetTopicPartitions(KAFKA_TOPIC);
			if (partitionInfos == null || partitionInfos.size() == 0) {
				// 没有获取到partition或者partition为空
				LOG.error("ServerDetector PushLogToKafka, null or empty partitionInfos of topic "
						+ KAFKA_TOPIC);
				SafeSleep(1000L);
				continue;
			}

			for (PartitionInfo partitionInfo : partitionInfos) {
				while (true) {
					if (!kafkaSyncProducer.SendMessage(changeLog,
							partitionInfo.partition())) {
						LOG.error("ServerDetector PushLogToKafka, SendMessage to partition "
								+ partitionInfo.partition() + " failed");
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

	// 在循环中调用，并且直到成功才返回
	private void SetLastUpdatedRecordIntoToZookeeper() {
		while (true) {
			try {
				// result = zooKeeper.create(ZOOKEEPER_NODE_NAME,
				// "10".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				zooKeeper.setData(ZOOKEEPER_NODE_NAME,
						Integer.toString(lastUpdatedRecordId).getBytes(), -1);
			} catch (Exception ex) {
				LOG.error("ServerDetector SetLastUpdatedRecordIntoToZookeeper, zookeeper create failed, "
						+ ex.getMessage());
				// 失败则sleep 1秒
				SafeSleep(1000L);
				continue;
			}
			// 退出循环
			break;
		}
	}

	private void SetLastUpdatedRecordId(int lastUpdatedRecordId) {
		this.lastUpdatedRecordId = lastUpdatedRecordId;
	}

	private void SafeSleep(Long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LOG.error("ServerDetector start, InterruptedException catched");
		}
	}

}
