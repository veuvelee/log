package com.pojo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * location 组合类
 *
 */
public class StatsLocation extends Base {
    private StatsCommon statsCommon = new StatsCommon();
    private Location location = new Location();

    /**
     * 根据现有的location对象复制一个
     */
    public static StatsLocation clone(StatsLocation dimension) {
        StatsLocation newDimesnion = new StatsLocation();
        newDimesnion.statsCommon = StatsCommon.clone(dimension.statsCommon);
        newDimesnion.location = Location.getLocation(dimension.location.getCountry(), dimension.location.getProvince(), dimension.location.getCity());
        newDimesnion.location.setId(dimension.location.getId());
        return newDimesnion;
    }

    public StatsLocation() {
        super();
    }

    public StatsLocation(StatsCommon statsCommon, Location location) {
        super();
        this.statsCommon = statsCommon;
        this.location = location;
    }

    public StatsCommon getStatsCommon() {
        return statsCommon;
    }

    public void setStatsCommon(StatsCommon statsCommon) {
        this.statsCommon = statsCommon;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.statsCommon.write(out);
        this.location.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.statsCommon.readFields(in);
        this.location.readFields(in);
    }

    @Override
    public int compareTo(Base o) {
        StatsLocation other = (StatsLocation) o;
        int tmp = this.statsCommon.compareTo(other.statsCommon);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.location.compareTo(other.location);
        return tmp;
    }

}
