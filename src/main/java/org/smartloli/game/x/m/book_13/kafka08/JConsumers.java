/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartloli.game.x.m.book_13.kafka08;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.smartloli.game.x.m.ubas.util.SystemConfig;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * Kafka 0.8.x版本的消费者实现.
 * 
 * @author smartloli.
 *
 *         Created by Jan 5, 2018
 */
public class JConsumers extends Thread {
	/** 声明一个消费者对象. */
	private ConsumerConnector consumer;
	/** 线程休眠的时间间隔. */
	private final int SLEEP = 1000 * 3;
	/** Kafka集群中业务Topic名称. */
	private String topic;
	/** Zookeeper客户端连接地址. */
	private String zkClis = "";
	/** 消费者指定组. */
	private String group = "";

	/** 构造函数初始化. */
	public JConsumers(String topic, String group, String zkClis) {
		consumer = Consumer.createJavaConsumerConnector(this.consumerConfig());
		this.topic = topic;
		this.zkClis = zkClis;
		this.group = group;
	}

	/** 配置消费者信息. */
	private ConsumerConfig consumerConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", this.zkClis);
		props.put("group.id", this.group);
		props.put("zookeeper.session.timeout.ms", "40000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

	/** 线程执行,开始消费消息并向指定的Kafka集群中的Topic消费记录. */
	@Override
	public void run() {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()) {
			System.out.println("Receive => " + new String(it.next().message()));
			try {
				sleep(SLEEP);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/** 启动消费者服务线程. */
	public static void main(String[] args) {
		String topic = "app_errors";
		String group = "es";
		String zkCli = SystemConfig.getProperty("game.x.m.zookeeper");
		/** 参数指定topic,group,zookeeper信息. */
		JConsumers consumer = new JConsumers(topic, group, zkCli);
		/** 启动线程. */
		consumer.start();
	}
}
