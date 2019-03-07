package com.analysis.addmem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.util.MemUtil;
import com.common.DateEnum;
import com.common.GlobalEnum;
import com.common.KpiEnum;
import com.common.LogEnum;
import com.pojo.*;
import com.util.Jdbc;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;


public class AddMemMapper extends TableMapper<StatsUser, Output> {
    private static final Logger logger = Logger.getLogger(AddMemMapper.class);
    private StatsUser outKey = new StatsUser();
    private Output outValue = new Output();
    private Kpi addMemKpi = new Kpi(KpiEnum.NEW_MEMBER.name);
    private Kpi addMemBrowserKpi = new Kpi(KpiEnum.BROWSER_NEW_MEMBER.name);
    private Browser defBrowserDimension = new Browser("");
    private Connection conn = null;
    private byte[] family = Bytes.toBytes(LogEnum.EVENT_LOGS_FAMILY_NAME);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

        // 进行初始化操作
        Configuration conf = context.getConfiguration();
        try {
            this.conn = Jdbc.getConnection(conf, GlobalEnum.WAREHOUSE_OF_REPORT);
            // 删除指定日期的数据
            MemUtil.deleteMemByDate(conf.get(GlobalEnum.RUN_DATE_NAME), conn);
        } catch (SQLException e) {
            throw new IOException("数据库连接信息获取失败");
        }
    }

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {

        // 获取会员id
        String memId = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_MEMBER_ID)));

        //过滤， 判断memid是否是第一次访问
        try {
            if (StringUtils.isBlank(memId) || !MemUtil.isGeShiMemberId(memId) || !MemUtil.isNew(memId, conn)) {
                return;
            }
        } catch (Exception e) {

            throw new IOException("查询数据库出现异常");
        }

        // memberid是第一次访问，获取平台名称、服务器时间
        String platform = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_PLATFORM)));
        String serverTime = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_SERVER_TIME)));

        // 过滤
        if (StringUtils.isBlank(platform) || StringUtils.isBlank(serverTime) || !StringUtils.isNumeric(serverTime.trim())) {
            return;
        }
        long LTime = Long.valueOf(serverTime.trim());
        Date1 day = Date1.buildDate(LTime, DateEnum.DAY);
        List<Platform> platforms = Platform.buildList(platform);
        String browserName = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_BROWSER_NAME)));
        Browser browser = Browser.build(browserName);
        // 输出
        outValue.setId(memId);
        StatsCommon statsCommon = outKey.getStatsCommon();
        statsCommon.setDate1(day);
        for (Platform pf : platforms) {
            outKey.setBrowser(this.defBrowserDimension);
            statsCommon.setKpi(this.addMemKpi);
            statsCommon.setPlatform(pf);
            context.write(this.outKey, this.outValue);

            statsCommon.setKpi(this.addMemBrowserKpi);
            this.outKey.setBrowser(browser);
            context.write(this.outKey, this.outValue);

        }

    }
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        try {
            super.cleanup(context);
        } finally {
            // 关
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException e) {
                    //.............
                }
            }
        }
    }
}
