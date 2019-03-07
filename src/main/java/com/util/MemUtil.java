package com.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 * 操作member_info表，主要作用是判断memid是否是正常的id以及是否是一个新的访问会员id
 *
 */
public class MemUtil {
    private static Map<String, Boolean> map = new HashMap();

    /**
     * 删除指定日期的数据
     */
    public static void deleteMemByDate(String date, Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM `member_info` WHERE `created` = ?");
            pstmt.setString(1, date);
            pstmt.execute();
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e) {
                    // ...
                }
            }
        }

    }

    /**
     * 判断member id的格式是否正常，如果不正常，直接方面false。否则返回true。，
     *
     */
    public static boolean isGeShiMemberId(String memId) {
        if (StringUtils.isNotBlank(memId)) {
            return memId.trim().matches("[0-9a-zA-Z]{1,32}");
        }
        return false;
    }

    /**
     * 判断memid是否是一个新会员id，如果是，则返回true。否则返回false。
     *
     */
    public static boolean isNew(String memId, Connection conn) throws SQLException {
        Boolean isNewId = null;
        if (StringUtils.isNotBlank(memId)) {
            isNewId = map.get(memId);
            if (isNewId == null) {
                PreparedStatement pstmt = null;
                ResultSet rs = null;
                try {
                    pstmt = conn.prepareStatement("SELECT `member_id`,`last_visit_date` FROM `member_info` WHERE `member_id`=?");
                    pstmt.setString(1, memId);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        // memberid不是新的会员id
                        isNewId = Boolean.valueOf(false);
                    } else {
                        // 表示该memberid是新的的会员id
                        isNewId = Boolean.valueOf(true);
                    }
                    map.put(memId, isNewId);
                } finally {
                    if (rs != null) {
                        try {
                            //关
                            rs.close();
                        } catch (SQLException e) {
                            // ...
                        }
                    }
                    if (pstmt != null) {
                        try {
                            //关
                            pstmt.close();
                        } catch (SQLException e) {
                            // ...
                        }
                    }
                }
            }
        }
        if(isNewId){
            return true;
        }else{
            return false;
        }
    }
}
