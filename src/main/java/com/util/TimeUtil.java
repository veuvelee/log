package com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import org.apache.commons.lang.StringUtils;

import com.common.DateEnum;

/**
 * 时间控制工具类
 *
 */
public class TimeUtil {
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 获取昨日的日期格式字符串数据
     */
    public static String getYesterday() {
        return getYesterday(DATE_FORMAT);
    }

    /**
     * 获取对应格式的时间字符串,昨天的
     */
    public static String getYesterday(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return sdf.format(calendar.getTime());
    }


    /**
     * 将yyyy-MM-dd格式的时间字符串转换为long时间
     *
     */
    public static long parseDate_String2Long(String str) {

        return parseDate_String2Long(str, DATE_FORMAT);
    }

    /**
     * 将指定格式的时间字符串转换为long时间
     */
    public static long parseDate_String2Long(String str, String pattern) {
        Date date = null;
        try {
            date = new SimpleDateFormat(pattern).parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date.getTime();
    }

    /**
     * 将long时间转换为yyyy-MM-dd格式的时间字符串
     *
     */
    public static String parseDate_Long2String(long l) {

        return parseDate_Long2String(l, DATE_FORMAT);
    }

    /**
     * 将时间戳转换为指定格式的字符串
     */
    public static String parseDate_Long2String(long l, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }

    /**
     * 将nginx时间转换为时间戳，如果说解析失败，返回-1
     *
     */
    public static long parseNginx_Time2Long(String str) {
        Date date = parseNginx_Time2Date(str);
        return date == null ? -1L : date.getTime();
    }

    /**
     * 将nginx时间转换为date对象。如果解析失败，返回null
     *
     */
    public static Date parseNginx_Time2Date(String str) {
        if (StringUtils.isNotBlank(str)) {
            try {
                long timestamp = Double.valueOf(Double.valueOf(str.trim()) * 1000).longValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                return calendar.getTime();
            } catch (Exception e) {
                // ...
            }
        }
        return null;
    }

    /**
     * 从时间戳中获取需要的时间信息

     */
    public static int getDateInfo(long time, DateEnum type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        if (DateEnum.YEAR.equals(type)) {
            // 需要年份信息
            return calendar.get(Calendar.YEAR);
        } else if (DateEnum.SEASON.equals(type)) {
            // 需要季度信息
            int month = calendar.get(Calendar.MONTH) + 1;
            if (month % 3 == 0) {
                return month / 3;
            }
            return month / 3 + 1;
        } else if (DateEnum.MONTH.equals(type)) {
            // 需要月份信息
            return calendar.get(Calendar.MONTH) + 1;
        } else if (DateEnum.WEEK.equals(type)) {
            // 需要周信息
            return calendar.get(Calendar.WEEK_OF_YEAR);
        } else if (DateEnum.DAY.equals(type)) {
            return calendar.get(Calendar.DAY_OF_MONTH);
        } else if (DateEnum.HOUR.equals(type)) {
            return calendar.get(Calendar.HOUR_OF_DAY);
        }
        throw new RuntimeException("没有类型:" + type);
    }

    /**
     * 获取time指定周的第一天的long时间值
     *
     */
    public static long getFirstDayOfWeek(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
