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
package org.smartloli.game.x.m.book_4_3_4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.game.x.m.ubas.util.SystemConfig;

/**
 * 通过JDBC操作Hive数据仓库
 * 
 * @author smartloli.
 *
 *         Created by Nov 1, 2017
 */
public class HiveJdbcUtils {
	private final static Logger LOG = LoggerFactory.getLogger(HiveJdbcUtils.class);

	static {
		try {
			// 加载Hive JDBC驱动
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			LOG.info("Load hive driver success.");
		} catch (Exception e) {
			LOG.error("Hive init driver failed,msg is " + e.getMessage());
		}
	}

	// 声明连接对象
	private Connection conn = null;

	// 初始化连接地址
	public HiveJdbcUtils() {
		try {
			// String[] urls = new String[] { "jdbc:hive2://nna:10001/default",
			// "jdbc:hive2://nns:10001/default" };
			String[] urls = SystemConfig.getPropertyArray("game.x.m.ubas.hive.jdbc", ",");
			for (String url : urls) {
				String connect = url;
				try {
					conn = DriverManager.getConnection(connect, "", "");
					if (conn != null) {
						break;
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
					LOG.error("URL[" + url + "] has error");
				}
			}
		} catch (Exception e) {
			LOG.error("Config file has error url.");
		}
	}

	// 获取连接对象
	public Connection getConnection() {
		return conn;
	}

	// 执行带有返回结果的SQL语句
	public ResultSet executeQuery(String hql) throws SQLException {
		Statement stmt = null;
		ResultSet res = null;

		if (conn != null) {
			LOG.info("HQL[" + hql + "]");
			stmt = conn.createStatement();
			res = stmt.executeQuery(hql);
		} else {
			LOG.info("Object [conn] is null");
		}

		return res;
	}

	// 执行不需要返回结果的SQL
	public void execute(String hql) throws SQLException {
		if (conn != null) {
			conn.createStatement().execute(hql);
		} else {
			LOG.info("Object [conn] is null");
		}
	}

	// 关闭连接对象
	public void close() {
		try {
			if (!conn.isClosed()) {
				conn.close();
			}
		} catch (Exception ex) {
			LOG.error("Close hive connect object has error,msg is " + ex.getMessage());
		}
	}

	// 主函数入口
	public static void main(String[] args) throws SQLException {
		HiveJdbcUtils hive = new HiveJdbcUtils();
		// 显示所有表名
		String sql = "SHOW TABLES";

		// 执行SQL语句
		ResultSet rs = hive.executeQuery(sql);

		// 打印所有表名
		while (rs.next()) {
			LOG.info("Tables Name : " + rs.getString(1));
		}

		// 查看表结构信息
		sql = "DESC ip_login_text2";

		// 执行SQL语句
		rs = hive.executeQuery(sql);

		// 循环打印表结构信息
		while (rs.next()) {
			LOG.info(rs.getString(1) + "\t" + rs.getString(2));
		}

		try {
			if (rs != null) {
				rs.close(); // 关闭当前ResultSet对象
			}

			hive.close(); // 关闭连接对象
		} catch (Exception ex) {
			LOG.error("Close connection has error, msg is " + ex.getMessage());
		}
	}
}
