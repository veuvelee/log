package com.analysis.adduser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.common.KpiEnum;
import com.pojo.MapWritableV;
import com.pojo.Output;
import com.pojo.StatsCommon;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;



/**
 * 计算新增用户的reduce类
 *
 */
public class AddlUserReducer extends Reducer<StatsCommon, Output, StatsCommon, MapWritableV> {
    private MapWritableV outValue = new MapWritableV();
    private Set<String> set = new HashSet<String>();

    @Override
    protected void reduce(StatsCommon key, Iterable<Output> values, Context context) throws IOException, InterruptedException {
        set.clear();

        // 计算uuid个数
        for (Output v : values) {
            set.add(v.getId());
        }
        
        MapWritable map = new MapWritable();//相当于HashMap
        //map里的key取值无所谓，只要能取出来就行
        map.put(new IntWritable(-100), new IntWritable(set.size()));
        outValue.setValue(map);

        // 设置kpi名称
        String kpiName = key.getKpi().getKpiName();
        if (KpiEnum.ADD_USER.name.equals(kpiName)) {
            // 计算stats_user表中的新增用户
            outValue.setKpi(KpiEnum.ADD_USER);
        } else if (KpiEnum.BROWSER_ADD_USER.name.equals(kpiName)) {
            // 计算stats_device_browser表中的新增用户
            outValue.setKpi(KpiEnum.BROWSER_ADD_USER);
        }
        context.write(key, outValue);
    }
}
