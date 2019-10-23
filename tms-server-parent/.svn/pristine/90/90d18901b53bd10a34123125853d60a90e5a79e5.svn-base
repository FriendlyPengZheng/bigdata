package com.taomee.tms.mgr.schema.detector.kafkaTest;

import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
public class FirstConsumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers",
                "10.1.1.228:9092,10.1.1.225:9092,10.1.1.223:9092");// 该地址是集群的子集，用来探测集群。
        props.put("group.id", "looperid");// cousumer的分组id
        props.put("enable.auto.commit", "true");// 自动提交offsets
        props.put("auto.commit.interval.ms", "1000");// 每隔1s，自动提交offsets
        props.put("session.timeout.ms", "30000");// Consumer向集群发送自己的心跳，超时则认为Consumer已经死了，kafka会把它的分区分配给其他进程
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");// 反序列化器
        props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("kafka-pcc-input"));// 订阅的topic,可以多个
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s",record.offset(), record.key(), record.value());
                System.out.println();
            }
        }
    }
}
