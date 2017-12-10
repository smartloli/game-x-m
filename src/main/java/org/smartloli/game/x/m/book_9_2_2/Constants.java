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

/**
 * 静态变量申明类.
 * 
 * @author smartloli.
 *
 *         Created by Dec 10, 2017
 */
public class Constants {
	/** Hadoop RPC 版本ID. */
	public interface VersionID {
		public static final long RPC_VERSION = 7788L;
	}

	public interface Address {
		public static final String RPC_HOST = "127.0.0.1";
		public static final int RPC_PORT = 8888;
	}
}
