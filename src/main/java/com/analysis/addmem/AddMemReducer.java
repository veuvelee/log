package com.analysis.addmem;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.common.KpiEnum;
import com.pojo.MapWritableV;
import com.pojo.Output;
import com.pojo.StatsUser;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**统计每一组value集合中的u_mid的唯一个数
 *
 */
public class AddMemReducer extends Reducer<StatsUser, Output, StatsUser, MapWritableV> {
    private Set<String> set = new HashSet();
    private MapWritableV outValue = new MapWritableV();
    private MapWritable map = new MapWritable();

    @Override
    protected void reduce(StatsUser key, Iterable<Output> values, Context context) throws IOException, InterruptedException {
        for (Output value : values) {
            set.add(value.getId());
        }

       outValue.setKpi(KpiEnum.INSERT_MEMBER_INFO);
        for (String id : set) {
            map.put(new IntWritable(-100), new Text(id));
            this.outValue.setValue(map);
            context.write(key, this.outValue);
        }

        // value指定
        this.map.put(new IntWritable(-100), new IntWritable(set.size()));
        this.outValue.setValue(this.map);
        // kpi指定
        this.outValue.setKpi(KpiEnum.valueOfName(key.getStatsCommon().getKpi().getKpiName()));
        context.write(key, this.outValue);
    }
}
