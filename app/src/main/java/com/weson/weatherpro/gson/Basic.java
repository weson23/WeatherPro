package com.weson.weatherpro.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weson23 on 2017/11/4 0004.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{

        @SerializedName("loc")
        public String updateTime;
    }

}
