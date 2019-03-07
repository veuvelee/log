package com.analysis.activeUser;

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


public class ActiveUserCollector implements MyOutputCollector {

    @Override
    public void collect(Configuration conf, Base key, BaseWritable value,
                        PreparedStatement pstmt, IDConverter converter) throws SQLException, IOException {

        StatsUser statsUser = (StatsUser) key;
        IntWritable activeUser = (IntWritable) ((MapWritableV) value).getValue().get(new IntWritable(-1));

        // 进行参数设置
        int i = 0;
        pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getDate1()));
        pstmt.setInt(++i, activeUser.get());
        pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
        pstmt.setInt(++i, activeUser.get());

        pstmt.addBatch();
    }
}
