package com.pojo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * 用户基本信息
 */
public class StatsCommon extends Base {
    private Date1 date1 = new Date1();
    private Platform platform = new Platform();
    private Kpi kpi = new Kpi();

    /**
     * 复制
     *
     */
    public static StatsCommon clone(StatsCommon dimension) {
        Date1 date1 = new Date1(dimension.date1.getId(), dimension.date1.getYear(), dimension.date1.getSeason(), dimension.date1.getMonth(), dimension.date1.getWeek(), dimension.date1.getDay(), dimension.date1.getType(), dimension.date1.getCalendar());
        Platform platform = new Platform(dimension.platform.getId(), dimension.platform.getPlatformName());
        Kpi kpi = new Kpi(dimension.kpi.getId(), dimension.kpi.getKpiName());
        return new StatsCommon(date1, platform, kpi);
    }

    public StatsCommon() {
        super();
    }

    public StatsCommon(Date1 date1, Platform platform, Kpi kpi) {
        super();
        this.date1 = date1;
        this.platform = platform;
        this.kpi = kpi;
    }

    public Date1 getDate1() {
        return date1;
    }

    public void setDate1(Date1 date1) {
        this.date1 = date1;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Kpi getKpi() {
        return kpi;
    }

    public void setKpi(Kpi kpi) {
        this.kpi = kpi;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.date1.write(out);
        this.platform.write(out);
        this.kpi.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.date1.readFields(in);
        this.platform.readFields(in);
        this.kpi.readFields(in);
    }

    @Override
    public int compareTo(Base o) {
        if (this == o) {
            return 0;
        }

        StatsCommon other = (StatsCommon) o;
        int tmp = this.date1.compareTo(other.date1);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.platform.compareTo(other.platform);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.kpi.compareTo(other.kpi);
        return tmp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date1 == null) ? 0 : date1.hashCode());
        result = prime * result + ((kpi == null) ? 0 : kpi.hashCode());
        result = prime * result + ((platform == null) ? 0 : platform.hashCode());
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
        StatsCommon other = (StatsCommon) obj;
        if (date1 == null) {
            if (other.date1 != null)
                return false;
        } else if (!date1.equals(other.date1))
            return false;
        if (kpi == null) {
            if (other.kpi != null)
                return false;
        } else if (!kpi.equals(other.kpi))
            return false;
        if (platform == null) {
            if (other.platform != null)
                return false;
        } else if (!platform.equals(other.platform))
            return false;
        return true;
    }

}
