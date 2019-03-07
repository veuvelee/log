package com.analysis.pv;

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
import org.apache.hadoop.io.NullWritable;
import org.apache.log4j.Logger;

/**
 * 统计pv的mapper类
 *
 */
public class PageViewMapper extends TableMapper<StatsUser, NullWritable> {
    private static final Logger logger = Logger.getLogger(PageViewMapper.class);
    private StatsUser statsUser= new StatsUser();
    private Kpi webpv = new Kpi(KpiEnum.WEBSITE_PAGEVIEW.name);
    private byte[] family = Bytes.toBytes(LogEnum.EVENT_LOGS_FAMILY_NAME);

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {

        String platform = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_PLATFORM)));

        String serverTime = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_SERVER_TIME)));

        String url = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_URL)));

        // 过滤
        if (StringUtils.isBlank(platform) || StringUtils.isBlank(url) || StringUtils.isBlank(serverTime) || !StringUtils.isNumeric(serverTime.trim())) {
            return ;
        }

        List<Platform> platforms = Platform.buildList(platform);
        // 4. 创建browser维度信息
        String browserName = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_BROWSER_NAME)));



        Browser browser = Browser.build(browserName);

        Date1 dayOfDimenion = Date1.buildDate(Long.valueOf(serverTime.trim()), DateEnum.DAY);

        // 6. 输出的写出
        StatsCommon statsCommon = statsUser.getStatsCommon();
        statsCommon.setDate1(dayOfDimenion);
        statsCommon.setKpi(webpv);
        for (Platform pf : platforms) {
            statsCommon.setPlatform(pf);
            statsUser.setBrowser(browser);

            context.write(this.statsUser, NullWritable.get());

        }
    }
}
