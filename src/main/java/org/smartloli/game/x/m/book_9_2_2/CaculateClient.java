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
package org.smartloli.game.x.m.book_9_2_2;

import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.ipc.RPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端向服务器端发送请求指令.
 * 
 * @author smartloli.
 *
 *         Created by Dec 10, 2017
 */
public class CaculateClient {
	/** 申明一个日志收集类对象. */
	private static final Logger LOG = LoggerFactory.getLogger(CaculateClient.class);

	public static void main(String[] args) {
		InetSocketAddress addr = new InetSocketAddress(Constants.Address.RPC_HOST, Constants.Address.RPC_PORT); // 格式化IP和端口
		try {
			RPC.getProtocolVersion(CaculateService.class); // 校验Hadoop RPC版本ID
			CaculateService service = (CaculateService) RPC.getProxy(CaculateService.class, RPC.getProtocolVersion(CaculateService.class), addr, new Configuration()); // 获取服务端接口对象
			int add = service.add(new IntWritable(2), new IntWritable(3)).get(); // 执行累加函数
			int sub = service.sub(new IntWritable(5), new IntWritable(2)).get(); // 执行消减函数
			LOG.info("2+3=" + add); // 打印累加结果
			LOG.info("5-2=" + sub); // 打印消减结果
		} catch (Exception ex) {
			LOG.error("Client has error, message is " + ex.getMessage());
		}
	}
}
