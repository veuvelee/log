package com.analysis.output;

import com.common.GlobalEnum;
import com.common.KpiEnum;
import com.pojo.Base;
import com.pojo.MapWritableV;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义数据输出
 *
 */
public class AnalysisRecordWriter extends RecordWriter<Base, MapWritableV> {
    private Connection conn = null;
    private Configuration conf = null;
    private IDConverter converter = null;
    private Map<KpiEnum, PreparedStatement> map = new HashMap();
    private Map<KpiEnum, Integer> batch = new HashMap();

    public AnalysisRecordWriter(Connection conn, Configuration conf, IDConverter converter) {
        super();
        this.conn = conn;
        this.conf = conf;
        this.converter = converter;
    }

    @Override
    /**
     * 这就相当于重写reduce中的write方法
     */
    public void write(Base key, MapWritableV value) throws IOException, InterruptedException {
        if (key == null || value == null) {
            return;
        }

        try {
            KpiEnum kpi = value.getKpi();
            PreparedStatement pstmt = null;//每一个pstmt对象对应一个sql语句
            int count = 1;//sql语句的批处理，一次执行10,可以在配置文件中指定
            if (map.get(kpi) == null) {
                // 使用kpi进行区分，返回sql保存到config中
                //也就是每一中kpi只会去配置文件中获取一条sql，第二个相同的kpi去执行else
                pstmt = this.conn.prepareStatement(conf.get(kpi.name));
                map.put(kpi, pstmt);
            } else {
                pstmt = map.get(kpi);
                count = batch.get(kpi);
                count++;
            }
            batch.put(kpi, count); // 记录每个kpi有多少个sql
            //在配置文件中有每个kpi的名字对应的collector的全路径限定名，可以通过反射得到类对象
            String collectorName = conf.get(kpi.name);
            Class<?> clazz = Class.forName(collectorName);
            MyOutputCollector collector = (MyOutputCollector) clazz.newInstance();//把value插入到mysql的方法。由于kpi维度不一样。插入到不能表里面。
            collector.collect(conf, key, value, pstmt, converter);
            //也就是每次执行多少条sql
            //先去配置文件中找，这里为10，如果配置文件中没有的话规定了一个默认值 50
            if (count % Integer.valueOf(conf.get(GlobalEnum.JDBC_BATCH_NUMBER, "50")) == 0) {
                pstmt.executeBatch();
                conn.commit();
                batch.put(kpi, 0); // 对应批量计算删除
            }
        } catch (Throwable e) {

            throw new IOException(e);
        }
    }


    /**
     * 关闭方法
     */
    @Override
    public void close(TaskAttemptContext context) throws IOException, InterruptedException {
        try {
            for (Map.Entry<KpiEnum, PreparedStatement> entry : this.map.entrySet()) {
                //在前边执行完后时如果还有没有执行的sql，可以在这里显示执行
                entry.getValue().executeBatch();
            }
        } catch (SQLException e) {
            throw new IOException(e);
        } finally {
            try {
                if (conn != null) {
                    conn.commit(); // 进行connection的提交动作
                }
            } catch (Exception e) {
                // 。。。
            } finally {
                for (Map.Entry<KpiEnum, PreparedStatement> entry : this.map.entrySet()) {
                    try {
                        entry.getValue().close();
                    } catch (SQLException e) {
                        // ...
                    }
                }
                if (conn != null)
                    try {
                        conn.close();
                    } catch (Exception e) {
                        // ...
                    }
            }
        }
    }
}