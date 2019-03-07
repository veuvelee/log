package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.hadoop.conf.Configuration;

import com.common.GlobalEnum;

/**
 * jdbc管理
 */
public class Jdbc {
    /**
     * 根据配置获取获取关系型数据库的jdbc连接
     *  hadoopconf: hadoop配置信息
     *  flag:区分不同数据源
     */
    public static Connection getConnection(Configuration hadoopConf, String flag) throws SQLException {
        String driverStr = String.format(GlobalEnum.JDBC_DRIVER, flag);
        String urlStr = String.format(GlobalEnum.JDBC_URL, flag);
        String usernameStr = String.format(GlobalEnum.JDBC_USERNAME, flag);
        String passwordStr = String.format(GlobalEnum.JDBC_PASSWORD, flag);

        String driver = hadoopConf.get(driverStr);
        String url = hadoopConf.get(urlStr);
        String username = hadoopConf.get(usernameStr);
        String password = hadoopConf.get(passwordStr);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            // ...
        }
        return DriverManager.getConnection(url, username, password);
    }
}
