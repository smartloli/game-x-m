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

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", new KafkaSpout());
		builder.setBolt("bolts", new MessageBolts()).shuffleGrouping("spout");
		builder.setBolt("stats", new StatsBolts(), 2).fieldsGrouping("bolts", new Fields("attribute"));
		Config config = new Config();
		config.setDebug(true);
		if (JConstants.StormParam.CLUSTER.equals(SystemConfig.getProperty("game.x.m.storm.mode"))) {
			LOG.info("Cluster Mode Submit.");
			// config.put(Config.NIMBUS_SEEDS,
			// SystemConfig.getPropertyArray("game.x.m.storm.nimbus", ","));
			// config.setMaxSpoutPending(5000);
			// try {
			// StormSubmitter.submitTopologyWithProgressBar(KafkaTopology.class.getSimpleName()
			// + "_" + new Date().getTime(), config, builder.createTopology());
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			String path = SystemConfig.getProperty("game.x.m.storm.jar.path");
			config.put(Config.NIMBUS_SEEDS, Arrays.asList("dn1")); //
			config.put(Config.NIMBUS_THRIFT_PORT, 6627);// 配置nimbus连接端口，默认 6627
			config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList("dn1", "dn2", "dn3")); // 配置zookeeper连接主机地址，可以使用集合存放多个
			config.put(Config.STORM_ZOOKEEPER_PORT, 2181); // 配置zookeeper连接端口，默认2181
			config.setNumWorkers(2);

			// 非常关键的一步，使用StormSubmitter提交拓扑时，不管怎么样，都是需要将所需的jar提交到nimbus上去，如果不指定jar文件路径，
			// storm默认会使用System.getProperty("storm.jar")去取，如果不设定，就不能提交
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
