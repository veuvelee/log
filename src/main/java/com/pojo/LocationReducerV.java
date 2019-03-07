package com.pojo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import com.common.KpiEnum;
import org.apache.hadoop.io.WritableUtils;
/**
 * 自定义location统计中的reducer value输出类
 */
public class LocationReducerV extends BaseWritable {
    private KpiEnum kpi;
    private int uvs; // 活跃用户数
    private int visits; // 会话个数
    private int bounceNumber; // 跳出会话个数

    public void setKpi(KpiEnum kpi) {
        this.kpi = kpi;
    }

    public int getUvs() {
        return uvs;
    }

    public void setUvs(int uvs) {
        this.uvs = uvs;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public int getBounceNumber() {
        return bounceNumber;
    }

    public void setBounceNumber(int bounceNumber) {
        this.bounceNumber = bounceNumber;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.uvs);
        out.writeInt(this.visits);
        out.writeInt(this.bounceNumber);
        WritableUtils.writeEnum(out, this.kpi);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.uvs = in.readInt();
        this.visits = in.readInt();
        this.bounceNumber = in.readInt();
        this.kpi = WritableUtils.readEnum(in, KpiEnum.class);
    }


}
