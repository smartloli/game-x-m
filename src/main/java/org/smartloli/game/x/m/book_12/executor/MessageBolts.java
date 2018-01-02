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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 实现IRichBolt接口,做数据预处理操作.
 * 
 * @author smartloli.
 *
 *         Created by Jan 1, 2018
 */
public class MessageBolts implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OutputCollector collector;

	@Override
	public void cleanup() {

	}

	@Override
	public void execute(Tuple input) {
		JSONObject json = JSON.parseObject(input.getString(0));
		String tm = json.getString("_tm");
		String uid = json.getString("uid");
		String plat = json.getString("plat");
		String ip = json.getString("ip");

		String[] line = new String[] { "uid_" + uid, plat, ip, tm };
		for (int i = 0; i < line.length; i++) {
			List<Tuple> a = new ArrayList<Tuple>();
			a.add(input);
			switch (i) {
			case 0:// uid
				this.collector.emit(a, new Values(line[i]));
				break;
			case 1:// plat
				this.collector.emit(a, new Values(line[i]));
				break;
			case 2:// ip
				this.collector.emit(a, new Values(line[i]));
				break;
			case 3:// tm
				this.collector.emit(a, new Values(line[i]));
				break;
			default:
				break;
			}
		}
		this.collector.ack(input);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("attribute"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
