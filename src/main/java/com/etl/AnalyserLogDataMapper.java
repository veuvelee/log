package com.etl;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import com.common.LogEnum;
import com.util.LoggerUtil;

/**
 * 自定义数据解析map类
 *
 */
public class AnalyserLogDataMapper extends Mapper<LongWritable, Text, NullWritable, Put> {
	
	//创建logger日志对象,并把该类设置到log对象中
	private final Logger logger = Logger.getLogger(AnalyserLogDataMapper.class);

	//指定列族的名称为log
	private byte[] family = Bytes.toBytes(LogEnum.EVENT_LOGS_FAMILY_NAME);
	


	//重写map方法,按偏移量进行读取每次读一行
	@Override
	protected void map(LongWritable key, Text value, Context context) {
		try {
			// 解析日志,将读取到的数据传进该方法中,方法返回一个map集合,返回的map集合中包含了ip,服务器时间,和?之后对应的key-value值
			Map<String, String> cliInfo = LoggerUtil.handleLog(value.toString());

			// 过滤解析失败的数据
			if (cliInfo.isEmpty()) {
				return;
			}

			// 获取事件名称
			String eventBieName = cliInfo.get(LogEnum.LOG_EVENT_NAME);
			LogEnum.EventEnum event = LogEnum.EventEnum.valueOfBieName(eventBieName);
			switch (event) {
			case LAUNCH:
			case PAGEVIEW:
			case CHARGEREQUEST:
			case CHARGEREFUND:
			case CHARGESUCCESS:
			case EVENT:
				this.parseData(cliInfo, event, context);
				break;
			default:
				this.logger.warn("事件没法解析");
			}
		} catch (Exception e) {
			this.logger.error("处理数据发出异常" );
		}
	}



	/**
	 * 具体处理数据的方法
	 *
	 */
	private void parseData(Map<String, String> cliInfo, LogEnum.EventEnum event,
			Context context) throws IOException, InterruptedException {
		String uuid = cliInfo.get(LogEnum.LOG_UUID);
		String memberId = cliInfo.get(LogEnum.LOG_MEMBER_ID);
		String serverTime = cliInfo.get(LogEnum.LOG_SERVER_TIME);
		if (StringUtils.isNotBlank(serverTime)) {
			// 要求服务器时间不为空
			cliInfo.remove(LogEnum.LOG_USER_AGENT); // 浏览器信息去掉
			String rowkey = serverTime; // 把时间作为rowkey

			Put put = new Put(Bytes.toBytes(rowkey));
			for (Map.Entry<String, String> entry : cliInfo.entrySet()) {
				if (StringUtils.isNotBlank(entry.getKey()) && StringUtils.isNotBlank(entry.getValue())) {
					put.add(family, Bytes.toBytes(entry.getKey()), Bytes.toBytes(entry.getValue()));
				}
			}
			context.write(NullWritable.get(), put);

		}
	}


}
