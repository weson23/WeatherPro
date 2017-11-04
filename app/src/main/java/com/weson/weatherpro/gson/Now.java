package com.weson.weatherpro.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weson23 on 2017/11/4 0004.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;
    }
}
