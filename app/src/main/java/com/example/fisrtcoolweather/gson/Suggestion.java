package com.example.fisrtcoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/7/14.
 */
public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;  //温度的提示
    @SerializedName("cw")
    public CarWash carWash; //是否适合洗车
    @SerializedName("sport")
    public Sport sport;  //是否运动
    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
