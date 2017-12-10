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
package org.smartloli.game.x.m.ubas.server;

import java.text.ParseException;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.game.x.m.ubas.quartz.StatsJobQuartz;
import org.smartloli.game.x.m.ubas.util.SystemConfig;

/**
 * 定时任务统计指标入口.
 * 
 * @author smartloli.
 *
 *         Created by Nov 12, 2017
 */
public class StatsServer {

	/** 申明日志打印对象. */
	private static final Logger LOG = LoggerFactory.getLogger(StatsServer.class);

	private StatsServer() {

	}

	/** 主函数入口. */
	public static void main(final String[] args) {
		try {
			if (args.length != 1) {
				LOG.info("Stats name has error,please check input.");
				return;
			}
			final String jobPath = System.getProperty("user.dir") + "/conf/" + args[0];
			jobStart(jobPath);
		} catch (Exception ex) {
			LOG.error("Run stats job has error, msg is " + ex.getMessage());
		}

	}

	/** 启动定时任务. */
	private static void jobStart(final String jobPath) throws ParseException, SchedulerException {
		final JobDetail job = new JobDetail("job_daily_" + System.currentTimeMillis(), StatsJobQuartz.class);
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("task", jobPath);
		job.setJobDataMap(jobDataMap);
		final CronTrigger cron = new CronTrigger("cron_daily_" + System.currentTimeMillis(), "cron_daily_" + System.currentTimeMillis(), SystemConfig.getProperty("game.x.m.ubas.crontab"));
		final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		final Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.scheduleJob(job, cron);
		scheduler.start();
	}
}
