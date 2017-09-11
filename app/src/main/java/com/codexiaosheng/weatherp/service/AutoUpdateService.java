package com.codexiaosheng.weatherp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.codexiaosheng.weatherp.bean.BingPicBean;
import com.codexiaosheng.weatherp.bean.WeatherBean;
import com.codexiaosheng.weatherp.constant.Constant;
import com.codexiaosheng.weatherp.http.HttpModule;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.List;

/**
 * 后台自动更新服务
 */
public class AutoUpdateService extends Service {

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingImg();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000; // 8 小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent intent1 = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, intent1, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新图片
     */
    private void updateBingImg() {
        final String bingPic = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        OkGo.<String>get(bingPic).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body().toString();
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = new Gson();
                    BingPicBean picBean = gson.fromJson(json, BingPicBean.class);
                    if (null != picBean) {
                        List<BingPicBean.ImagesBean> list = picBean.getImages();
                        if (list != null && list.size() > 0) {
                            String url = list.get(0).getUrl();
                            if (!TextUtils.isEmpty(url)) {
                                SharedPreferences.Editor editor = PreferenceManager
                                        .getDefaultSharedPreferences(AutoUpdateService.this).edit();
                                editor.putString("bing_pic", "http://s.cn.bing.net" + url);
                                editor.apply();
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
            }
        });
    }

    /**
     * 更新天气
     */
    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String json = preferences.getString("weather", null);
        if (json != null) {
            // 有缓存时直接解析天气数据
            Gson gson = new Gson();
            WeatherBean weatherBean = gson.fromJson(json, WeatherBean.class);
            List<WeatherBean.HeWeather5Bean> weatherList = weatherBean.getHeWeather5();
            if (weatherList.size() > 0) {
                WeatherBean.HeWeather5Bean weather5Bean = weatherList.get(0);
                String cName = weather5Bean.getBasic().getCity();
                // 更新天气信息
                OkGo.<String>post(HttpModule.BASE_WEATHER_URL + HttpModule.BASIC_WEATHER + cName +
                        Constant.HEWEATHER_KEY)
                        .tag(this).execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("AutoUpdateService", "onSuccess: " + response.body());
                        String json = response.body().toString();
                        if (!TextUtils.isEmpty(json)) {
                            Gson gson = new Gson();
                            WeatherBean weatherBean = gson.fromJson(json, WeatherBean.class);
                            List<WeatherBean.HeWeather5Bean> weatherList = weatherBean.getHeWeather5();
                            if (weatherList.size() > 0) {
                                WeatherBean.HeWeather5Bean weather5Bean = weatherList.get(0);
                                if (weather5Bean != null && "ok".equals(weather5Bean.getStatus())) {
                                    SharedPreferences.Editor editor = PreferenceManager
                                            .getDefaultSharedPreferences(AutoUpdateService.this).edit();
                                    editor.putString("weather", json);
                                    editor.apply();
                                } else {
                                    Toast.makeText(AutoUpdateService.this, "获取天气信息失败",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Log.e("requestWeatherData", "onError: " + response.body());
                        Toast.makeText(AutoUpdateService.this, "网络请求失败",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
