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
package org.smartloli.game.x.m.ubas.quartz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.game.x.m.ubas.protocol.SqlTypeInfo;
import org.smartloli.game.x.m.ubas.util.DomUtils;
import org.smartloli.game.x.m.ubas.util.HiveJdbcUtils;
import org.smartloli.game.x.m.ubas.util.JedisUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;

/**
 * 使用Quartz定时执行任务.
 * 
 * @author smartloli.
 *
 *         Created by Nov 12, 2017
 */
public class StatsJobQuartz implements Job {
	private static final Logger LOG = LoggerFactory.getLogger(StatsJobQuartz.class);
	private static int taskNumber = 0;
	private HiveJdbcUtils hive = new HiveJdbcUtils();

	/** 处理业务逻辑, 获取执行任务清单. */
	public void execute(JobExecutionContext context) {
		JobDetail job = context.getJobDetail();
		String jobPath = (String) job.getJobDataMap().get("task");
		try {
			List<SqlTypeInfo> tasks = DomUtils.getTask(jobPath, new String[] {});
			for (SqlTypeInfo task : tasks) {
				try {
					executeJobs(task);
					taskNumber++;
					LOG.info("Finished task,number is [" + taskNumber + "]");
				} catch (Exception ex) {
					ex.printStackTrace();
					LOG.error("Execute jobs has error ,msg is " + ex.getMessage());
				}
			}
			taskNumber = 0;
			try {
				hive.close(); // 释放Hive连接对象
			} catch (Exception ex) {
				LOG.error("Release Connection obj has error,msg is " + ex.getMessage());
			}
		} catch (Exception e) {
			LOG.error("Get Task list has error.msg is " + e.getMessage());
		}

	}

	/** 若任务需要重新计算, 可以调度此函数. */
	public void reExecute(String jobPath, String[] reDate) {
		try {
			List<SqlTypeInfo> tasks = DomUtils.getTask(jobPath, reDate);
			for (SqlTypeInfo task : tasks) {
				try {
					executeJobs(task);
					taskNumber++;
					LOG.info("Finished task,number is [" + taskNumber + "]");
				} catch (Exception ex) {
					LOG.error("Execute jobs has error ,msg is " + ex.getMessage());
				}
			}
			taskNumber = 0;
			try {
				hive.close();// Release connection
			} catch (Exception ex) {
				LOG.error("Release Connection obj has error,msg is " + ex.getMessage());
			}
		} catch (Exception e) {
			LOG.error("Get Task list has error.msg is " + e.getMessage());
		}

	}

	/** 执行Hive统计任务. */
	private void executeJobs(final SqlTypeInfo task) throws SQLException {
		LOG.info("Execute HQL is [" + task.getSql() + "]");
		final ResultSet resultSet = hive.executeQuery(task.getSql());
		final JSONArray arrays = new JSONArray();
		if (resultSet != null) {
			while (resultSet.next()) {
				JSONObject obj = new JSONObject();
				if ("0".equals(task.getType())) {
					// TODO
				} else if ("1".equals(task.getType())) {
					String key = resultSet.getString("key");
					int value = resultSet.getInt("value");
					obj.put("key", key);
					obj.put("value", value);
					arrays.add(obj);
				}
			}

			// 申请Redis连接对象
			Jedis jedis = JedisUtils.getJedisInstance("game.x.m.ubas.stats");

			String key = task.getTheme() + "_" + task.getFrequency() + "_v2_" + task.getDate();
			String field = task.getName() + "_" + task.getType();
			// 写入到Redis数据库
			jedis.hset(key, field, arrays.toJSONString());

			if ("day".equals(task.getFrequency()) || "daily".equals(task.getFrequency())) {
				// 过期时间为30天
				jedis.expire(task.getTheme() + "_" + task.getFrequency() + "_v2_" + task.getDate(), 3600 * 24 * 30);
			}

			try {
				// 关闭数据库连接对象
				resultSet.close();
				// 释放Redis连接对象到连接池
				JedisUtils.release("game.x.m.ubas.stats", jedis);
			} catch (SQLException ex) {
				LOG.error("SQL Release has error, msg is " + ex.getMessage());
			}
		}
	}
}
