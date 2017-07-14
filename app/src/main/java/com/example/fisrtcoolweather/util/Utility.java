package com.example.fisrtcoolweather.util;

import android.text.TextUtils;

import com.example.fisrtcoolweather.db.City;
import com.example.fisrtcoolweather.db.County;
import com.example.fisrtcoolweather.db.Province;
import com.example.fisrtcoolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/7/14.
 * 由于服务器返回的数据都是JSON格式，需要再提供一个工具类对其进行解析和处理
 */
public class Utility {
    /**
     *解析和处理服务器返回的省级数据
     * */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){  //TextUtils是对字符串处理的类
            //如果获取到的响应值不为空
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0; i< allProvinces.length(); i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);  //至此时将数据解析出来
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     *解析和处理服务器返回的市级数据
     * */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     *解析和处理服务器返回的县级数据
     * */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i=0; i< allCounties.length(); i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**将返回的json数据解析成weather实体类*/
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();  //获取json数组的第一项，并转换为string型。
//            Gson gson = new Gson();
//            return gson.fromJson(weatherContent,Weather.class); //其中参数1：json数据，参数2：需解析完成的类对象
            //以上注释的两句可合并为以下一句话
            return new Gson().fromJson(weatherContent,Weather.class);  //调用fromJson语句直接将JSON数据转换成Weather对象
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
