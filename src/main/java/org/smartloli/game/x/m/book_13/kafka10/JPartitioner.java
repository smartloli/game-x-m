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

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

/**
 * Kafka多分区实现.
 * 
 * @author smartloli.
 *
 *         Created by Jan 5, 2018
 */
public class JPartitioner implements Partitioner {

	@Override
	public void configure(Map<String, ?> configs) {

	}

	/** 按照Key的Hash值取模得到分区编号. */
	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		int partition = 0;
		String k = (String) key;
		partition = Math.abs(k.hashCode()) % cluster.partitionCountForTopic(topic);
		return partition;
	}

	@Override
	public void close() {

	}

}
