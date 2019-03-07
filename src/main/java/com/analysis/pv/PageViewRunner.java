package com.analysis.pv;

import com.analysis.activeUser.ActiveUserRunner;
import com.analysis.activemem.ActiveMemRunner;
import com.analysis.output.AllOutputFormat;
import com.common.GlobalEnum;
import com.common.LogEnum;
import com.pojo.MapWritableV;
import com.pojo.StatsUser;
import com.util.ScanUtil;
import com.util.TimeUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.Logger;
import java.io.IOException;

/**
 *
 * 只获取pv事件的数据
 * 将计算得到的pv值保存到mysql中。
 *
 */
public class PageViewRunner  {


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
        String[] col = new String[] { LogEnum.LOG_EVENT_NAME, // 获取事件名称
                LogEnum.LOG_URL, // 当前url
                LogEnum.LOG_SERVER_TIME, // 服务器时间
                LogEnum.LOG_PLATFORM, // 平台名称
                LogEnum.LOG_BROWSER_NAME // 浏览器名称
        };
        Job job = null;
        try {
            job = Job.getInstance(conf);
        } catch (IOException e) {
            //...
        }

        job.setJarByClass(ActiveMemRunner.class);

        try {
            TableMapReduceUtil.initTableMapperJob(ScanUtil.Scans(job, col), PageViewMapper.class, StatsUser.class, NullWritable.class, job, false);
        } catch (IOException e) {
            e.printStackTrace();
        }


        job.setReducerClass(PageViewReducer.class);
        job.setOutputKeyClass(StatsUser.class);
        job.setOutputValueClass(MapWritableV.class);
        job.setOutputFormatClass(AllOutputFormat.class);
        try {
            job.waitForCompletion(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
