package com.analysis.adduser;

import java.io.IOException;
import java.util.List;

import com.common.DateEnum;
import com.common.KpiEnum;
import com.common.LogEnum;
import com.pojo.*;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
/**
 * 自定义的计算新用户的mapper类
 *
 */
public class AddUserMapper extends TableMapper<StatsCommon, Output> {//每个分析条件（由各个维度组成的）作为key，uuid作为value
   
	private static final Logger logger = Logger.getLogger(AddUserMapper.class);

	//输出key类型
	private StatsCommon outKay = new StatsCommon();

	//输出value类型
	private Output outValue = new Output();
    //列族
    private byte[] family = Bytes.toBytes(LogEnum.EVENT_LOGS_FAMILY_NAME);
    
    //代表用户分析模块的统计
    private Kpi kpi = new Kpi(KpiEnum.ADD_USER.name);

    /**
     * map 读取hbase中的数据，输入数据为：hbase表中每一行。
     */
    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) 
    		throws IOException, InterruptedException {

        //从hbase中获取时间的值
        String date = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(LogEnum.LOG_SERVER_TIME))));
        long time = Long.valueOf(date);
        //构建时间维度
        Date1 date1 = Date1.buildDate(time, DateEnum.DAY);
        //获取平台维度的值
        String platform = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(LogEnum.LOG_PLATFORM))));
        //构建平台维度
        List<Platform> platformList = Platform.buildList(platform);
        //获取浏览器的名称
        String browserName = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(LogEnum.LOG_BROWSER_NAME))));
        //构建浏览器维度
        Browser browser = Browser.build(browserName);

        //获取用户唯一标识
        String uuid = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(LogEnum.LOG_UUID))));
        //给输出的value对象赋值
        outValue.setId(uuid);
        outValue.setTime(time);


        //设置时间维度
        outKay.setDate1(date1);

        //设置用户基本信息模块的维度组合
        for (Platform pl:platformList) {
            outKay.setKpi(kpi);
            outKay.setPlatform(pl);
            context.write(outKay, outValue);
        }
    }
}
