package com.taomee.tms.mgr.server.detector;

import java.util.List;

import org.apache.kafka.common.PartitionInfo;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.common.imp.Detector;
import com.taomee.tms.mgr.entity.ServerDiaryInfo;

/**
 * 动态生成Server变化服务日志的服务
 * @author looper
 * @date 2017年6月19日 下午4:15:31
 * @project tms-log-schema-detector ServerNewDetector
 */
public class ServerNewDetector extends Detector<ServerDiaryInfo> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ServerNewDetector.class);
	
	/**
	 * 最后数据需要写zk上的path node
	 */
	private static final String ZOOKEEPER_NODE_NAME="/tms/server-detector";
	/**
	 * 利用spring注入，实例化该bean
	 */
	private LogMgrService logMgrService;
	public void setLogMgrService(LogMgrService logMgrService) {
		this.logMgrService = logMgrService;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		//
		/**
		 * @author looper
		 * @date 2017年6月19日 下午4:14:35
		 * @body_statement
		 * 
		 */
		LOG.info("新的实时更新serverInfo服务开启...");
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
					

						String changeLog = GenChangeLog(serverDiaryInfo);
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

	@Override
	protected boolean GetLastUpdatedRecordIdFromZookeeper() {
		// TODO Auto-generated method stub
		// return false;
		/**
		 * @author looper
		 * @date 2017年6月19日 下午4:14:35
		 * @body_statement return false;
		 */
		String strResult = null;
		try {
			// byte[] bytes = zooKeeper.getData(ZOOKEEPER_NODE_NAME, null,
			// null);
			/**
			 * 需要判断Zookeeper上的节点是否存在，注意是该程序第一次初始化的时候，在zk上生成对于的path node
			 */
			if(zkClientUtil.givedPathIsexists(ZOOKEEPER_NODE_NAME) == false)
			{
				zkClientUtil.recursiveCreateNode(ZOOKEEPER_NODE_NAME, "0".getBytes());
			}
			byte[] bytes = zkClientUtil.getData(ZOOKEEPER_NODE_NAME);
			strResult = new String(bytes);
			System.out.println("strResult:" + strResult);
			lastUpdatedRecordId = Integer.parseInt(strResult.trim());
			System.out.println("lastUpdatedRecordId:" + lastUpdatedRecordId);
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

		if (lastUpdatedRecordId < 0) {
			LOG.error("ServerDetector GetLastUpdatedRecordIdFromZookeeper failed, get lastUpdatedRecordId ["
					+ lastUpdatedRecordId + "] from zookeeper <0");
			return false;
		}

		return true;
	}

	@Override
	protected String GenChangeLog(ServerDiaryInfo serverDiaryInfo) {
		// TODO Auto-generated method stub
		// return null;
		/**
		 * @author looper
		 * @date 2017年6月19日 下午4:14:35
		 * @body_statement return null;
		 */
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

		if (serverDiaryInfo.getParentId() < 0) {
			LOG.error("ServerDetector GenServerInfoChangeLog, or ParentId < 0");
			return null;
		}

		if (serverDiaryInfo.getGameId() < 0) {
			LOG.error("GameId < 0");
			return null;
		}

		StringBuffer buf = new StringBuffer();
		/**
		 * 这边根据com.taomee.tms.mgr.core.loganalyser.RealtimeLogAnalyser.
		 * UpdateConfig的处理流程 设置日志格式以及logid信息
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
			// _setserverid_
			break;
		}
		default:
			LOG.error("ServerDetector GenSchemaChangeLog, unknown curd "
					+ serverDiaryInfo.getCurd());
			return null;
		}

		return buf.toString();
	}

	@Override
	protected void PushLogToKafka(String changeLog) {
		// TODO Auto-generated method stub
		//
		/**
		 * @author looper
		 * @date 2017年6月19日 下午4:14:35
		 * @body_statement
		 */
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

	@Override
	protected void SetLastUpdatedRecordIntoToZookeeper() {
		// TODO Auto-generated method stub
		//
		/**
		 * @author looper
		 * @date 2017年6月19日 下午4:14:35
		 * @body_statement
		 */
		while (true) {
			try {
				// result = zooKeeper.create(ZOOKEEPER_NODE_NAME,
				// "10".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				// zooKeeper.setData(ZOOKEEPER_NODE_NAME,Integer.toString(lastUpdatedRecordId).getBytes(),
				// -1);
				zkClientUtil.setData(ZOOKEEPER_NODE_NAME,
						Integer.toString(lastUpdatedRecordId).getBytes());
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

}
