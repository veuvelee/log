package com.util;

import com.common.LogEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.URLDecoder;
import java.util.Map;

public class LoggerUtil01 {
    private static final Logger logger = Logger.getLogger(LoggerUtil.class);
    private static IPSeeker ipSeekerExt = new IPSeeker();
    /**
     * 处理ip地址
     */
    public static void parseIp(Map<String,String> cliInfo) {
        if (cliInfo.containsKey(LogEnum.LOG_IP)) {
            String ip = cliInfo.get(LogEnum.LOG_IP);
            IPSeeker.RegionInfo info = ipSeekerExt.analyticIp(ip);
            if (info != null) {
                cliInfo.put(LogEnum.LOG_COUNTRY, info.getCountry());
                cliInfo.put(LogEnum.LOG_PROVINCE, info.getProvince());
                cliInfo.put(LogEnum.LOG_CITY, info.getCity());
            }
        }
    }

    /**
     * 处理浏览器的userAgent信息
     */
    public static void parseUserAgent(Map<String, String> cliInfo) {
        if (cliInfo.containsKey(LogEnum.LOG_USER_AGENT)) {
            UserAgentUtil.UserAgentInfo info = UserAgentUtil.analyticUserAgent(cliInfo.get(LogEnum.LOG_USER_AGENT));
            if (info != null) {
                cliInfo.put(LogEnum.LOG_OS_NAME, info.getOsName());
                cliInfo.put(LogEnum.LOG_OS_VERSION, info.getOsVersion());
                cliInfo.put(LogEnum.LOG_BROWSER_NAME, info.getBrowserName());
            }
        }
    }

    /**
     * 处理请求参数

     */
    //该方法处理'?'之后的参数,切分
    public static void parseRequest(String request, Map<String, String> cliInfo) {
        if (StringUtils.isNotBlank(request)) {
            //第一次分割,使用'&'进行分割
            String[] requestParams = request.split("&");
            for (String param : requestParams) {
                if (StringUtils.isNotBlank(param)) {
                    int index = param.indexOf("=");
                    if (index < 0) {
                        logger.warn("没法进行解析参数" );
                        continue;
                    }

                    String key = null, value = null;
                    try {
                        key = param.substring(0, index);
                        value = URLDecoder.decode(param.substring(index + 1), "utf-8");
                    } catch (Exception e) {
                        logger.warn("解码操作出现异常");
                        continue;
                    }
                    if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                        cliInfo.put(key, value);
                    }
                }
            }
        }
    }
}
