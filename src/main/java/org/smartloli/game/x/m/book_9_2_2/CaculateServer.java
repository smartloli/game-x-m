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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 计算应用服务类.
 * 
 * @author smartloli.
 *
 *         Created by Dec 10, 2017
 */
public class CaculateServer {
	/** 申明一个日志收集类对象. */
	private static final Logger LOG = LoggerFactory.getLogger(CaculateServer.class);

	public static void main(String[] args) {
		try {
			Server server = new RPC.Builder(new Configuration()).setProtocol(CaculateService.class).setBindAddress(Constants.Address.RPC_HOST).setPort(Constants.Address.RPC_PORT).setInstance(new CaculateServiceImpl()).build(); // 创建一个Server对象
			server.start(); // 开启服务
			LOG.info("CaculateServer has started.");
		} catch (Exception ex) {
			LOG.error("CaculateServer server has error, message is " + ex.getMessage());
		}
	}

}
