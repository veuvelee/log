package com.analysis.activemem;

import com.analysis.activeUser.ActiveUserRunner;
import com.analysis.output.AllOutputFormat;
import com.common.GlobalEnum;
import com.common.LogEnum;
import com.pojo.MapWritableV;
import com.pojo.Output;
import com.pojo.StatsUser;
import com.util.ScanUtil;
import com.util.TimeUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.Logger;
import java.io.IOException;

/**
 * 统计active member数量的执行入口类
 * 
 * @author root
 *
 */
public class ActiveMemRunner {


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
        String[] col = new String[] { LogEnum.LOG_MEMBER_ID, // 会员id
                LogEnum.LOG_SERVER_TIME, // 服务器时间
                LogEnum.LOG_PLATFORM, // 平台名称
                LogEnum.LOG_BROWSER_NAME, // 浏览器名称
                LogEnum.LOG_EVENT_NAME //事件名称
        };
        Job job = null;
        try {
            job = Job.getInstance(conf);
        } catch (IOException e) {
            //...
        }

        job.setJarByClass(ActiveMemRunner.class);

        try {
            TableMapReduceUtil.initTableMapperJob(ScanUtil.Scans(job, col), ActiveMemMapper.class, StatsUser.class, Output.class, job, false);
        } catch (IOException e) {
            e.printStackTrace();
        }


        job.setReducerClass( ActiveMemReducer.class);
        job.setOutputKeyClass(StatsUser.class);
        job.setOutputValueClass(MapWritableV.class);

        job.setOutputFormatClass(AllOutputFormat.class);
        // 开始毫秒数
        long startTime = System.currentTimeMillis();

        try {
            job.waitForCompletion(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


















}
