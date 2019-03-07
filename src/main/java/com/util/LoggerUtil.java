package com.util;


import java.util.HashMap;
import java.util.Map;

import com.common.LogEnum;

import org.apache.commons.lang.StringUtils;




/**
 * 处理日志数据的具体工作类
 *
 */
public class LoggerUtil {


    /**
     * 处理日志数据str，返回处理结果map集合
     * 如果str没有指定数据格式，那么直接返回empty的集合
     *
     */
    public static Map<String, String> handleLog(String str) {
    	
    	//实例化一个map集合
        Map<String, String> cliInfo = new HashMap<String, String>();
        
        //先判断数据是否为空,
        if (StringUtils.isNotBlank(str)) {
        	
        	//通过\t对读取到数据进行切割
            String[] strings = str.trim().split(LogEnum.LOG_SEPARTIOR);
            

            //数据切割后有四个部分
            if (strings.length == 4) {
                // 日志格式为: ip\t服务器时间\thost\t请求参数
                cliInfo.put(LogEnum.LOG_IP, strings[0].trim()); // 设置ip
                // 设置服务器时间
                cliInfo.put(LogEnum.LOG_SERVER_TIME, String.valueOf(TimeUtil.parseNginx_Time2Long(strings[1].trim())));
                
                //从第三个切割后的数据中,先获得'?'的下标
                int index = strings[3].indexOf("?");
                
                
                if (index > -1) {
                    String request = strings[3].substring(index + 1); // 获取请求参数，也就是数据
                    // 处理请求参数
                    LoggerUtil01.parseRequest(request, cliInfo);
                    // 处理userAgent
                    LoggerUtil01.parseUserAgent(cliInfo);
                    // 处理ip地址
                    LoggerUtil01.parseIp(cliInfo);
                } else {
                    // 数据格式异常
                    cliInfo.clear();
                }
            }
        }
        return cliInfo;
    }

}
