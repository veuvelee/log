package com.pojo;

import com.common.DateEnum;
import com.util.TimeUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Calendar;

/**
 * 时间维度信息类
 */
public class Date1 extends Base {
	
    private int id; // id
    private int year; // 年份
    private int season; // 季度
    private int month; // 月份
    private int week; // 周
    private int day;// 日
    private String type; // 类型
    private java.util.Date calendar = new java.util.Date();

    /**
     * 根据type类型获取对应的时间维度对象
     * 
     * time: 时间
     *  type: 类型
     */
    public static Date1 buildDate(long time, DateEnum type) {
        int year = TimeUtil.getDateInfo(time, DateEnum.YEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        if (DateEnum.YEAR.equals(type)) {
            calendar.set(year, 0, 1);
            return new Date1(year, 0, 0, 0, 0, type.name, calendar.getTime());
        }
        int season = TimeUtil.getDateInfo(time, DateEnum.SEASON);
        if (DateEnum.SEASON.equals(type)) {
            int month = (3 * season - 2);
            calendar.set(year, month - 1, 1);
            return new Date1(year, season, 0, 0, 0, type.name, calendar.getTime());
        }
        int month = TimeUtil.getDateInfo(time, DateEnum.MONTH);
        if (DateEnum.MONTH.equals(type)) {
            calendar.set(year, month - 1, 1);
            return new Date1(year, season, month, 0, 0, type.name, calendar.getTime());
        }
        int week = TimeUtil.getDateInfo(time, DateEnum.WEEK);
        if (DateEnum.WEEK.equals(type)) {
            long firstDayOfWeek = TimeUtil.getFirstDayOfWeek(time); // 获取指定时间戳所属周的第一天时间戳
            year = TimeUtil.getDateInfo(firstDayOfWeek, DateEnum.YEAR);
            season = TimeUtil.getDateInfo(firstDayOfWeek, DateEnum.SEASON);
            month = TimeUtil.getDateInfo(firstDayOfWeek, DateEnum.MONTH);
            week = TimeUtil.getDateInfo(firstDayOfWeek, DateEnum.WEEK);
            if (month == 12 && week == 1) {
                week = 53;
            }
            return new Date1(year, season, month, week, 0, type.name, new java.util.Date(firstDayOfWeek));
        }
        int day = TimeUtil.getDateInfo(time, DateEnum.DAY);
        if (DateEnum.DAY.equals(type)) {
            calendar.set(year, month - 1, day);
            if (month == 12 && week == 1) {
                week = 53;
            }
            return new Date1(year, season, month, week, day, type.name, calendar.getTime());
        }
        throw new RuntimeException("不支持" + type);
    }

    public Date1() {

        super();
    }

    public Date1(int year, int season, int month, int week, int day, String type) {
        super();
        this.year = year;
        this.season = season;
        this.month = month;
        this.week = week;
        this.day = day;
        this.type = type;
    }

    public Date1(int year, int season, int month, int week, int day, String type, java.util.Date calendar) {
        this(year, season, month, week, day, type);
        this.calendar = calendar;
    }

    public Date1(int id, int year, int season, int month, int week, int day, String type, java.util.Date calendar) {
        this(year, season, month, week, day, type, calendar);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public java.util.Date getCalendar() {
        return calendar;
    }

    public void setCalendar(java.util.Date calendar) {
        this.calendar = calendar;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.id);
        out.writeInt(this.year);
        out.writeInt(this.season);
        out.writeInt(this.month);
        out.writeInt(this.week);
        out.writeInt(this.day);
        out.writeUTF(this.type);
        out.writeLong(this.calendar.getTime());
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.year = in.readInt();
        this.season = in.readInt();
        this.month = in.readInt();
        this.week = in.readInt();
        this.day = in.readInt();
        this.type = in.readUTF();
        this.calendar.setTime(in.readLong());
    }

    @Override
    public int compareTo(Base o) {
        if (this == o) {
            return 0;
        }

        Date1 other = (Date1) o;
        int tmp = Integer.compare(this.id, other.id);
        if (tmp != 0) {
            return tmp;
        }

        tmp = Integer.compare(this.year, other.year);
        if (tmp != 0) {
            return tmp;
        }

        tmp = Integer.compare(this.season, other.season);
        if (tmp != 0) {
            return tmp;
        }

        tmp = Integer.compare(this.month, other.month);
        if (tmp != 0) {
            return tmp;
        }

        tmp = Integer.compare(this.week, other.week);
        if (tmp != 0) {
            return tmp;
        }

        tmp = Integer.compare(this.day, other.day);
        if (tmp != 0) {
            return tmp;
        }

        tmp = this.type.compareTo(other.type);
        return tmp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + day;
        result = prime * result + id;
        result = prime * result + month;
        result = prime * result + season;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + week;
        result = prime * result + year;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Date1 other = (Date1) obj;
        if (day != other.day)
            return false;
        if (id != other.id)
            return false;
        if (month != other.month)
            return false;
        if (season != other.season)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (week != other.week)
            return false;
        if (year != other.year)
            return false;
        return true;
    }
}
