package com.etl;

import java.io.IOException;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import com.common.LogEnum;
import com.common.GlobalEnum;
import com.util.TimeUtil;

/**
 * 编写mapreduce的runner类
 *
 */
public class AnalyserLogDataRunner  {

	public static void main(String[] args) {


		//创建conf对象
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://linux001:8020");
		conf.set("hbase.zookeeper.quorum", "linux002,linux003,linux004");
		conf = HBaseConfiguration.create(conf);
		//时间字符串
		String date = TimeUtil.getYesterday();// 分析昨天数据

		//设置变量RUN_DATE,值为该方法的变量值
		conf.set(GlobalEnum.RUN_DATE_NAME, date);
		Job job = null;
		try {
			job = Job.getInstance(conf);
		} catch (IOException e1) {
			//...
		}

		try {
			FileSystem fs = FileSystem.get(conf);
			//根据时间找路径，要把-去掉
			Path inputPath = new Path("/mylog/" + date.replace("-", "") + "/");

			if (fs.exists(inputPath)) {
				FileInputFormat.addInputPath(job, inputPath);
			} else {
				throw new RuntimeException("文件不存在:" + inputPath);
			}
		}catch (IOException e) {
			// nothing
		}

		job.setJarByClass(AnalyserLogDataRunner.class);
		job.setMapperClass(AnalyserLogDataMapper.class);
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(Put.class);
		// 1. 集群上运行，打成jar运行(要求addDependencyJars参数为true，默认就是true)
		// 2. 本地运行，要求参数addDependencyJars为false
		try {
			TableMapReduceUtil.initTableReducerJob(
					LogEnum.HBASE_NAME_LOGS, null, job, null, null,
					null, null, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		job.setNumReduceTasks(0);
		int a = 0;
		try {
			 a = job.waitForCompletion(true)?1:0;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(a ==1){
			System.out.println("执行成功");
		}else{
			System.out.println("执行失败");
		}



	}



}
