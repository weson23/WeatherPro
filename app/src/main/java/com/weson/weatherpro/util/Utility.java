package com.weson.weatherpro.util;

import android.text.TextUtils;

import com.weson.weatherpro.database.City;
import com.weson.weatherpro.database.County;
import com.weson.weatherpro.database.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weson23 on 2017/11/3 0003.
 * 进行从服务器返回的数据的处理
 */

public class Utility {


    /**
     * @param response 服务器返回的JSON数组数据
     * @return 返回处理的结果
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                //服务器中定义的是一个JSON数组，这里将返回的数组传到allProvinces中
                JSONArray allProvinces = new JSONArray(response);
                //进行循环遍历JSONArray，取出JSONObject对象
                for (int i = 0; i < allProvinces.length(); i++) {
                    //取出JSONObject对象赋给provinceObject
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    //实例化一个Province类
                    Province province = new Province();
                    //将provinceObject对象中的name取出来封装到province
                    province.setProvinceName(provinceObject.getString("name"));
                    //将provinceObject对象中的id取出来封装到province
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //进行数据保存
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * @param response
     * @param provinceId
     * @return
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if(!TextUtils.isEmpty(response)){
            try {
                //服务器中定义的是一个JSON数组，这里将返回的数组传到allCities中
                JSONArray allCities = new JSONArray(response);
                //进行循环遍历JSONArray，取出JSONObject对象
                for(int i = 0; i < allCities.length(); i++){
                    //取出JSONObject对象赋给provinceObject
                    JSONObject cityObject = allCities.getJSONObject(i);
                    //实例化一个City类
                    City city = new City();
                    //将cityObject对象中的name取出来封装到city
                    city.setCityName(cityObject.getString("name"));
                    //将cityObject对象中的id取出来封装到city
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    //进行数据保存
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    //取出JSONObject对象赋给provinceObject
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    //实例化一个County类
                    County county = new County();
                    //将countyObject对象中的name取出来封装到county
                    county.setCountyName(countyObject.getString("name"));
                    //将countyObject对象中的weather_id取出来封装到county
                    county.setWeatherId(countyObject.getString("weather_id"));
                    //设置cityId
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param response
     * @return
     * 将返回的JSON数据解析成Weather实体类
     */
    /*public static Weather handleWeatherResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/

}
