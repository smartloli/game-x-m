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

import org.apache.hadoop.ipc.ProtocolSignature;

/**
 * 实现自定义的Hadoop RPC接口.
 * 
 * @author smartloli.
 *
 *         Created by Dec 13, 2017
 */
public class JClientProtocolImpl implements JClientProtocol {

	/** 用于获取定义版本ID. */
	@Override
	public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
		return JClientProtocol.versionID;
	}

	/** 用于获取签名协议. */
	@Override
	public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash) throws IOException {
		return new ProtocolSignature(JClientProtocol.versionID, null);
	}

	/** 用于打印参数值. */
	@Override
	public String print(String name) throws IOException {
		return name;
	}

	/** 用于累加两个整型值. */
	@Override
	public int sum(int val1, int val2) throws IOException {
		return val1 + val2;
	}

}
