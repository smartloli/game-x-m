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

import java.io.IOException;

import org.apache.hadoop.ipc.VersionedProtocol;

/**
 * 自定义一个Hadoop RPC通信接口
 * 
 * @author smartloli.
 *
 *         Created by Dec 13, 2017
 */
public interface JClientProtocol extends VersionedProtocol {

	/** 不同版本号的RPC客户端和服务端之间不能通信. */
	public static final long versionID = 2L;

	/** 打印姓名. */
	public String print(String name) throws IOException;

	/** 累加两个整形数. */
	public int sum(int val1, int val2) throws IOException;
}
