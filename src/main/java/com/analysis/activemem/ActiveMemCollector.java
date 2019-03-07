package com.analysis.activemem;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.analysis.output.IDConverter;
import com.analysis.output.MyOutputCollector;
import com.common.GlobalEnum;
import com.common.KpiEnum;
import com.pojo.Base;
import com.pojo.BaseWritable;
import com.pojo.MapWritableV;
import com.pojo.StatsUser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
/*
 * 定义具体的activemem kpi的输出类
 *
 */
public class ActiveMemCollector implements MyOutputCollector {

    @Override
    public void collect(Configuration conf, Base key, BaseWritable value, PreparedStatement pstmt, IDConverter converter) throws SQLException, IOException {

        StatsUser statsUser = (StatsUser) key;
        IntWritable activeMembers = (IntWritable) ((MapWritableV) value).getValue().get(new IntWritable(-100));

        int i = 0;
        pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getDate1()));
        if (KpiEnum.BROWSER_ACTIVE_USER.name.equals(statsUser.getStatsCommon().getKpi().getKpiName())) {
            // 表示输出结果是统计browser active member的，那么进行browser条件设置
            pstmt.setInt(++i, converter.getId(statsUser.getBrowser()));
        }
        pstmt.setInt(++i, activeMembers.get());
        pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
        pstmt.setInt(++i, activeMembers.get());


        pstmt.addBatch();
    }

}
