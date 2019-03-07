package com.analysis.pv;

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
/**
 * pv计算的具体输出
 */
public class PageViewCollector implements MyOutputCollector {

    @Override
    public void collect(Configuration conf, Base key, BaseWritable value, PreparedStatement pstmt, IDConverter converter) throws SQLException, IOException {
        StatsUser statsUser = (StatsUser) key;
        int pv = ((IntWritable) ((MapWritableV) value).getValue().get(new IntWritable(-1))).get();

        int i = 0;
        pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getDate1()));
        pstmt.setInt(++i, converter.getId(statsUser.getBrowser()));
        pstmt.setInt(++i, pv);
        pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
        pstmt.setInt(++i, pv);
        pstmt.addBatch();
    }

}
