package com.taomee.tms.mgr.schema.detector;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaSyncProducer {
	private static final Logger LOG = LoggerFactory.getLogger(KafkaSyncProducer.class);
    private final KafkaProducer<Object, String> producer;		// 不需要key，定义成Object，并传入null
    private final String topic;
    private int messageNo = 1;
    private List<Integer> list = new ArrayList<Integer>();
//    private List<PartitionInfo> partitionInfos = new ArrayList<PartitionInfo>();

    public KafkaSyncProducer(String topic, String brokers, String clientId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
//        props.put("zk.connect", zkHosts);		// 必须要有bootstrap.servers
        props.put("client.id", clientId);
        props.put("acks", "all");	// The "all" setting we have specified will result in blocking on the full commit of the record, the slowest but most durable setting
        props.put("retries", 0);	// 无需保存并重传
        props.put("linger.ms", 0);	// 收到消息后保存一段时间，以便批量发送，尽量关闭此机制
//        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
        this.topic = topic;
    }
    
    public List<PartitionInfo> GetTopicPartitions(String topic) {
    	try {
    		List<PartitionInfo> partitionInfos = producer.partitionsFor(topic);
    		return partitionInfos;
    	} catch (Exception ex) {
    		LOG.error("KafkaSyncProducer GetTopicPartitions failed, " + ex.getMessage());
    		return null;
    	}
    }

    public boolean SendMessage(String messageStr, int partition) {
        try {
        	// TODO ProducerRecord有partition参数
//            producer.send(new ProducerRecord<>(topic, Integer.toString(messageNo), messageStr)).get();
            producer.send(new ProducerRecord<>(topic, partition, null, messageStr)).get();
        } catch (KafkaException | InterruptedException | ExecutionException ex) {
            LOG.error("KafkaSyncProducer SendMessage failed, " + ex.getMessage());
            return false;
        } catch (Exception ex) {
        	LOG.error("KafkaSyncProducer SendMessage failed, unknown exception occured, " + ex.getMessage());
            return false;
        }
        
        // 成功以后messageNo+1，TODO确认此机制
	    ++messageNo;
	    return true;
    }
    
    public static void main(String[] args) {
    	KafkaSyncProducer kafkaSyncProducer = new KafkaSyncProducer("tms-online-test", "10.1.1.35:9092,10.1.1.151:9093,10.1.1.153:9094", "tms-schema-detector");
    	
//    	kafkaSyncProducer.SendMessage("1234", 0);
//    	kafkaSyncProducer.SendMessage("2345", 1);
//    	kafkaSyncProducer.SendMessage("2345", 2);	// 测试exception
    	
    	for (PartitionInfo partitionInfo: kafkaSyncProducer.GetTopicPartitions("tms-online-test")) {
    		System.out.println("partitionInfo " + partitionInfo);
    	}
    }
}
