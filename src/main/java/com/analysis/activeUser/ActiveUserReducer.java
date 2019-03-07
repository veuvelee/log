package com.analysis.activeUser;

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
 * 统计active user， 就是计算本一组中这个uuid的个数
 *
 */
public class ActiveUserReducer extends Reducer<StatsUser, Output, StatsUser, MapWritableV> {
	
	private Set<String> set = new HashSet();
	private MapWritableV outValue = new MapWritableV();
	private MapWritable map = new MapWritable();

	@Override
	protected void reduce(StatsUser key, Iterable<Output> values, Context context)
			throws IOException, InterruptedException {
		try {
			// 将uuid添加到set集合中去，可以去重
			for (Output value : values) {
				set.add(value.getId());
			}


			outValue.setKpi(KpiEnum.valueOfName(key.getStatsCommon().getKpi().getKpiName()));

			map.put(new IntWritable(-100), new IntWritable(set.size()));
			outValue.setValue(this.map);


			context.write(key, outValue);
		} finally {
			// 清空操作
			set.clear();
		}

	}
}
