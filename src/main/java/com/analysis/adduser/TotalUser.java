package com.analysis.adduser;

import com.common.DateEnum;
import com.common.GlobalEnum;
import com.pojo.Date1;
import com.util.Jdbc;
import com.util.TimeUtil;
import org.apache.hadoop.conf.Configuration;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TotalUser {


    /**
     * 计算总用户
     */
    public static void totalUser(Configuration conf) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            long date = TimeUtil.parseDate_String2Long(conf.get(GlobalEnum.RUN_DATE_NAME));
            // 获取今天的date
            Date1 today = Date1.buildDate(date, DateEnum.DAY);
            // 获取昨天的date
            Date1 yesterday = Date1.buildDate(date - GlobalEnum.DAY_OF_MILLISECONDS, DateEnum.DAY);
            int yesterdayId = -2;
            int todayId = -2;

            // 1. 获取时间id
            conn = Jdbc.getConnection(conf, GlobalEnum.WAREHOUSE_OF_REPORT);
            // 获取昨天
            pstmt = conn.prepareStatement("SELECT `id` FROM `dimension_date` WHERE `year` = ? AND `season` = ? AND `month` = ? AND `week` = ? AND `day` = ? AND `type` = ? AND `calendar` = ?");
            int i = 0;
            pstmt.setInt(++i, yesterday.getYear());
            pstmt.setInt(++i, yesterday.getSeason());
            pstmt.setInt(++i, yesterday.getMonth());
            pstmt.setInt(++i, yesterday.getWeek());
            pstmt.setInt(++i, yesterday.getDay());
            pstmt.setString(++i, yesterday.getType());
            pstmt.setDate(++i, new Date(yesterday.getCalendar().getTime()));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                yesterdayId = rs.getInt(1);
            }

            // 获取执行时间当天的id
            pstmt = conn.prepareStatement("SELECT `id` FROM `date1` WHERE `year` = ? AND `season` = ? AND `month` = ? AND `week` = ? AND `day` = ? AND `type` = ? AND `calendar` = ?");
            i = 0;
            pstmt.setInt(++i, today.getYear());
            pstmt.setInt(++i, today.getSeason());
            pstmt.setInt(++i, today.getMonth());
            pstmt.setInt(++i, today.getWeek());
            pstmt.setInt(++i, today.getDay());
            pstmt.setString(++i, today.getType());
            pstmt.setDate(++i, new Date(today.getCalendar().getTime()));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                todayId = rs.getInt(1);
            }

            // 2.获取昨天的原始数据
            Map<String, Integer> old = new HashMap();
            // 开始更新stats_user
            if (yesterdayId > -2) {
                pstmt = conn.prepareStatement("select `platform_id`,`total_add_users` from `stats_user` where `date1_id`=?");
                pstmt.setInt(1, yesterdayId);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    int platformId = rs.getInt("platform_id");
                    int totalUser = rs.getInt("total_add_users");
                    old.put("" + platformId, totalUser);
                }
            }

            // 添加今天的总用户
            pstmt = conn.prepareStatement("select `platform_id`,`new_add_users` from `stats_user` where `date1_id`=?");
            pstmt.setInt(1, todayId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int platformId = rs.getInt("platform_id");
                int newUser = rs.getInt("new_add_users");
                if (old.containsKey("" + platformId)) {
                    newUser += old.get("" + platformId);
                }
                old.put("" + platformId, newUser);
            }

            // 更新操作
            pstmt = conn.prepareStatement("INSERT INTO `stats_user`(`platform_id`,`date1_id`,`total_add_users`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `total_install_users` = ?");
            for (Map.Entry<String, Integer> entry : old.entrySet()) {
                pstmt.setInt(1, Integer.valueOf(entry.getKey()));
                pstmt.setInt(2, todayId);
                pstmt.setInt(3, entry.getValue());
                pstmt.setInt(4, entry.getValue());
                pstmt.execute();
            }

            // 开始更新stats_device_browser
            old.clear();
            if (yesterdayId > -1) {
                pstmt = conn.prepareStatement("select `platform_id`,`browser_id`,`total_add_users` from `stats_device_browser` where `date1_id`=?");
                pstmt.setInt(1, yesterdayId);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    int platformId = rs.getInt("platform_id");
                    int browserId = rs.getInt("browser_id");
                    int totalUsers = rs.getInt("total_add_users");
                    old.put(platformId + "_" + browserId, totalUsers);
                }
            }

            // 添加今天的总用户
            pstmt = conn.prepareStatement("select `platform_id`,`browser_id`,`new_add_users` from `stats_device_browser` where `date1_id`=?");
            pstmt.setInt(1, todayId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int platformId = rs.getInt("platform_id");
                int browserId = rs.getInt("browser_id");
                int newUsers = rs.getInt("new_add_users");
                String key = platformId + "_" + browserId;
                if (old.containsKey(key)) {
                    newUsers += old.get(key);
                }
                old.put(key, newUsers);
            }

            // 更新操作
            pstmt = conn.prepareStatement("INSERT INTO `stats_device_browser`(`platform_id`,`browser_id`,`date1_id`,`total_add_users`) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE `total_add_users` = ?");
            for (Map.Entry<String, Integer> entry : old.entrySet()) {
                String[] key = entry.getKey().split("_");
                pstmt.setInt(1, Integer.valueOf(key[0]));
                pstmt.setInt(2, Integer.valueOf(key[1]));
                pstmt.setInt(3, todayId);
                pstmt.setInt(4, entry.getValue());
                pstmt.setInt(5, entry.getValue());
                pstmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
