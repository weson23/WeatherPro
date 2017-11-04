package com.weson.weatherpro.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by weson23 on 2017/11/4 0004.
 */

public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    //使用集合类型进行声明Forecast类
    //daily_forecast中包含的是一个数组，多以使用List集合引用Forecast类
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
