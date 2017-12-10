package org.smartloli.game.x.m.ubas.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Date Nov 6, 2015
 *
 * @Author dengjie
 *
 * @Note Get stats date,include day|month
 */
public class CalendarUtils {

	public static String getLastDay() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		Date curr = new Date();
		calendar.setTime(curr);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return df.format(calendar.getTime());
	}
	
	public static String getLastDayFilter() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date curr = new Date();
		calendar.setTime(curr);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return df.format(calendar.getTime());
	}

	public static String getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
	}

	public static String getNext30Day(String date) {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dfs.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.DAY_OF_MONTH, -30);
		return dfs.format(calendar.getTime());
	}
	
	public static String getNext60Day(String date) {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dfs.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.DAY_OF_MONTH, -60);
		return dfs.format(calendar.getTime());
	}

	public static String[] getLastMonth() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Calendar calendarFirstDay = Calendar.getInstance();
		calendarFirstDay.add(Calendar.MONTH, -1);
		calendarFirstDay.set(Calendar.DAY_OF_MONTH, 1);
		Calendar calendarLastDay = Calendar.getInstance();
		calendarLastDay.set(Calendar.DAY_OF_MONTH, 1);
		calendarLastDay.add(Calendar.DATE, -1);
		return new String[] { df.format(calendarFirstDay.getTime()), df.format(calendarLastDay.getTime()) };
	}

	public static void main(String[] args) {
		System.out.println(getNext60Day("20161120"));
	}

}
