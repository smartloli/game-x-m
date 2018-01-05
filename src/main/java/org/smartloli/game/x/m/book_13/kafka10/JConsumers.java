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
package org.smartloli.game.x.m.book_13.kafka10;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.game.x.m.ubas.util.SystemConfig;

/**
 * Kafka 0.10.x 版本消费者代码实现.
 * 
 * @author smartloli.
 *
 *         Created by Jan 5, 2018
 */
public class JConsumers {
	/** 创建一个日志对象. */
	private final static Logger LOG = LoggerFactory.getLogger(JConsumers.class);
	/** 声明一个Kafka消费者对象. */
	private final KafkaConsumer<String, String> consumer;
	/** 创建一个线程池对象. */
	private ExecutorService executorService;

	/** 构造函数初始化. */
	public JConsumers() {
		Properties props = new Properties();
		props.put("bootstrap.servers", SystemConfig.getProperty("game.x.m.kafka.brokers"));// Kafka Brokers信息配置
		props.put("group.id", "esx");// 指定消费组
		props.put("enable.auto.commit", "true");// 是否开启自动提交
		props.put("auto.commit.interval.ms", "1000");// 设置自动提交时间
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");// Key 反序列化
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // Value 反序列化
		consumer = new KafkaConsumer<String, String>(props); // 实例化一个消费者对象
		consumer.subscribe(Arrays.asList("kv_topic")); // 指定Topic
	}

	/** 多线程执行. */
	public void execute() {
		int nums = SystemConfig.getIntProperty("game.x.m.consumer.thread.num");
		executorService = Executors.newFixedThreadPool(nums);
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			if (null != records) {
				executorService.submit(new KafkaConsumerThread(records, consumer));
			}
		}
	}

	/** 关闭Kafka消费者对象和销毁线程池对象. */
	public void shutdown() {
		try {
			if (consumer != null) {
				consumer.close();
			}
			if (executorService != null) {
				executorService.shutdown();
			}
			if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
				LOG.error("Shutdown kafka consumer thread timeout.");
			}
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
	}

	/** 多线程子类实现消费者. */
	class KafkaConsumerThread implements Runnable {

		private ConsumerRecords<String, String> records;

		public KafkaConsumerThread(ConsumerRecords<String, String> records, KafkaConsumer<String, String> consumer) {
			this.records = records;
		}

		@Override
		public void run() {
			for (TopicPartition partition : records.partitions()) {
				List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
				for (ConsumerRecord<String, String> record : partitionRecords) {
					System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
				}
			}
		}

	}

	/** 开启多线程服务. */
	public static void main(String[] args) {
		JConsumers consumer = new JConsumers(); // 创建一个多线程对象
		try {
			consumer.execute(); // 开启多线程执行
		} catch (Exception e) {
			LOG.error("Consumer kafka has error,msg is " + e.getMessage());
			consumer.shutdown();
		}
	}
}
