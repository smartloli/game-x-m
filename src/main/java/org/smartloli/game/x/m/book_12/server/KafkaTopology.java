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
package org.smartloli.game.x.m.book_12.server;

import java.util.Arrays;
import java.util.Date;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.game.x.m.book_12.constant.JConstants;
import org.smartloli.game.x.m.book_12.executor.KafkaSpout;
import org.smartloli.game.x.m.book_12.executor.MessageBolts;
import org.smartloli.game.x.m.book_12.executor.StatsBolts;
import org.smartloli.game.x.m.ubas.util.SystemConfig;

/**
 * 消费Kafka集群中Topic的数据,提交任务到Storm集群.
 * 
 * @author smartloli.
 *
 *         Created by Jan 1, 2018
 */
public class KafkaTopology {
	private static Logger LOG = LoggerFactory.getLogger(KafkaTopology.class);

	/** 采用Storm Thrift接口进行提交任务. */
	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", new KafkaSpout());
		builder.setBolt("bolts", new MessageBolts()).shuffleGrouping("spout");
		builder.setBolt("stats", new StatsBolts(), 2).fieldsGrouping("bolts", new Fields("attribute"));
		Config config = new Config();
		config.setDebug(true);
		if (JConstants.StormParam.CLUSTER.equals(SystemConfig.getProperty("game.x.m.storm.mode"))) {
			LOG.info("Cluster Mode Submit.");

			String path = SystemConfig.getProperty("game.x.m.storm.jar.path");
			config.put(Config.NIMBUS_SEEDS, Arrays.asList("dn1")); //
			config.put(Config.NIMBUS_THRIFT_PORT, 6627);
			config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList("dn1", "dn2", "dn3"));
			config.put(Config.STORM_ZOOKEEPER_PORT, 2181);
			config.setNumWorkers(2);

			System.setProperty("storm.jar", path);

			try {
				StormSubmitter.submitTopology(KafkaTopology.class.getSimpleName() + "_" + new Date().getTime(), config, builder.createTopology());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LOG.info("Local Mode Submit.");
			config.setMaxTaskParallelism(1);
			LocalCluster local = new LocalCluster();
			local.submitTopology("stats", config, builder.createTopology());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			local.shutdown();
		}
	}
}
