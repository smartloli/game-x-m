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
package org.smartloli.game.x.m.book_9_3_2;

import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.game.x.m.book_9_2_2.Constants;

/**
 * 实现一个自定义客户端并发送请求给RPC服务端.
 * 
 * @author smartloli.
 *
 *         Created by Dec 13, 2017
 */
public class JClient {
	/** 申明一个日志收集类对象. */
	private static final Logger LOG = LoggerFactory.getLogger(JClient.class);

	public static void main(String[] args) {
		InetSocketAddress addr = new InetSocketAddress(Constants.Address.RPC_HOST, Constants.Address.RPC_PORT); // 格式化IP和端口
		try {
			JClientProtocol service = (JClientProtocol) RPC.getProxy(JClientProtocol.class, JClientProtocol.versionID, addr, new Configuration()); // 获取服务端接口对象
			String name = service.print("邓杰"); // 获取打印姓名
			int sum = service.sum(5, 2); // 执行累加函数
			LOG.info("name=" + name); // 打印姓名
			LOG.info("sum=" + sum); // 打印累加结果
		} catch (Exception ex) {
			LOG.error("Client has error, message is " + ex.getMessage());
		}
	}
}
