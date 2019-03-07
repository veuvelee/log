package com.pojo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;



/**
 * 进行用户分析(用户基本分析和浏览器分析)定义的组合维度
 *
 *
 */
public class StatsUser extends Base {
	
    private StatsCommon statsCommon = new StatsCommon();
    private Browser browser = new Browser();

    /**
     * close一个实例对象
     *
     */
    public static StatsUser clone(StatsUser statsUser) {
        Browser browser = new Browser(statsUser.browser.getBrowserName());
        StatsCommon statsCommon = StatsCommon.clone(statsUser.statsCommon);
        return new StatsUser(statsCommon, browser);
    }

    public StatsUser() {
        super();
    }

    public StatsUser(StatsCommon statsCommon, Browser browser) {
        super();
        this.statsCommon = statsCommon;
        this.browser = browser;
    }

    public StatsCommon getStatsCommon() {
        return statsCommon;
    }

    public void setStatsCommon(StatsCommon statsCommon) {
        this.statsCommon = statsCommon;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.statsCommon.write(out);
        this.browser.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.statsCommon.readFields(in);
        this.browser.readFields(in);
    }

    @Override
    public int compareTo(Base o) {
        if (this == o) {
            return 0;
        }

        StatsUser other = (StatsUser) o;
        int tmp = this.statsCommon.compareTo(other.statsCommon);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.browser.compareTo(other.browser);
        return tmp;
    }

}
