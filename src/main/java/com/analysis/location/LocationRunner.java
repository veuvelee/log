package com.analysis.location;

import com.analysis.activeUser.ActiveUserMapper;
import com.analysis.activeUser.ActiveUserReducer;
import com.analysis.activeUser.ActiveUserRunner;
import com.analysis.output.AllOutputFormat;
import com.common.GlobalEnum;
import com.common.LogEnum;
import com.pojo.*;
import com.util.ScanUtil;
import com.util.TimeUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.Logger;

import java.io.IOException;


/**
 * 统计location维度信息的入口类
 *
 */
public class LocationRunner {


    public static void main(String[] args) {
        Logger logger = Logger.getLogger(ActiveUserRunner.class);
        Configuration conf = null;
        conf.set("fs.defaultFS", "hdfs://linux001:8020");
        conf.set("hbase.zookeeper.quorum", "linux004,linux003,linux002");
        conf.addResource("mysql.xml");
        conf.addResource("mapping.xml");
        conf.addResource("collector.xml");
        conf = HBaseConfiguration.create(conf);
        String date = TimeUtil.getYesterday(); // 昨天
        conf.set(GlobalEnum.RUN_DATE_NAME, date);
        String[] col = new String[] { LogEnum.LOG_PLATFORM, // 平台
                LogEnum.LOG_SERVER_TIME, // 服务器时间
                LogEnum.LOG_UUID, // 用户id
                LogEnum.LOG_SESSION_ID, // 会话id
                LogEnum.LOG_COUNTRY, // 国家
                LogEnum.LOG_PROVINCE, // 省份
                LogEnum.LOG_CITY, // 城市
                LogEnum.LOG_EVENT_NAME, // 事件名称
        };
        Job job = null;
        try {
            job = Job.getInstance(conf);
        } catch (IOException e) {
            //...
        }

        job.setJarByClass(LocationRunner.class);

        try {
            TableMapReduceUtil.initTableMapperJob(ScanUtil.Scans(job, col), LocationMapper.class, StatsLocation.class, TextOutputV.class, job, false);
        } catch (IOException e) {
            e.printStackTrace();
        }


        job.setReducerClass(LocationReducer.class);
        job.setOutputKeyClass(StatsLocation.class);
        job.setOutputValueClass(LocationReducerV.class);
        job.setOutputFormatClass(AllOutputFormat.class);
        // 开始毫秒数
        long startTime = System.currentTimeMillis();

        try {
            job.waitForCompletion(true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


}
