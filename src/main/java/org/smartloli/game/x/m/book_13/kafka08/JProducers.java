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

import java.util.Properties;

import org.smartloli.game.x.m.ubas.util.SystemConfig;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * Kafka 0.8.x版本的生产者实现.
 * 
 * @author smartloli.
 *
 *         Created by Jan 5, 2018
 */
public class JProducers extends Thread {

	/** 声明生产者对象. */
	private Producer<Integer, String> producer;
	/** 指定生产Kafka集群中Topic的名称. */
	private String topic;
	/** 配置属性对象创建. */
	private Properties props = new Properties();
	/** 线程休眠时间间隔. */
	private final int SLEEP = 3000 * 1;

	/** 构造函数初始化. */
	public JProducers(String topic, String zkClis) {
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("metadata.broker.list", zkClis);
		producer = new Producer<Integer, String>(new ProducerConfig(props));
		this.topic = topic;
	}

	/** 线程执行,开始生产消息并向指定的Kafka中的Topic发送消息. */
	@Override
	public void run() {
		int offsetNo = 0;
		while (true) {
			String msg = new String("a" + offsetNo + ",b" + offsetNo);
			System.out.println("Send => " + msg);
			producer.send(new KeyedMessage<Integer, String>(topic, msg));
			offsetNo++;
			try {
				sleep(SLEEP);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/** 启动生产者服务线程. */
	public static void main(String[] args) {
		String topic = "app_errors";
		String zkCli = SystemConfig.getProperty("game.x.m.zookeeper");
		/** 参数指定topic,zookeeper信息. */
		JProducers producer = new JProducers(topic, zkCli);
		/** 启动线程. */
		producer.start();
	}
}
