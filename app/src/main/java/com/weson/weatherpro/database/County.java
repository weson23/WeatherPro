package com.weson.weatherpro.database;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/11/3 0003.
 */

public class County extends DataSupport {
    private int id;

    private int cityId;

    private String CountyName;

    private String weatherId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountyName() {
        return CountyName;
    }

    public void setCountyName(String countyName) {
        CountyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
