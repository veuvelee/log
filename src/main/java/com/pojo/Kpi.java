package com.pojo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 项目的功能模块维度
 */
public class Kpi extends Base {
	
    private int id;
    private String kpiName;

    public Kpi() {
        super();
    }

    public Kpi(String kpiName) {
        super();
        this.kpiName = kpiName;
    }

    public Kpi(int id, String kpiName) {
        super();
        this.id = id;
        this.kpiName = kpiName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.id);
        out.writeUTF(this.kpiName);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.kpiName = in.readUTF();
    }

    @Override
    public int compareTo(Base o) {
        if (this == o) {
            return 0;
        }

        Kpi other = (Kpi) o;
        int tmp = Integer.compare(this.id, other.id);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.kpiName.compareTo(other.kpiName);
        return tmp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((kpiName == null) ? 0 : kpiName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Kpi other = (Kpi) obj;
        if (id != other.id)
            return false;
        if (kpiName == null) {
            if (other.kpiName != null)
                return false;
        } else if (!kpiName.equals(other.kpiName))
            return false;
        return true;
    }

}
