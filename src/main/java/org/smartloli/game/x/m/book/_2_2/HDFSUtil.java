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
package org.smartloli.game.x.m.book._2_2;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

/**
 * TODO
 * 
 * @author smartloli.
 *
 *         Created by Sep 19, 2017
 */
public class HDFSUtil {

	private static Configuration conf = null;// 申明配置属性值对象
	static {
		conf = new Configuration();
		// 指定hdfs的nameservice为cluster1,是NameNode的URI
		conf.set("fs.defaultFS", "hdfs://cluster1");
		// 指定hdfs的nameservice为cluster1
		conf.set("dfs.nameservices", "cluster1");
		// cluster1下面有两个NameNode，分别是nna节点和nns节点
		conf.set("dfs.ha.namenodes.cluster1", "nna,nns");
		// nna节点下的RPC通信地址
		conf.set("dfs.namenode.rpc-address.cluster1.nna", "nna:9000");
		// nns节点下的RPC通信地址
		conf.set("dfs.namenode.rpc-address.cluster1.nns", "nns:9000");
		// 实现故障自动转移方式
		conf.set("dfs.client.failover.proxy.provider.cluster1", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
	}

	/** 目录列表操作，展示分布式文件系统（HDFS）的目录结构 */
	public static void ls(String remotePath) throws IOException {
		FileSystem fs = FileSystem.get(conf); // 申明一个分布式文件系统对象
		Path path = new Path(remotePath); // 得到操作分布式文件系统（HDFS）文件的路径对象
		FileStatus[] status = fs.listStatus(path); // 得到文件状态数组
		Path[] listPaths = FileUtil.stat2Paths(status);
		for (Path p : listPaths) {
			System.out.println(p); // 循环打印目录结构
		}
	}

	public static void main(String[] args) throws IOException {
		ls("/");
	}

}
