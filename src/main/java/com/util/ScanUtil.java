package com.util;

import com.common.GlobalEnum;
import com.common.LogEnum;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;

import java.util.List;

public class ScanUtil {



    /**
     * scan集合
     */
    public static List<Scan> Scans(Job job,String[] col) {
        Configuration conf = job.getConfiguration();
        // 获取运行时间: yyyy-MM-dd
        String date = conf.get(GlobalEnum.RUN_DATE_NAME);
        long runDate = TimeUtil.parseDate_String2Long(date);
        long startDate = runDate - GlobalEnum.DAY_OF_MILLISECONDS;
        long endDate = runDate;


        Scan scan = new Scan();
        // 定义hbase扫描的开始rowkey和结束rowkey
        scan.setStartRow(Bytes.toBytes("" + startDate));
        scan.setStopRow(Bytes.toBytes("" + endDate));

        FilterList filterList = new FilterList();
        // 过滤数据，只分析launch事件
        filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes(LogEnum.EVENT_LOGS_FAMILY_NAME), Bytes.toBytes(LogEnum.LOG_EVENT_NAME), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(LogEnum.EventEnum.LAUNCH.bieName)));
        // 定义mapper中需要获取的列名

        filterList.addFilter(getColumnFilter(col));

        //指定表明
        scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, Bytes.toBytes(LogEnum.HBASE_NAME_LOGS));
        scan.setFilter(filterList);
        return Lists.newArrayList(scan);
    }

    /**
     * 用来获取指定的列
     */
    public static Filter getColumnFilter(String[] columns) {
        int length = columns.length;
        byte[][] filter = new byte[length][];
        for (int i = 0; i < length; i++) {
            filter[i] = Bytes.toBytes(columns[i]);
        }
        return new MultipleColumnPrefixFilter(filter);
    }
}
