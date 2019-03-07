package com.analysis.adduser;

import java.io.IOException;

import com.analysis.output.AllOutputFormat;
import com.pojo.MapWritableV;
import com.pojo.StatsCommon;
import com.pojo.Output;
import com.util.ScanUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.Logger;
import com.common.LogEnum;
import com.common.GlobalEnum;
import com.util.TimeUtil;

/**
 * 计算新增用户入口类
 *
 *
 */
public class AddUserRunner {


    /**
     * 入口main方法
     */
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(AddUserRunner.class);
        Configuration conf = new Configuration();
        conf.addResource("collector.xml");
        conf.addResource("mapping.xml");
        conf.addResource("mysql.xml");
        conf.set("fs.defaultFS", "hdfs://linux001:8020");
        conf.set("hbase.zookeeper.quorum", "linux004,linux003,linux002");
        conf.set(GlobalEnum.RUN_DATE_NAME, TimeUtil.getYesterday());
        conf = HBaseConfiguration.create(conf);
        Job job = null;
        try {
            job = Job.getInstance(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        job.setJarByClass(AddUserRunner.class);

        //设置要取出的列
        String[] col = new String[] {
                LogEnum.LOG_EVENT_NAME,
                LogEnum.LOG_UUID,
                LogEnum.LOG_SERVER_TIME,
                LogEnum.LOG_PLATFORM,
                LogEnum.LOG_BROWSER_NAME
        };
        // 本地运行
        try {
            TableMapReduceUtil.initTableMapperJob(
                    ScanUtil.Scans(job,col),
                    AddUserMapper.class,
                    StatsCommon.class,
                    Output.class,
                    job,
                    false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        job.setReducerClass(AddlUserReducer.class);
        job.setOutputKeyClass(StatsCommon.class);
        job.setOutputValueClass(MapWritableV.class);
        job.setOutputFormatClass(AllOutputFormat.class);
        try {
            if (job.waitForCompletion(true)) {
                TotalUser.totalUser(conf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    }



