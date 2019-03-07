package com.dataproducer.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * 用户发送数据的http工具类
 *
 */
public class HttpUtil {

    /**
     * 具体发送url的方法
     *
     */
    public static void sendData(String url) throws IOException {
        HttpURLConnection con = null;
        BufferedReader in = null;

        try {
            URL obj = new URL(url); // 创建url对象
            con = (HttpURLConnection) obj.openConnection(); // 打开url连接
            // 设置连接参数
            con.setConnectTimeout(5000); // 连接过期时间
            con.setReadTimeout(5000); // 读取数据过期时间
            con.setRequestMethod("GET"); // 设置请求类型为get

            System.out.println("发送url:" + url);
            // 发送连接请求
            in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Throwable e) {
                // pass
            }
            try {
                con.disconnect();
            } catch (Throwable e) {
                // pass
            }
        }
    }


}
