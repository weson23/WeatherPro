package com.weson.weatherpro.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.weson.weatherpro.gson.Weather;
import com.weson.weatherpro.util.HttpUtil;
import com.weson.weatherpro.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 每次服务启动时候调用的方法，进行天气更新，图片更新
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        //获得定时器实例
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //这是8小时的毫秒数
        int anhour = 8*60*60*1000;
        //设置定时时间为8小时
        long triggerAtTime = SystemClock.elapsedRealtime() + anhour;
        //设置一个意图，跳转到服务
        Intent i = new Intent(this, AutoUpdateService.class);
        //设置一个pendingIntent等到某时刻跳转
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        //设置定时跳转
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息方法
     */
    public void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //获得天气数据内容
        String weatherString = prefs.getString("weather", null);
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            //获得具体的weatherId
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+
                    "&key=25b0b440d3bb454ca70e18e83ad39351";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().toString();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if(weather!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新每日的每日背景的图片
     */
    public void updateBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().toString();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }
        });
    }

}
