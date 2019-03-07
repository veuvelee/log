package com.analysis.output;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.pojo.Base;
import com.pojo.BaseWritable;
import org.apache.hadoop.conf.Configuration;


/**
 * 自定义的配合自定义output进行具体sql输出的类
 *
 *
 */
public interface MyOutputCollector {

    /**
     * 具体执行统计数据插入的方法
     */
    public void collect(Configuration conf, Base base, BaseWritable value, PreparedStatement pstmt, IDConverter converter) throws SQLException, IOException;
}
