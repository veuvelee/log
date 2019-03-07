package com.analysis.location;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.analysis.output.IDConverter;
import com.analysis.output.MyOutputCollector;
import com.common.GlobalEnum;
import com.pojo.Base;
import com.pojo.BaseWritable;
import com.pojo.LocationReducerV;
import com.pojo.StatsLocation;
import org.apache.hadoop.conf.Configuration;



public class LocationCollector implements MyOutputCollector {

    @Override
    public void collect(Configuration conf, Base key, BaseWritable value, PreparedStatement pstmt, IDConverter converter) throws SQLException, IOException {
        StatsLocation locationDimension = (StatsLocation) key;
        LocationReducerV locationReducerV = (LocationReducerV) value;

        int i = 0;
        pstmt.setInt(++i, converter.getId(locationDimension.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getId(locationDimension.getStatsCommon().getDate1()));
        pstmt.setInt(++i, converter.getId(locationDimension.getLocation()));
        pstmt.setInt(++i, locationReducerV.getUvs());
        pstmt.setInt(++i, locationReducerV.getVisits());
        pstmt.setInt(++i, locationReducerV.getBounceNumber());
        pstmt.setString(++i, conf.get(GlobalEnum.RUN_DATE_NAME));
        pstmt.setInt(++i, locationReducerV.getUvs());
        pstmt.setInt(++i, locationReducerV.getVisits());
        pstmt.setInt(++i, locationReducerV.getBounceNumber());

        pstmt.addBatch();
        
        
    }

}
