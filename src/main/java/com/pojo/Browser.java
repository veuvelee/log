package com.pojo;

import com.common.GlobalEnum;
import org.apache.commons.lang.StringUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 浏览器维度
 */
public class Browser extends Base {
    private int id; // id
    private String browserName; // 名称


    public Browser() {
        super();
    }

    public Browser(String browserName) {
        super();
        this.browserName = browserName;
    }
    public void clean() {
        this.id = 0;
        this.browserName = "";
    }

    public static Browser getInstance(String browserName) {
        Browser browser = new Browser();
        browser.browserName = browserName;

        return browser;
    }

    /**
     * 构建多个浏览器维度信息对象集合
     */
    public static Browser build(String browserName) {

        if (StringUtils.isBlank(browserName)) {
            // 浏览器名称为空，那么设置为unknown
            browserName = GlobalEnum.DEFAULT_VALUE;

        }


        return  Browser.getInstance(browserName);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }





    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.id);
        out.writeUTF(this.browserName);

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.browserName = in.readUTF();

    }

    @Override
    public int compareTo(Base o) {
        if (this == o) {
            return 0;
        }

        Browser other = (Browser) o;
        int tmp = Integer.compare(this.id, other.id);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.browserName.compareTo(other.browserName);
        if (tmp != 0) {
            return tmp;
        }

        return tmp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((browserName == null) ? 0 : browserName.hashCode());
        result = prime * result + id;
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
        Browser other = (Browser) obj;
        if (browserName == null) {
            if (other.browserName != null)
                return false;
        } else if (!browserName.equals(other.browserName))
            return false;

        if (id != other.id)
            return false;
        return true;
    }
}
