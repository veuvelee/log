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

/**
 *
 *多了一个浏览器信息
 */
public class ActiveUserBrowserCollector implements MyOutputCollector {

    @Override
    public void collect(Configuration conf, Base key, BaseWritable value, PreparedStatement pstmt, IDConverter converter) throws SQLException, IOException {
     // 进行强制后获取对应的值
        StatsUser statsUser = (StatsUser) key;
        IntWritable activeUserValue = (IntWritable) ((MapWritableV) value).getValue().get(new IntWritable(-1));

        // 进行参数设置
        int i = 0;
        pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getId(statsUser.getStatsCommon().getDate1()));
        pstmt.setInt(++i, converter.getId(statsUser.getBrowser()));
        pstmt.setInt(++i, activeUserValue.get());
        pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
        pstmt.setInt(++i, activeUserValue.get());

        pstmt.addBatch();
    }

}
