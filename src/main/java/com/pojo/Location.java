package com.pojo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.common.GlobalEnum;
import org.apache.commons.lang.StringUtils;

/**
 * location表对应的model类

 *
 */
public class Location extends Base {
    private int id;//id
    private String country;//国
    private String province;//省
    private String city;//市

    public Location() {
        super();
        this.clean();
    }

    public Location(int id, String country, String province, String city) {
        super();
        this.id = id;
        this.country = country;
        this.province = province;
        this.city = city;
    }

    public void clean() {
        this.id = 0;
        this.country = "";
        this.province = "";
        this.city = "";
    }

    /**
     * 构造方法
     *
     */
    public static Location getLocation(String country, String province, String city) {
        Location location = new Location();
        location.country = country;
        location.province = province;
        location.city = city;
        return location;
    }


    public static List<Location> buildList(String country, String province, String city) {
        List<Location> list = new ArrayList<Location>();
        if (StringUtils.isBlank(country) || GlobalEnum.DEFAULT_VALUE.equals(country)) {
            // 国家名称为空，将所有的unknown；
            country = province = city = GlobalEnum.DEFAULT_VALUE;
        }
        if (StringUtils.isBlank(province) || GlobalEnum.DEFAULT_VALUE.equals(province)) {
            // 省份名称为空，那么将city 和province设置为unknown
            province = city = GlobalEnum.DEFAULT_VALUE;
        }
        if (StringUtils.isBlank(city)) {
            // 城市名称为空，设置为unknown
            city = GlobalEnum.DEFAULT_VALUE;
        }
        //这样做可以按省分组，按时分组，按国分组
        list.add(Location.getLocation(country, GlobalEnum.VALUE_OF_ALL, GlobalEnum.VALUE_OF_ALL));
        list.add(Location.getLocation(country, province, GlobalEnum.VALUE_OF_ALL));
        list.add(Location.getLocation(country, province, city));
        return list;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.id);
        out.writeUTF(this.country);
        out.writeUTF(this.province);
        out.writeUTF(this.city);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.country = in.readUTF();
        this.province = in.readUTF();
        this.city = in.readUTF();
    }

    @Override
    public int compareTo(Base o) {
        Location other = (Location) o;
        int tmp = Integer.compare(this.id, other.id);
        if (tmp != 0) {
            return tmp;
        }

        tmp = this.country.compareTo(other.country);
        if (tmp != 0) {
            return tmp;
        }

        tmp = this.province.compareTo(other.province);
        if (tmp != 0) {
            return tmp;
        }

        tmp = this.city.compareTo(other.city);
        return tmp;
    }

}
