package com.analysis.adduser;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.analysis.output.IDConverter;
import com.analysis.output.MyOutputCollector;
import com.common.GlobalEnum;
import com.pojo.Base;
import com.pojo.BaseWritable;
import com.pojo.MapWritableV;
import com.pojo.StatsCommon;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;


public class AddUserCollector implements MyOutputCollector {

    @Override
    public void collect(Configuration conf, Base base, BaseWritable value,
                        PreparedStatement pstmt, IDConverter converter)
    				throws SQLException, IOException {
        StatsCommon statsCommon = (StatsCommon) base;
        MapWritableV mapWritableV = (MapWritableV) value;
        //在这里取出的key就是reducer里定义的key
        IntWritable addUsers = (IntWritable) mapWritableV.getValue().get(new IntWritable(-100));

        int i = 0;
        //取平台
        pstmt.setInt(++i, converter.getId(statsCommon.getPlatform()));
        //取时间
        pstmt.setInt(++i, converter.getId(statsCommon.getDate1()));
        //取个数
        pstmt.setInt(++i, addUsers.get());
        //取运行时间
        pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
        pstmt.setInt(++i, addUsers.get());
        pstmt.addBatch();//放入批处理
    }

}
