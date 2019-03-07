package com.analysis.output;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pojo.Base;
import com.pojo.Browser;
import com.pojo.Date1;
import com.pojo.Platform;
import org.apache.log4j.Logger;


/**
 * id   mysql具体实现
 */
public class IDConverterImpl implements IDConverter {
    private static final Logger logger = Logger.getLogger(IDConverterImpl.class);
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://linux003:3306/log?useUnicode=true&amp;characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123";


    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            // 。。。
        }
    }

    @Override
    public int getId(Base base) throws IOException {


        Connection conn = null;
        try {
            // 1. 查看数据库中是否有对应的值，有则返回
            // 2. 如果第一步中，没有值；先插入数据， 来获取id
            String[] sql = null; // sql数组
            if (base instanceof Date1) {
                sql = this.buildDate1Sql();
            } else if (base instanceof Platform) {
                sql = this.buildPlatformSql();
            } else if (base instanceof Browser) {
                sql = this.buildBrowserSql();
            } else {
                throw new IOException("出错" );
            }

            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD); // 获取连接
            int id = 0;
            synchronized (this) {
                id = this.executeSql(conn,  sql, base);
            }
            return id;
        } catch (Throwable e) {
            logger.error("数据库异常");
            throw new IOException(e);
        } finally {
            if (conn != null) {
                try {
                    //关连接
                    conn.close();
                } catch (SQLException e) {
                    // ...
                }
            }
        }
    }


    /**
     * 设置参数
     *
     */
    private void setParams(PreparedStatement pstmt, Base base) throws SQLException {
        int i = 0;
        if (base instanceof Date1) {
            Date1 date = (Date1) base;
            pstmt.setInt(++i, date.getYear());
            pstmt.setInt(++i, date.getSeason());
            pstmt.setInt(++i, date.getMonth());
            pstmt.setInt(++i, date.getWeek());
            pstmt.setInt(++i, date.getDay());
            pstmt.setString(++i, date.getType());
            pstmt.setDate(++i, new Date(date.getCalendar().getTime()));
        } else if (base instanceof Platform) {
            Platform platform = (Platform) base;
            pstmt.setString(++i, platform.getPlatformName());
        } else if (base instanceof Browser) {
            Browser browser = (Browser) base;
            pstmt.setString(++i, browser.getBrowserName());
        }
    }

    /**
     * 创建date1相关sql
     */
    private String[] buildDate1Sql() {
        String querySql = "SELECT `id` FROM `dimension_date` WHERE `year` = ? AND `season` = ? AND `month` = ? AND `week` = ? AND `day` = ? AND `type` = ? AND `calendar` = ?";
        String insertSql = "INSERT INTO `dimension_date`(`year`, `season`, `month`, `week`, `day`, `type`, `calendar`) VALUES(?, ?, ?, ?, ?, ?, ?)";
        return new String[] { querySql, insertSql };
    }

    /**
     * 创建polatform 相关sql
     */
    private String[] buildPlatformSql() {
        String querySql = "SELECT `id` FROM `dimension_platform` WHERE `platform_name` = ?";
        String insertSql = "INSERT INTO `dimension_platform`(`platform_name`) VALUES(?)";
        return new String[] { querySql, insertSql };
    }

    /**
     * 创建browser 相关sql
     */
    private String[] buildBrowserSql() {
        //查询语句
        String querySql = "SELECT `id` FROM `dimension_browser` WHERE `browser_name` = ? AND `browser_version` = ?";
        //插入语句
        String insertSql = "INSERT INTO `dimension_browser`(`browser_name`) VALUES(?)";
        return new String[] { querySql, insertSql };
    }

    /**
     * 执行sql的方法
     */
    private int executeSql(Connection conn, String[] sqls, Base base) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sqls[0]);
            // 设置参数
            this.setParams(pstmt, base);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // 返回值
            }
            // 数据库中没有该条件，插入（java.sql.Statement.RETURN_GENERATED_KEYS：没懂）
            pstmt = conn.prepareStatement(sqls[1], java.sql.Statement.RETURN_GENERATED_KEYS);
            // 设置参数
            this.setParams(pstmt, base);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys(); // 获取返回的自动生成的id
            if (rs.next()) {
                return rs.getInt(1); // 获取返回的id
            }
        } finally {
            if (rs != null) {
                try {
                    //关
                    rs.close();
                } catch (Throwable e) {
                    // 。。。
                }
            }
            if (pstmt != null) {
                try {
                    //接着关
                    pstmt.close();
                } catch (Throwable e) {
                    //。。。
                }
            }
        }
        throw new RuntimeException("又出错了");
    }
}
