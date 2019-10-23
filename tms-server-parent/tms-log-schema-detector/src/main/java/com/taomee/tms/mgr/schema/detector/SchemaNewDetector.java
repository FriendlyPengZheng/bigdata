package com.taomee.tms.mgr.schema.detector;

import java.util.List;

import org.apache.kafka.common.PartitionInfo;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.common.imp.Detector;
import com.taomee.tms.mgr.core.opanalyser.BaseOpAnalyser;
import com.taomee.tms.mgr.core.opanalyser.OpAnalyserFactory;
import com.taomee.tms.mgr.core.schemaanalyser.PlainSchemaAnalyser;
import com.taomee.tms.mgr.entity.SchemaDiaryInfo;
import com.taomee.tms.mgr.entity.SchemaInfo;

/**
 * 动态生成Schema变化服务日志的服务
 * 
 * @author looper
 * @date 2017年6月19日 下午6:00:50
 * @project tms-log-schema-detector SchemaNewDetector
 */
public class SchemaNewDetector extends Detector<SchemaDiaryInfo> {

	private static final Logger LOG = LoggerFactory.getLogger(SchemaNewDetector.class);
	
	/**
	 * 最后数据需要写zk上的path node
	 */
	private static final String ZOOKEEPER_NODE_NAME="/tms/schema-detector";
	//ZOOKEEPER_NODE_NAME ="/tms/schema-detector";
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
		 * @date 2017年6月19日 下午5:55:19
		 * @body_statement
		 */
		LOG.info("新的实时更新schemaInfo服务开启...");
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

			if (schemaDiaryInfoList == null) {
				LOG.info("SchemaDetector loop, logMgrService getSchemaDiaryInfoBydiaryId get null list");
			} else {
				for (SchemaDiaryInfo schemaDiaryInfo : schemaDiaryInfoList) {
					if (schemaDiaryInfo == null
							|| schemaDiaryInfo.getDiaryId() == null) {
						LOG.error("SchemaDetector loop, null schemaDiaryInfo or DiaryId");
						continue;
					}

					if (schemaDiaryInfo.getDiaryId().intValue() <= lastUpdatedRecordId) {
						LOG.error("SchemaDetector loop, DiaryId in schemaDiaryInfo "
								+ schemaDiaryInfo.getDiaryId().intValue()
								+ " < lastUpdatedRecordId"
								+ lastUpdatedRecordId);
						continue;
					}
					SetLastUpdatedRecordId(schemaDiaryInfo.getDiaryId()
							.intValue());

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

						String changeLog = GenChangeLog(schemaDiaryInfo);
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

	@Override
	protected boolean GetLastUpdatedRecordIdFromZookeeper() {
		// TODO Auto-generated method stub
		// return false;
		/**
		 * @author looper
		 * @date 2017年6月19日 下午5:55:19
		 * @body_statement return false;
		 */
		String strResult = null;
		try {
			if (zkClientUtil.givedPathIsexists(ZOOKEEPER_NODE_NAME) == false) {
				zkClientUtil.recursiveCreateNode(ZOOKEEPER_NODE_NAME,
						"0".getBytes());
			}
			byte[] bytes = zkClientUtil.getData(ZOOKEEPER_NODE_NAME);
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

	@Override
	protected String GenChangeLog(SchemaDiaryInfo schemaDiaryInfo) {
		// TODO Auto-generated method stub
		// return null;
		/**
		 * @author looper
		 * @date 2017年6月19日 下午5:55:19
		 * @body_statement return null;
		 */
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

	@Override
	protected void PushLogToKafka(String changeLog) {
		// TODO Auto-generated method stub
		//
		/**
		 * @author looper
		 * @date 2017年6月19日 下午5:55:19
		 * @body_statement
		 */
		while (true) {
			List<PartitionInfo> partitionInfos = kafkaSyncProducer
					.GetTopicPartitions(KAFKA_TOPIC);
			if (partitionInfos == null || partitionInfos.size() == 0) {
				// 没有获取到partition或者partition为空
				LOG.error("SchemaDetector PushLogToKafka, null or empty partitionInfos of topic "
						+ KAFKA_TOPIC);
				SafeSleep(1000L);
				continue;
			}

			for (PartitionInfo partitionInfo : partitionInfos) {
				while (true) {
					if (!kafkaSyncProducer.SendMessage(changeLog,
							partitionInfo.partition())) {
						LOG.error("SchemaDetector PushLogToKafka, SendMessage to partition "
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
		 * @date 2017年6月19日 下午5:55:19
		 * @body_statement
		 */
		while (true) {
			try {
				zkClientUtil.setData(ZOOKEEPER_NODE_NAME,
						Integer.toString(lastUpdatedRecordId).getBytes());
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

	private void SetLastUpdatedRecordId(int lastUpdatedRecordId) {
		this.lastUpdatedRecordId = lastUpdatedRecordId;
	}

}
