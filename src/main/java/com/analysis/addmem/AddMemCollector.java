package com.analysis.addmem;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.analysis.output.IDConverter;
import com.analysis.output.MyOutputCollector;
import com.common.GlobalEnum;
import com.pojo.Base;
import com.pojo.BaseWritable;
import com.pojo.MapWritableV;
import com.pojo.StatsUser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class AddMemCollector implements MyOutputCollector {

    @Override
    public void collect(Configuration conf, Base key, BaseWritable value, PreparedStatement pstmt, IDConverter converter) throws SQLException, IOException {
        StatsUser statsUser = (StatsUser) key;
        MapWritableV mapWritableV = (MapWritableV) value;
        int i = 0;
        // 设置参数
        switch (mapWritableV.getKpi()) {
        case NEW_MEMBER: // 统计add member的kpi
            IntWritable v1 = (IntWritable) mapWritableV.getValue().get(new IntWritable(-100));
            pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getPlatform()));
            pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getDate1()));
            pstmt.setInt(++i, v1.get());
            pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
            pstmt.setInt(++i, v1.get());
            break;
        case BROWSER_NEW_MEMBER: // 统计browser add member 的kpi
            IntWritable v2 = (IntWritable) mapWritableV.getValue().get(new IntWritable(-1));
            pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getPlatform()));
            pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getDate1()));
            pstmt.setInt(++i, converter.getId(statsUser.getBrowser()));
            pstmt.setInt(++i, v2.get());
            pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
            pstmt.setInt(++i, v2.get());
            break;
        case INSERT_MEMBER_INFO: // 插入member info信息
            Text v3 = (Text) mapWritableV.getValue().get(new IntWritable(-1));
            pstmt.setString(++i, v3.toString());
            pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
            pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
            pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
            break;
        default:
            throw new RuntimeException("不支持该种kpi输出操作");
        }
        pstmt.addBatch();
    }

}
