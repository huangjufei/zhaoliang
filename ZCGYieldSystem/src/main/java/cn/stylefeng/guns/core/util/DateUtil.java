package cn.stylefeng.guns.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	final static public String DEFALUT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	static public Date currentDate() {
		return new Date();
	}
	
	static public Date date1970() {
		return new Date(0);
	}
	
	static public Date incrementYear(Date date ,int years) {
		return increment(date, years, Calendar.YEAR);
	}
	
	static public Date decrementYear(Date date ,int years) {
		return decrement(date, years, Calendar.YEAR);
	}
	
	static public Date incrementMonth(Date date ,int months) {
		return increment(date, months, Calendar.MONTH);
	}
	
	static public Date decrementMonth(Date date ,int months) {
		return decrement(date, months, Calendar.MONTH);
	}
	
	static public Date incrementDay(Date date ,int days) {
		return increment(date, days, Calendar.DATE);
	}
	
	static public Date decrementDay(Date date ,int days) {
		return decrement(date, days, Calendar.DATE);
	}
	
	static public Date incrementHour(Date date ,int hours) {
		return increment(date, hours, Calendar.HOUR_OF_DAY);
	}
	
	static public Date decrementHour(Date date ,int hours) {
		return decrement(date, hours, Calendar.HOUR_OF_DAY);
	}
	
	static public Date incrementMinute(Date date ,int minutes) {
		return increment(date, minutes, Calendar.MINUTE);
	}
	
	static public Date decrementMinute(Date date ,int minutes) {
		return decrement(date, minutes, Calendar.MINUTE);
	}
	
	static public Date incrementSecond(Date date ,int seconds) {
		return increment(date, seconds, Calendar.SECOND);
	}
	
	static public Date decrementSecond(Date date ,int seconds) {
		return decrement(date, seconds, Calendar.SECOND);
	}
	
	static public Calendar toCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	static public String format(Date date) {
		return format(date,DEFALUT_DATE_FORMAT);
	}
	
	static public String format(Date date ,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	static public Date parse(String dateString) {
		return parse(dateString ,DEFALUT_DATE_FORMAT);
	}
	
	static public Date parse(String dateString ,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(dateString);
		} catch (Exception e) {
			throw new RuntimeException("parse date exception ,data :" + dateString + " ,format :" + format);
		}
	}
	
	static public Date parse(String dateString ,Date defaultValue) {
		return parse(dateString, DEFALUT_DATE_FORMAT, defaultValue);
	}
	
	static public Date parse(String dateString ,String format ,Date defaultValue) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(dateString);
		} catch (Exception e) {
		}
		return defaultValue;
	}
	
	static public int getYear(Date date) {
		return getTime(date, Calendar.YEAR);
	}
	
	/**
	 * 	月份从0开始，所以
	 * @param date
	 * @return
	 */
	static public int getMonthFromZero(Date date) {
		return getTime(date, Calendar.MONTH);
	}
	
	static public int getMonthFromOne(Date date) {
		return getTime(date, Calendar.MONTH) + 1;
	}
	
	static public int getDayTime(Date date) {
		return getTime(date, Calendar.DATE);
	}
	
	static public int getHourTime(Date date) {
		return getTime(date, Calendar.HOUR_OF_DAY);
	}
	
	static public int getMinutes(Date date) {
		return getTime(date, Calendar.MINUTE);
	}
	
	static public int getSecond(Date date) {
		return getTime(date, Calendar.SECOND);
	}
	
	static public int getMillsecond(Date date) {
		return getTime(date, Calendar.MILLISECOND);
	}
	
	static private int getTime(Date date ,int dateType) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(dateType);
	}
	
	static private Date increment(Date date ,int incrementNumber ,int dateType) {
		Calendar calendar = toCalendar(date);
		calendar.set(dateType, getTime(date, dateType) + incrementNumber);
		return calendar.getTime();
	}
	
	static private Date decrement(Date date ,int decrementNumber ,int dateType) {
		Calendar calendar = toCalendar(date);
		calendar.set(dateType, getTime(date, dateType) - decrementNumber);
		return calendar.getTime();
	}
}
