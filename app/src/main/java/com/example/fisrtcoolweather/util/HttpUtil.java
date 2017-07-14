package com.example.fisrtcoolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/7/14.
 */
public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();   //创建一个实例
        Request request = new Request.Builder().url(address).build();  //创建一个Request对象
        client.newCall(request).enqueue(callback);  //创建一个Call对象，并调用它的execute()方法来发送请求并获取服务器返回的数据
    }
}
