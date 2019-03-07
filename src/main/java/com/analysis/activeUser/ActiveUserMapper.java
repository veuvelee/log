package com.analysis.activeUser;

import java.io.IOException;
import java.util.List;

import com.common.DateEnum;
import com.common.KpiEnum;
import com.common.LogEnum;
import com.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;



/**
 * Active user的mapper类
 *
 */
public class ActiveUserMapper extends TableMapper<StatsUser, Output> {
	private static final Logger logger = Logger.getLogger(ActiveUserMapper.class);
	private byte[] family = Bytes.toBytes(LogEnum.EVENT_LOGS_FAMILY_NAME);
	private StatsUser outKey = new StatsUser();
	private Output outValue = new Output();
	private Browser defaultBrowser = new Browser("");
	private Kpi activeUserKpi = new Kpi(KpiEnum.ACTIVE_USER.name);
	private Kpi activeUserOfBrowserKpi = new Kpi(KpiEnum.BROWSER_ACTIVE_USER.name);

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context)
			throws IOException, InterruptedException {
		// 获取uuid&platform&serverTime，
		String uuid = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_UUID)));
		String platform = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_PLATFORM)));
		String serverTime = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_SERVER_TIME)));

		// 过滤数据
		if (StringUtils.isBlank(uuid) || StringUtils.isBlank(platform)
				|| StringUtils.isBlank(serverTime) || !StringUtils.isNumeric(serverTime.trim())) {
			return;
		}

		this.outValue.setId(uuid);

		long LServerTime = Long.valueOf(serverTime.trim());

		//按天
		Date1 date1 = Date1.buildDate(LServerTime, DateEnum.DAY);


		List<Platform> platforms = Platform.buildList(platform);
		

		String browser = Bytes.toString(value.getValue(family, Bytes.toBytes(LogEnum.LOG_BROWSER_NAME)));

		Browser browser1 = Browser.build(browser);


		StatsCommon statsCommon = this.outKey.getStatsCommon();

		statsCommon.setDate1(date1);
		for (Platform pf : platforms) {
			this.outKey.setBrowser(defaultBrowser); // 覆盖

			statsCommon.setPlatform(pf);

			statsCommon.setKpi(activeUserKpi);
			context.write(this.outKey, this.outValue);


			statsCommon.setKpi(activeUserOfBrowserKpi);
			this.outKey.setBrowser(browser1);
			context.write(this.outKey, this.outValue);



		}
	}
}
