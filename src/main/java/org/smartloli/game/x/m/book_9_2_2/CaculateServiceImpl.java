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

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.ipc.ProtocolSignature;

/**
 * 实现计算接口中的函数.
 * 
 * @author smartloli.
 *
 *         Created by Dec 10, 2017
 */
public class CaculateServiceImpl implements CaculateService {

	public ProtocolSignature getProtocolSignature(String arg0, long arg1, int arg2) throws IOException {
		return this.getProtocolSignature(arg0, arg1, arg2);
	}

	/** 校验Hadoop RPC的版本ID. */
	public long getProtocolVersion(String arg0, long arg1) throws IOException {
		return Constants.VersionID.RPC_VERSION;
	}

	/** 实现累加功能. */
	public IntWritable add(IntWritable arg1, IntWritable arg2) {
		return new IntWritable(arg1.get() + arg2.get());
	}

	/** 实现消减功能. */
	public IntWritable sub(IntWritable arg1, IntWritable arg2) {
		return new IntWritable(arg1.get() - arg2.get());
	}

}
