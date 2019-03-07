package com.analysis.location;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.common.KpiEnum;
import com.pojo.LocationReducerV;
import com.pojo.StatsLocation;
import com.pojo.TextOutputV;
import org.apache.hadoop.mapreduce.Reducer;


public class LocationReducer extends Reducer<StatsLocation, TextOutputV, StatsLocation, LocationReducerV> {
    private Set<String> set = new HashSet();
    private Map<String, Integer> map = new HashMap();
    private LocationReducerV outValue = new LocationReducerV();

    @Override
    protected void reduce(StatsLocation key, Iterable<TextOutputV> values, Context context) throws IOException, InterruptedException {
        try {
            for (TextOutputV value : values) {
                String uuid = value.getUuid();
                String sid = value.getSid();


                set.add(uuid);
                if (map.containsKey(sid)) {
                    // 表示该sid已经有访问过的数据
                    map.put(sid, 2);
                } else {
                    // 表示该sid是第一次访问
                    map.put(sid, 1);
                }
            }

            // 输出对象的创建
            this.outValue.setKpi(KpiEnum.valueOfName(key.getStatsCommon().getKpi().getKpiName()));
            this.outValue.setUvs(set.size());
            this.outValue.setVisits(map.size());
            int i = 0;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == 1) {
                    i++;
                }
            }
            this.outValue.setBounceNumber(i);

            // 输出
            context.write(key, this.outValue);
        } finally {
            // 清空操作
            set.clear();
            map.clear();
        }

    }
}
