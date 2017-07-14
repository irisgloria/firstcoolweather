package com.example.fisrtcoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/7/14.
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;  //温度
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
