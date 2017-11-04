package com.weson.weatherpro.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weson23 on 2017/11/4 0004.
 */

public class Suggestion {

    @SerializedName("comf")
    public  Comfort comfort;

    @SerializedName("cw")
    public  CarWash carWash;

    public Sport sport;

    public class Comfort {

        @SerializedName("txt")
        public String info;

    }

    public class CarWash {

        @SerializedName("txt")
        public String info;

    }

    public class Sport {

        @SerializedName("txt")
        public String info;

    }
}
