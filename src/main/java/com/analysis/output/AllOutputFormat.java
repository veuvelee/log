package com.analysis.output;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.common.GlobalEnum;
import com.pojo.Base;
import com.pojo.MapWritableV;
import com.util.Jdbc;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;


/**
 * 自定义输出到mysql的outputformat类
 * Base:reducer输出的key
 * MapWritableV：reducer输出的value
 *
 * 泛型为父类，可以接受任意子类
 */
public class AllOutputFormat extends OutputFormat<Base, MapWritableV> {
    private static final Logger logger = Logger.getLogger(AllOutputFormat.class);

    /**
     * 这里定义的就是reduce的write格式
     */
    @Override
	public RecordWriter<Base, MapWritableV> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        Connection conn = null;
        IDConverter converter = new IDConverterImpl();
        try {
            conn = Jdbc.getConnection(conf, GlobalEnum.WAREHOUSE_OF_REPORT);
            //关闭自动提交
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error("数据库连接失败。数据库连接失败。数据库连接失败");
            throw new IOException("数据库连接失败。数据库连接失败。数据库连接失败");
        }
        return new AnalysisRecordWriter(conn, conf, converter);
    }

    @Override
    public void checkOutputSpecs(JobContext context) throws IOException, InterruptedException {
        // 检测输出空间，输出到mysql不用检测，没懂，真好
    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
        return new FileOutputCommitter(FileOutputFormat.getOutputPath(context), context);
    }



    }

