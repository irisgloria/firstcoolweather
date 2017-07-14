package com.example.fisrtcoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/7/14.
 * Serializable是intent的传值方式之一，将对象转换成可存储或可传输的状态；
 * 序列化后的对象可以在网络上传输，也可以存储到本地
 */
public class Basic {
    @SerializedName("city")
    public String cityName;  //城市名
    @SerializedName("id")
    public String weatherId;  //天气对应的ID
    public Update update;
    public class Update{
        @SerializedName("loc")  //表示天气的更新时间，使用该注解方式让json字段和java字段间建立映射关系
        public String updateTime;
    }
}
