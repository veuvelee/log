package com.analysis.activemem;

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
 * 其实就是按照条件进行分组输出
 *
 */
public class ActiveMemMapper extends TableMapper<StatsUser, Output> {
    private static final Logger logger = Logger.getLogger(ActiveMemMapper.class);
    private StatsUser outKey = new StatsUser();
    private Output outValue = new Output();
    private Browser defaultBrowser = new Browser("");
    private Kpi activeMemKpi = new Kpi(KpiEnum.ACTIVE_MEM.name);
    private Kpi activeMemOfBrowserKpi = new Kpi(KpiEnum.BROWSER_ACTIVE_MEM.name);
    private byte[] family = Bytes.toBytes(LogEnum.EVENT_LOGS_FAMILY_NAME);

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        String memId = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_MEMBER_ID)));
        String platform = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_PLATFORM)));
        String serverTime = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_SERVER_TIME)));



        // 过滤
        if (StringUtils.isBlank(memId) || StringUtils.isBlank(platform) || StringUtils.isBlank(serverTime) || !StringUtils.isNumeric(serverTime.trim())) {
            return;
        }
        long longOfServerTime = Long.valueOf(serverTime.trim());
        Date1 date1 = Date1.buildDate(longOfServerTime, DateEnum.DAY);
        this.outValue.setId(memId);

        List<Platform> platforms = Platform.buildList(platform);
        String browserName = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_BROWSER_NAME)));
        Browser browser1 = Browser.build(browserName);
        StatsCommon statsCommon = outKey.getStatsCommon();
        statsCommon.setDate1(date1);
        for (Platform pf : platforms) {
            this.outKey.setBrowser(defaultBrowser);
            statsCommon.setPlatform(pf);
            statsCommon.setKpi(activeMemKpi);
            context.write(this.outKey, this.outValue);

            statsCommon.setKpi(activeMemOfBrowserKpi);
            outKey.setBrowser(browser1);
            context.write(this.outKey, this.outValue);

        }
    }
}
