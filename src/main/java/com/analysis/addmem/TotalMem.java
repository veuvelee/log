package com.analysis.addmem;

import com.common.DateEnum;
import com.common.GlobalEnum;
import com.pojo.Date1;
import com.util.Jdbc;
import com.util.TimeUtil;
import jdk.nashorn.internal.runtime.GlobalConstants;
import org.apache.hadoop.conf.Configuration;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TotalMem {

    /**
     * 计算总会员
     */
    public static void getTotalMembers(Configuration conf) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            long date = TimeUtil.parseDate_String2Long(conf.get(GlobalEnum.RUN_DATE_NAME));
            // 获取今天的date dimension
            Date1 today = Date1.buildDate(date, DateEnum.DAY);
            // 获取昨天的date dimension
            Date1 yesterday = Date1.buildDate(date - GlobalEnum.DAY_OF_MILLISECONDS, DateEnum.DAY);
            int yesterdayId = -1;
            int todayId = -1;

            // 1. 获取时间id
            conn = Jdbc.getConnection(conf, GlobalEnum.WAREHOUSE_OF_REPORT);
            // 获取执行时间的昨天的
            pstmt = conn.prepareStatement("SELECT `id` FROM `date` WHERE `year` = ? AND `season` = ? AND `month` = ? AND `week` = ? AND `day` = ? AND `type` = ? AND `calendar` = ?");
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
            pstmt = conn.prepareStatement("SELECT `id` FROM `date` WHERE `year` = ? AND `season` = ? AND `month` = ? AND `week` = ? AND `day` = ? AND `type` = ? AND `calendar` = ?");
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


            Map<String, Integer> oldValueMap = new HashMap();

            // 更新stats_user
            if (yesterdayId > -1) {
                pstmt = conn.prepareStatement("select `platform_id`,`total_members` from `stats_user` where `date_id`=?");
                pstmt.setInt(1, yesterdayId);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    int platformId = rs.getInt("platform_id");
                    int totalMembers = rs.getInt("total_members");
                    oldValueMap.put("" + platformId, totalMembers);
                }
            }

            // 添加今天的总用户
            pstmt = conn.prepareStatement("select `platform_id`,`new_members` from `stats_user` where `date_id`=?");
            pstmt.setInt(1, todayId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int platformId = rs.getInt("platform_id");
                int newMembers = rs.getInt("new_members");
                if (oldValueMap.containsKey("" + platformId)) {
                    newMembers += oldValueMap.get("" + platformId);
                }
                oldValueMap.put("" + platformId, newMembers);
            }

            // 更新操作
            pstmt = conn.prepareStatement("INSERT INTO `stats_user`(`platform_id`,`date_id`,`total_members`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `total_members` = ?");
            for (Map.Entry<String, Integer> entry : oldValueMap.entrySet()) {
                pstmt.setInt(1, Integer.valueOf(entry.getKey()));
                pstmt.setInt(2, todayId);
                pstmt.setInt(3, entry.getValue());
                pstmt.setInt(4, entry.getValue());
                pstmt.execute();
            }

            // 更新stats_device_browser
            oldValueMap.clear();
            if (yesterdayId > -1) {
                pstmt = conn.prepareStatement("select `platform_id`,`browser_id`,`total_members` from `stats_device_browser` where `date_id`=?");
                pstmt.setInt(1, yesterdayId);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    int platformId = rs.getInt("platform__id");
                    int browserId = rs.getInt("browser_id");
                    int totalMembers = rs.getInt("total_members");
                    oldValueMap.put(platformId + "_" + browserId, totalMembers);
                }
            }

            // 添加今天的总用户
            pstmt = conn.prepareStatement("select `platform_id`,`browser_id`,`new_members` from `stats_device_browser` where `date_id`=?");
            pstmt.setInt(1, todayId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int platformId = rs.getInt("platform_id");
                int browserId = rs.getInt("browser_id");
                int newMembers = rs.getInt("new_members");
                String key = platformId + "_" + browserId;
                if (oldValueMap.containsKey(key)) {
                    newMembers += oldValueMap.get(key);
                }
                oldValueMap.put(key, newMembers);
            }

            // 更新
            pstmt = conn.prepareStatement("INSERT INTO `stats_device_browser`(`platform_id`,`browser_id`,`date_id`,`total_members`) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE `total_members` = ?");
            for (Map.Entry<String, Integer> entry : oldValueMap.entrySet()) {
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
