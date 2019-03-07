package com.pojo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.common.KpiEnum;
import org.apache.hadoop.io.MapWritable;



public class MapWritableV extends BaseWritable {

    private MapWritable value = new MapWritable();//即将插入数据库表中的一行记录
    private KpiEnum kpi;

    public KpiEnum getKpi() {
        return kpi;
    }

    public void setKpi(KpiEnum kpi) {
        this.kpi = kpi;
    }

    public MapWritableV() {
        super();
    }

    public MapWritableV(MapWritable value) {
        super();
        this.value = value;
    }

    public MapWritable getValue() {
        return value;
    }

    public void setValue(MapWritable value) {
        this.value = value;
    }



    @Override
    public void write(DataOutput out) throws IOException {
        this.value.write(out);

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.value.readFields(in);

    }



}
