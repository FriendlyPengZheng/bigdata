package com.taomee.tms.mgr.ssgrl.detector;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.PartitionInfo;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.beans.StidSStidRefLogDaily;
import com.taomee.tms.mgr.common.imp.Detector;

/**
 * 往实时那边插入或者删除更新内存里面的stid到logid的映射信息
 * 
 * @author looper
 * @date 2017年7月10日 下午3:12:26
 * @project tms-log-schema-detector StidSstidGidRefLogidDetector
 */
public class StidSstidGidRefLogidDetector extends
		Detector<StidSStidRefLogDaily> {

	private static final Logger LOG = LoggerFactory
			.getLogger(StidSStidRefLogDaily.class);

	/**
	 * 最后数据需要写zk上的path node,写到zk上
	 */
	private static final String ZOOKEEPER_NODE_NAME = "/tms/stidreflogid-detector";
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
		 * @date 2017年7月10日 下午3:45:39
		 * @body_statement
		 */
		LOG.info("新的实时更新stid到logid映射服务开启...");
		if (!Init()) {
			LOG.error("StidSstidGidRefLogidDetector start, Init failed");
			return;
		}

		Long lastTimestamp = System.currentTimeMillis();
		while (true) {
			List<StidSStidRefLogDaily> stidSStidRefLogDailyList = null;
			try {
				stidSStidRefLogDailyList = new ArrayList<StidSStidRefLogDaily>();//logMgrService.getServerDiaryInfoBydiaryId(lastUpdatedRecordId),
				//这边后面从dubbo获取修改的信息
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("StidSstidGidRefLogidDetector loop, logMgrService getStidSstidGidRefLogidDetectoroBydiaryId failed, "
						+ e.getMessage());
			}
			if(stidSStidRefLogDailyList == null)
			{
				LOG.info("StidSstidGidRefLogidDetector loop, logMgrService getStidSstidGidRefLogidDetectorBydiaryId get null list");
			}else
			{
				for (StidSStidRefLogDaily sStidRefLogDaily : stidSStidRefLogDailyList)
				{
					if (sStidRefLogDaily == null
							|| sStidRefLogDaily.getId() == null) {
						LOG.error("StidSstidGidRefLogidDetector loop, null StidSstidGidRefLogidDetector or DiaryId");
						continue;
					}
					if (sStidRefLogDaily.getId().intValue() <= lastUpdatedRecordId) {
						LOG.error("StidSstidGidRefLogidDetector loop, DiaryId in StidSstidGidRefLogidDetector "
								+ sStidRefLogDaily.getId().intValue()
								+ " < lastUpdatedRecordId"
								+ lastUpdatedRecordId);
						continue;
					}
					SetLastUpdatedRecordId(sStidRefLogDaily.getId().intValue());
					
					do {										
						String changeLog = GenChangeLog(sStidRefLogDaily);
						if (changeLog == null) {
							// 
							LOG.error("StidSstidGidRefLogidDetector loop, GenStidSstidGidRefLogidDetectorChangeLog failed, StidSstidGidRefLogidDetector is "
									+ sStidRefLogDaily.toString());
							break;
						}

						LOG.info("StidSstidGidRefLogidDetector loop, GenStidSstidGidRefLogidDetectorChangeLog: "
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
			LOG.info("StidSstidGidRefLogidDetector start, time to sleep is "
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
		 * @date 2017年7月10日 下午3:45:39
		 * @body_statement return false;
		 */
		String strResult = null;
		try {
			// byte[] bytes = zooKeeper.getData(ZOOKEEPER_NODE_NAME, null,
			// null);
			/**
			 * 需要判断Zookeeper上的节点是否存在，注意是该程序第一次初始化的时候，在zk上生成对于的path node
			 */
			if (zkClientUtil.givedPathIsexists(ZOOKEEPER_NODE_NAME) == false) {
				zkClientUtil.recursiveCreateNode(ZOOKEEPER_NODE_NAME,
						"0".getBytes());
			}
			byte[] bytes = zkClientUtil.getData(ZOOKEEPER_NODE_NAME);
			strResult = new String(bytes);
			System.out.println("strResult:" + strResult);
			lastUpdatedRecordId = Integer.parseInt(strResult.trim());
			System.out.println("lastUpdatedRecordId:" + lastUpdatedRecordId);
		} catch (NullPointerException ex) {
			LOG.error("StidSstidGidRefLogidDetector GetLastUpdatedRecordIdFromZookeeper failed, NullPointerException occured");
			return false;
		} catch (KeeperException ex) {
			LOG.error("StidSstidGidRefLogidDetector GetLastUpdatedRecordIdFromZookeeper failed, KeeperException occured");
			LOG.error("ex.code() " + ex.code());
			LOG.error("ex.toString()" + ex.toString());
			LOG.error("ex.getMessage()" + ex.getMessage());
		} catch (Exception ex) {
			LOG.error("StidSstidGidRefLogidDetector GetLastUpdatedRecordIdFromZookeeper failed, "
					+ ex.getMessage());
			return false;
		}

		if (lastUpdatedRecordId < 0) {
			LOG.error("StidSstidGidRefLogidDetector GetLastUpdatedRecordIdFromZookeeper failed, get lastUpdatedRecordId ["
					+ lastUpdatedRecordId + "] from zookeeper <0");
			return false;
		}

		return true;
	}

	@Override
	protected String GenChangeLog(StidSStidRefLogDaily stidSStidRefLogDaily) {
		// TODO Auto-generated method stub
		// return null;
		/**
		 * @author looper
		 * @date 2017年7月10日 下午3:45:39
		 * @body_statement return null;
		 */
		if (stidSStidRefLogDaily.getCurd() == null) {
			LOG.error("StidSStidRefLogDaily GenServerChangeLog, null curd");
			return null;
		}

		if (stidSStidRefLogDaily.getGid() != null
				&& stidSStidRefLogDaily.getId() != null
				&& stidSStidRefLogDaily.getLogid() != null
				&& stidSStidRefLogDaily.getStid() != null
				&& stidSStidRefLogDaily.getSstid() != null) {
			StringBuffer buf = new StringBuffer();
			/**
			 * 这边根据com.taomee.tms.mgr.core.loganalyser.RealtimeLogAnalyser.
			 * UpdateConfig的处理流程 设置日志格式以及logid信息
			 */
			switch (stidSStidRefLogDaily.getCurd()) {
			case 1: {
				// 增加
				// "_logid_=0\t_editId_=1\t_stid_=主线剧情\t_sstid_=完成步骤3\t_gid_=632\t_newlogId_=12"
				buf.append("_logid_=0\t_editId_=");
				buf.append(stidSStidRefLogDaily.getCurd());
				buf.append("\t");
				buf.append("_stid_=");
				buf.append(stidSStidRefLogDaily.getStid());
				buf.append("\t");
				buf.append("_sstid_=");
				buf.append(stidSStidRefLogDaily.getSstid());
				buf.append("\t");
				buf.append("_gid_=");
				buf.append(stidSStidRefLogDaily.getGid());
				buf.append("\t");
				buf.append("_newlogId_=");
				buf.append(stidSStidRefLogDaily.getLogid());
				break;
			}
			case 2: {
				// 修改
				buf.append("_logid_=0\t_editId_=");
				buf.append(stidSStidRefLogDaily.getCurd());
				buf.append("\t");
				buf.append("_stid_=");
				buf.append(stidSStidRefLogDaily.getStid());
				buf.append("\t");
				buf.append("_sstid_=");
				buf.append(stidSStidRefLogDaily.getSstid());
				buf.append("\t");
				buf.append("_gid_=");
				buf.append(stidSStidRefLogDaily.getGid());
				buf.append("\t");
				buf.append("_newlogId_=");
				buf.append(stidSStidRefLogDaily.getLogid());
				break;
			}
			case 3: {

				break;
			}
			default:

				return null;
			}

			return buf.toString();
		}
		return null;
	}

	@Override
	protected void PushLogToKafka(String changeLog) {
		// TODO Auto-generated method stub
		//
		/**
		 * @author looper
		 * @date 2017年7月10日 下午3:45:39
		 * @body_statement
		 */
		while (true) {
			List<PartitionInfo> partitionInfos = kafkaSyncProducer
					.GetTopicPartitions(KAFKA_TOPIC);
			if (partitionInfos == null || partitionInfos.size() == 0) {
				// 没有获取到partition或者partition为空
				LOG.error("StidSstidGidRefLogidDetector PushLogToKafka, null or empty partitionInfos of topic "
						+ KAFKA_TOPIC);
				SafeSleep(1000L);
				continue;
			}

			for (PartitionInfo partitionInfo : partitionInfos) {
				while (true) {
					if (!kafkaSyncProducer.SendMessage(changeLog,
							partitionInfo.partition())) {
						LOG.error("StidSstidGidRefLogidDetector PushLogToKafka, SendMessage to partition "
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
		 * @date 2017年7月10日 下午3:45:39
		 * @body_statement
		 */
		while (true) {
			try {

				zkClientUtil.setData(ZOOKEEPER_NODE_NAME,
						Integer.toString(lastUpdatedRecordId).getBytes());
			} catch (Exception ex) {
				LOG.error("StidSstidGidRefLogidDetector SetLastUpdatedRecordIntoToZookeeper, zookeeper create failed, "
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
