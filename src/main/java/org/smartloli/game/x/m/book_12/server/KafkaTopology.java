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

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.smartloli.game.x.m.book_12.executor.KafkaSpout;
import org.smartloli.game.x.m.book_12.executor.MessageBlots;
import org.smartloli.game.x.m.book_12.executor.StatsBlots;

/**
 * 消费Kafka集群中Topic的数据,提价任务到Storm集群.
 * 
 * @author smartloli.
 *
 *         Created by Jan 1, 2018
 */
public class KafkaTopology {
	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", new KafkaSpout());
		builder.setBolt("bolts", new MessageBlots()).shuffleGrouping("spout");
		builder.setBolt("stats", new StatsBlots(), 2).fieldsGrouping("bolts", new Fields("attribute"));
		Config config = new Config();
		config.setDebug(true);
		// storm.messaging.netty.max_retries
		// storm.messaging.netty.max_wait_ms
		if (args != null && args.length > 0) {
			// online commit Topology
			config.setNumWorkers(3);
			try {
				StormSubmitter.submitTopologyWithProgressBar(KafkaTopology.class.getSimpleName(), config, builder.createTopology());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// Local commit jar
			config.setMaxTaskParallelism(1);
			LocalCluster local = new LocalCluster();
			local.submitTopology("stats", config, builder.createTopology());
			// try {
			// Thread.sleep(50);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// local.shutdown();
		}
	}
}
