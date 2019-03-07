package com.analysis.pv;

import java.io.IOException;

import com.common.KpiEnum;
import com.pojo.MapWritableV;
import com.pojo.StatsUser;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

/**
 *
 * 不用去重，直接统计输入到reducer的同一组key中，value的个数。
 *
 */
public class PageViewReducer extends Reducer<StatsUser, NullWritable, StatsUser, MapWritableV> {
    private MapWritableV outValue = new MapWritableV();
    private MapWritable map = new MapWritable();

    @Override
    protected void reduce(StatsUser key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        int pvCount = 0;
        for (NullWritable value : values) {
            pvCount++;
        }
        

       map.put(new IntWritable(-100), new IntWritable(pvCount));
       outValue.setValue(this.map);


        outValue.setKpi(KpiEnum.valueOfName(key.getStatsCommon().getKpi().getKpiName()));


        context.write(key, outValue);
    }
}
