package com.taomee.tms.mgr.common.imp;

import java.io.IOException;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.taomee.tms.mgr.schema.detector.KafkaSyncProducer;
import com.taomee.tms.mgr.utils.ZKClientUtil;


/**
 * 动态修改schema和serverx信息的抽象父类
 * @author looper
 * @date 2017年6月9日 下午2:44:46
 * @project tms-log-schema-detector ServerDetector
 */
public abstract class Detector<T> {
	private static final Logger LOG = LoggerFactory
			.getLogger(Detector.class);
	// 初始化时opId的三种获取方式：
	// 1. 通过zookeeper永久节点获取，在每次循环结束后会将当期处理完毕的最大opId写入此zookeeper永久节点（不需要隔一段时间写入）。
	// 2. 启动时zookeeper中必须要有次节点，否则启动失败。
	// 3. 每次只更新一条修改记录，以保证kafka中的修改记录，最多重复一个。
	// （废弃）2. 若zookeeper中没有此永久节点，表示程序首次启动，若不带指定参数程序会启动失败。
	// （废弃）3.
	// 当zookeeper中没有此永久节点，必须通过指定参数设置opId来冷启动，通过指定参数启动时不会先去读取zookeeper中的永久节点。启动后会会和步骤一一致，进入正常状态。
	// （废弃）4. 可通过指定参数，通过协议获取数据库中最大opId信息，并不启动程序（可以通过这样获取到的最大opId来指定opId启动）
	protected static final String KAFKA_TOPIC = "tms-online-test";
	// TODO 是否可以设置成zookeeper
	private static final String KAFKA_BROKERS = "10.1.1.35:9092,10.1.1.151:9093,10.1.1.153:9094";
	private static final String KAFKA_CLIENT_ID = "tms-detector";
	protected static final int LOOP_INTERVAL_MS = 2000;
	//protected static String ZOOKEEPER_NODE_NAME;
	private static final String ZOOKEEPER_HOSTS = "10.1.1.35:2181,10.1.1.153:2181,10.1.1.151:2181";
	private static final int SESSION_TIMEOUT = 30000;
	
	protected int lastUpdatedRecordId = -1;
	
	protected KafkaSyncProducer kafkaSyncProducer = null;
	protected ZKClientUtil zkClientUtil = new ZKClientUtil(ZOOKEEPER_HOSTS);
	
	/**
	 * 在实例化bean下呗调用
	 */
	public abstract void start(); 

	// 从zookeeper中获取lastUpdatedRecordId，只会在初始化中调用
	protected abstract boolean GetLastUpdatedRecordIdFromZookeeper();
		
	protected abstract String GenChangeLog(T t);
	
	// 写入kafka数据topic的所有partition
	protected abstract void PushLogToKafka(String changeLog);
	
	// 在循环中调用，并且直到成功才返回
	protected abstract void SetLastUpdatedRecordIntoToZookeeper(); 

	/**
	 * 初始化zk、kafka、以及从zk服务当中拉取最后更改的ID信息
	 * 
	 * @return
	 */
	public boolean Init() {

		if (!InitZookeeper()) {
			LOG.error("Detector Init, InitZookeeper failed");
			return false;
		}

		LOG.info("Detector Init, zookeeper init success");

		if (!InitKafka()) {
			LOG.error("Detector Init, InitKafka failed");
			return false;
		}

		LOG.info("Detector Init, kafka init success");

		// 从zookeeper中获取最后修改记录id，后续会作为参数从数据库拉取此id后所有的修改
		if (!GetLastUpdatedRecordIdFromZookeeper()) {
			LOG.error("Detector Init, GetLastUpdatedRecordIdFromZookeeper failed");
			return false;
		}

		return true;
	}


	private boolean InitZookeeper() {
		// 连接zookeeper
		try {
			// TODO 没有close
			zkClientUtil.connect();
			
		} catch (IOException ex) {
			LOG.info("Detector Init, IOException occured when connect zookeeper, "
					+ ex.getMessage());
			return false;
		} catch (Exception ex) {
			LOG.info("Detector Init, unknown exception occured when connect zookeeper, "
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
			LOG.info("Detector InitKafka, KafkaException occured, "
					+ ex.getMessage());
			return false;
		} catch (Exception ex) {
			LOG.info("Detector InitKafka, unknown exception occured, "
					+ ex.getMessage());
			return false;
		}
		return true;
	}

	
	protected void SafeSleep(Long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LOG.error("Detector start, InterruptedException catched");
		}
	}

}
