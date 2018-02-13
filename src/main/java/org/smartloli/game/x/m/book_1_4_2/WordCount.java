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
package org.smartloli.game.x.m.book_1_4_2;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.game.x.m.ubas.util.SystemConfig;

/**
 * Wordcount的例子是一个比较经典的mapreduce例子，可以叫做Hadoop版的hello world。
 * 它将文件中的单词分割取出，然后shuffle，sort（map过程）。 
 * 接着进入到汇总统计 （reduce过程），最后写到hdfs中。
 *
 * @author smartloli.
 *
 *         Created by Sep 17, 2017
 */

public class WordCount {

	private static final Logger LOG = LoggerFactory.getLogger(WordCount.class);
	private static Configuration conf;

	/**
	 * 设置高可用集群连接信息
	 */
	static {
		String[] hosts = SystemConfig.getPropertyArray("game.x.hdfs.host", ",");
		conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://cluster1");
		conf.set("dfs.nameservices", "cluster1");					// 指定hdfs的nameservice为cluster1
		conf.set("dfs.ha.namenodes.cluster1", "nna,nns");			// cluster1下面有两个NameNode，分别是nna节点，nns节点
		conf.set("dfs.namenode.rpc-address.cluster1.nna", hosts[0]);	// nna节点的RPC通信地址
		conf.set("dfs.namenode.rpc-address.cluster1.nns", hosts[1]);	// nns节点的RPC通信地址

		// 配置失败自动切换实现方式
		conf.set("dfs.client.failover.proxy.provider.cluster1", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");

		// 打包到运行集群运行
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
	}

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		/**
		 * 源文件：a b b
		 * 
		 * map之后：
		 * 
		 * a 1
		 * 
		 * b 1
		 * 
		 * b 1
		 */
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());	// 整行读取
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());								// 按空格分割单词
				context.write(word, one);								// 每次统计出来的单词+1
			}
		}
	}

	/**
	 * reduce之前：
	 * 
	 * a 1
	 * 
	 * b 1
	 * 
	 * b 1
	 * 
	 * reduce之后:
	 * 
	 * a 1
	 * 
	 * b 2
	 */
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();		// 分组累加
			}
			result.set(sum);
			context.write(key, result);	// 按相同的key输出
		}
	}

	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				LOG.info("args length is 0");
				run("test");
			} else {
				run(args[0]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void run(String name) throws Exception {
		Job job = Job.getInstance(conf);				// 创建一个任务提交对象
		job.setJarByClass(WordCount.class);
		job.setMapperClass(TokenizerMapper.class);	// 指定Map计算的类
		job.setCombinerClass(IntSumReducer.class);	// 合并的类
		job.setReducerClass(IntSumReducer.class);	// Reduce的类
		job.setOutputKeyClass(Text.class);			// 输出Key类型
		job.setOutputValueClass(IntWritable.class);	// 输出值类型

		// 设置统计文件在分布式文件系统中的路径
		String tmpLocalIn = SystemConfig.getProperty("game.x.hdfs.input.path");
		String inPath = String.format(tmpLocalIn, name);
		// 设置输出结果在分布式文件系统中的路径
		String tmpLocalOut = SystemConfig.getProperty("game.x.hdfs.output.path");
		String outPath = String.format(tmpLocalOut, name);

		FileInputFormat.addInputPath(job, new Path(inPath));		// 指定输入路径
		FileOutputFormat.setOutputPath(job, new Path(outPath));	// 指定输出路径

		int status = job.waitForCompletion(true) ? 0 : 1;

		System.exit(status);										// 执行完MR任务后退出应用
	}
}
