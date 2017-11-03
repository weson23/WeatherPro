package com.weson.weatherpro.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by weson23 on 2017/11/3 0003.
 */

public class HttpUtil {
    //定义sendOkHttpRequest()方法，并且使用自带的回调接口okhttp3.Callback，进行请求数据
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();//创建一个OkHttpClient实例
        //创建一个传入了URL的request对象
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);//使用enqueue()来传入回调接口参数
    }

}
