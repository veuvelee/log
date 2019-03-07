package com.dataproducer.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class Utils {
    /**
     * 根据传入的参数构建url
     */
    public static String buildUrl(Map<String, String> data,String url)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (isNotEmpty(entry.getKey()) && isNotEmpty(entry.getValue())) {
                sb.append(entry.getKey().trim())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue().trim(), "utf-8"))
                        .append("&");
            }
        }
        return sb.substring(0, sb.length() - 1);// 去掉最后&
    }

    /**
     * 判断字符串是否为空，如果为空，返回true。否则返回false。
     *
     */
    public static boolean isEmpty(String value) {

        return value == null || value.trim().isEmpty();
    }

    /**
     * 判断字符串是否非空，如果不是空，返回true。如果是空，返回false。
     */
    public static boolean isNotEmpty(String value) {

        return !isEmpty(value);
    }
}
