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
package org.smartloli.game.x.m.ubas.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.game.x.m.ubas.protocol.SqlTypeInfo;

/**
 * 用于解析XML文件的工具类.
 * 
 * @author smartloli.
 *
 *         Created by Nov 12, 2017
 */
public class DomUtils {

	/** 申明一个日志打印对象. */
	private static final Logger LOG = LoggerFactory.getLogger(DomUtils.class);

	/** 读取XML配置文件, 解析XML文件中的任务数. */
	public static List<SqlTypeInfo> getTask(String xml, String[] reDate) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(xml));
		Element node = document.getRootElement();
		return listNodes(node, reDate);
	}

	/** 节点XML文件的节点. */
	@SuppressWarnings("unchecked")
	private static List<SqlTypeInfo> listNodes(Element node, String[] reDate) {
		List<SqlTypeInfo> list = new ArrayList<SqlTypeInfo>();
		List<Element> tasks = node.elements();
		for (Element taskNode : tasks) {
			String frequency = taskNode.attributeValue("frequency");
			List<String> listDate = new ArrayList<>();
			String sql = taskNode.element("hql").getTextTrim();
			SqlTypeInfo task = new SqlTypeInfo();
			String name = taskNode.attributeValue("name");
			if ("day".equals(frequency)) {
				if (name.contains("lose_reg_date")) {
					String date = (reDate.length == 0 ? CalendarUtils.getLastDay() : reDate[0]);
					task.setDate(date);
					task.setSql(String.format(sql, CalendarUtils.getLastDayFilter(), CalendarUtils.getNext60Day(CalendarUtils.getLastDay()), CalendarUtils.getLastDay(), CalendarUtils.getLastDayFilter()));
				} else {
					String date = (reDate.length == 0 ? CalendarUtils.getLastDay() : reDate[0]);
					task.setDate(date);
					task.setSql(String.format(sql, date, date, date));
				}
			} else if ("month".equals(frequency) || "season".equals(frequency)) {
				String[] realData = (reDate.length != 2 ? CalendarUtils.getLastMonth() : reDate);
				for (String date : realData) {
					listDate.add(date);
				}
				task.setDate(listDate.get(0).substring(0, 6));
				if (name.contains("ios")) {
					task.setSql(String.format(sql, listDate.get(0), listDate.get(1), "iPhone%"));
				} else if (name.contains("android")) {
					task.setSql(String.format(sql, listDate.get(0), listDate.get(1), "android%"));
				} else if (name.contains("pay_click")) {
					task.setSql(String.format(sql, listDate.get(0), listDate.get(1), listDate.get(0), listDate.get(1)));
				} else {
					task.setSql(String.format(sql, listDate.get(0), listDate.get(1)));
				}
			} else if ("daily".equals(frequency)) {
				if (name.contains("rat")) {
					String[] realData = (reDate.length != 2 ? CalendarUtils.getLastMonth() : reDate);
					for (String date : realData) {
						listDate.add(date);
					}
					task.setLimit(Integer.parseInt(listDate.get(1)) - Integer.parseInt(listDate.get(0)) + 1);
					task.setDate(listDate.get(0).substring(0, 6));
					task.setSql(String.format(sql, listDate.get(0), listDate.get(1), listDate.get(0), listDate.get(1)));
				} else {
					String plat = taskNode.attributeValue("name");
					String realData = (reDate.length != 1 ? CalendarUtils.getLastDay() : reDate[0]);
					task.setDate(realData);
					task.setSql(String.format(sql, realData, plat, realData, plat));
				}
			}
			task.setType(taskNode.attributeValue("type"));
			task.setName(name);
			task.setTheme(taskNode.attributeValue("theme"));
			task.setFrequency(frequency);
			list.add(task);
			LOG.info("Add job has finished, job task size [" + list.size() + "]");
		}
		return list;
	}
}
