package com.analysis.location;

import java.io.IOException;
import java.util.List;

import com.common.DateEnum;
import com.common.KpiEnum;
import com.common.LogEnum;
import com.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;


/**
 * 统计location维度信息的mapper类
 * 输入: country、province、city、platform、servertime、uuid、sid
 * 一条输入对应6条输出

 *
 */
public class LocationMapper extends TableMapper<StatsLocation, TextOutputV> {
    private static final Logger logger = Logger.getLogger(LocationMapper.class);
    private StatsLocation statsLocation = new StatsLocation();
    private TextOutputV outValue = new TextOutputV();
    private Kpi locationKpi = new Kpi(KpiEnum.LOCATION.name);
    private byte[] family = Bytes.toBytes(LogEnum.EVENT_LOGS_FAMILY_NAME);
    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        // 获取平台名称、服务器时间、用户id、会话id
        // 获取uuid&platform&serverTime，
        String platform = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_PLATFORM)));
        String serverTime = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_SERVER_TIME)));
        String uuid = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_UUID)));
        String sessionid = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_SESSION_ID)));



        // 过滤
        if (StringUtils.isBlank(platform) || StringUtils.isBlank(uuid) || StringUtils.isBlank(sessionid) || StringUtils.isBlank(serverTime) || !StringUtils.isNumeric(serverTime.trim())) {
            return;
        }

        // 时间维度创建
        long LTime = Long.valueOf(serverTime.trim());
        Date1 dayOfDimension = Date1.buildDate(LTime, DateEnum.DAY);

        // platform维度创建
        List<Platform> platforms = Platform.buildList(platform);
        String country = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_COUNTRY)));
        String province = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_PROVINCE)));
        String city = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_CITY)));

        List<Location> locations = Location.buildList(country, province, city);

        // 进行输出定义
        outValue.setUuid(uuid);
        outValue.setSid(sessionid);
        StatsCommon statsCommon = statsLocation.getStatsCommon();
        statsCommon.setDate1(dayOfDimension);
        statsCommon.setKpi(locationKpi);
        for (Platform pf : platforms) {
            statsCommon.setPlatform(pf);

            for (Location location : locations) {
                this.statsLocation.setLocation(location);
                context.write(this.statsLocation, this.outValue);
            }
        }
    }
}
