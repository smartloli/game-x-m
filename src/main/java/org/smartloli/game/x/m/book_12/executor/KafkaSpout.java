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
package org.smartloli.game.x.m.book_12.executor;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.game.x.m.ubas.util.SystemConfig;

/**
 * 实现IRichSpout接口, 用来消费Kafka集群中的Topic.
 * 
 * @author smartloli.
 *
 *         Created by Jan 1, 2018
 */
public class KafkaSpout implements IRichSpout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger LOG = LoggerFactory.getLogger(KafkaSpout.class);

	private SpoutOutputCollector collector;

	private KafkaConsumer<String, String> createKafkaConfig() {
		Properties props = new Properties();
		props.put("bootstrap.servers", SystemConfig.getProperty("game.x.m.kafka.brokers"));// "slave01:9095,slave01:9094"
		props.put("group.id", SystemConfig.getProperty("game.x.m.kafka.consumer.group"));
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return new KafkaConsumer<>(props);
	}

	@Override
	public void activate() {
		KafkaConsumer<String, String> consumer = createKafkaConfig();
		consumer.subscribe(SystemConfig.getPropertyArrayList("game.x.m.kafka.consumer.topic", ","));
		boolean flag = true;
		while (flag) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				String value = record.value();
				LOG.info("Value : " + value);
				this.collector.emit(new Values(value), value);
			}
		}
		consumer.close();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("KafkaSpout"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

	@Override
	public void ack(Object arg0) {

	}

	@Override
	public void close() {

	}

	@Override
	public void deactivate() {

	}

	@Override
	public void fail(Object arg0) {

	}

	@Override
	public void nextTuple() {

	}

	@Override
	public void open(@SuppressWarnings("rawtypes") Map arg0, TopologyContext arg1, SpoutOutputCollector collector) {
		this.collector = collector;
	}

}
