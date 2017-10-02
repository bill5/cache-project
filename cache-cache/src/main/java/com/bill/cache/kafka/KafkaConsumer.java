package com.bill.cache.kafka;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;


/**
 * 
 * kafka 消费者线程
 * @author bill
 * @date 2017年8月26日 上午2:52:54
 */
public class KafkaConsumer implements Runnable{
	
	private  ConsumerConnector consumerConnector;
	private  String topic;
	
	public KafkaConsumer(String topic){
		this.consumerConnector = Consumer.createJavaConsumerConnector(createConsumerConfig());
		this.topic = topic;
	}

	@Override
	public void run() {
		//kafka 消息分发处理
		Map<String,Integer> topicCountMap = Maps.newHashMap();
		topicCountMap.put(topic, 1);
		Map<String,List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		for(KafkaStream<byte[], byte[]> stream : streams){
			new Thread(new KafkaMessageProcessor(stream)).start();
		}
	}
	
	/**
	 * 创建 kafka consumer config
	 * @return ConsumerConfig
	 */
	private static ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.0.16:2181,192.168.0.17:2181,192.168.0.18:2181");
        props.put("group.id", "cache-group");
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }

}
       