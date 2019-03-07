package com.pojo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.common.KpiEnum;
import org.apache.commons.lang.StringUtils;


/**字符串输出类
 */
public class TextOutputV extends BaseWritable {
    private KpiEnum kpiType;
    private String uuid; // 用户唯一标识符
    private String sid; // 会话id

    public TextOutputV() {
        super();
    }

    public TextOutputV(String uuid, String sid) {
        super();
        this.uuid = uuid;
        this.sid = sid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setKpiType(KpiEnum kpiType) {
        this.kpiType = kpiType;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.internalWriteString(out, this.uuid);
        this.internalWriteString(out, this.sid);
    }

    private void internalWriteString(DataOutput out, String value) throws IOException {
        if (StringUtils.isEmpty(value)) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeUTF(value);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.uuid = this.internalReadString(in);
        this.sid = this.internalReadString(in);
    }

    private String internalReadString(DataInput in) throws IOException {
        return in.readBoolean() ? in.readUTF() : null;
    }



}
