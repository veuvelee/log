package com.analysis.activemem;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.common.KpiEnum;
import com.pojo.MapWritableV;
import com.pojo.Output;
import com.pojo.StatsUser;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;


/**
 * 其实就是计算每一组中去重memberid然后计数
 */
public class ActiveMemReducer extends Reducer<StatsUser, Output, StatsUser, MapWritableV> {
    private Set<String> set = new HashSet();
    private MapWritableV outValue = new MapWritableV();
    private MapWritable map = new MapWritable();

    @Override
    protected void reduce(StatsUser key, Iterable<Output> values, Context context) throws IOException, InterruptedException {
        try {
            // 将memberid添加到set集合中去，方便进行统计memberid的去重个数
            for (Output value : values) {
                set.add(value.getId());
            }

            // 设置kpi
            this.outValue.setKpi(KpiEnum.valueOfName(key.getStatsCommon().getKpi().getKpiName()));
            // 设置value
            this.map.put(new IntWritable(-100), new IntWritable(set.size()));
            this.outValue.setValue(this.map);

            // 输出
            context.write(key, this.outValue);
        } finally {
            // 清空操作
            set.clear();
        }

    }
}
