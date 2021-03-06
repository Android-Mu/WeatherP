package com.codexiaosheng.weatherp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codexiaosheng.weatherp.bean.BingPicBean;
import com.codexiaosheng.weatherp.bean.WeatherBean;
import com.codexiaosheng.weatherp.constant.Constant;
import com.codexiaosheng.weatherp.http.HttpModule;
import com.codexiaosheng.weatherp.service.AutoUpdateService;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Description：显示天气界面
 * <p>
 * Created by code-xiaosheng on 2017/9/4.
 */

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    private Button btnCity;
    public SwipeRefreshLayout refreshLayout;
    private ImageView ivBg;
    private ScrollView svWeatherLayout;
    private TextView tvCityName;
    private TextView tvUpdateTime;
    private TextView tvDegreeText;
    private TextView tvWeatherInfoText;
    private LinearLayout llForecastLayout;
    private TextView tvAqiText;
    private TextView tvPm05Text;
    private TextView tvComfortText;
    private TextView tvCarWashText;
    private TextView tvSportText;

    private List<WeatherBean.HeWeather5Bean> weatherList = new ArrayList<>();
    private WeatherBean.HeWeather5Bean weather5Bean;

    public String cityNames; // 刷新时记录城市的名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        // 融合状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        initView();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(ivBg);
        } else {
            loadBingImg();
        }

        String weatherStr = prefs.getString("weather", null);
        if (weatherStr != null) {
            // 有缓存时直接解析天气数据
            Gson gson = new Gson();
            WeatherBean weatherBean = gson.fromJson(weatherStr, WeatherBean.class);
            weatherList = weatherBean.getHeWeather5();
            if (weatherList.size() > 0) {
                weather5Bean = weatherList.get(0);
                cityNames = weather5Bean.getBasic().getCity();
                showWeatherInfo(weather5Bean);
            }
        } else {
            // 无缓存时去服务器请求数据
            String cityName = getIntent().getStringExtra("city_name");
            cityNames = cityName;
            cityName = cityName.replaceAll("\\s*", ""); // 去掉空格(空白)
            Log.e("WeatherActivity-城市名->", "onCreate: " + cityName);
            svWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeatherData(cityNames);
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                requestWeatherData(cityNames);
            }
        });
    }

    /**
     * 加载背景图片
     */
    private void loadBingImg() {
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
                                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                                editor.putString("bing_pic", "http://s.cn.bing.net" + url);
                                editor.apply();
                                Glide.with(WeatherActivity.this)
                                        .load("http://s.cn.bing.net" + url)
                                        .into(ivBg);
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

    private void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btnCity = (Button) findViewById(R.id.btn_nav);
        ivBg = (ImageView) findViewById(R.id.iv_bg);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        svWeatherLayout = (ScrollView) findViewById(R.id.sv_weather_layout);
        tvCityName = (TextView) findViewById(R.id.tv_city_name);
        tvUpdateTime = (TextView) findViewById(R.id.tv_update_time);
        tvDegreeText = (TextView) findViewById(R.id.tv_degree_text);
        tvWeatherInfoText = (TextView) findViewById(R.id.tv_weather_info_text);
        llForecastLayout = (LinearLayout) findViewById(R.id.ll_forecast_layout);
        tvAqiText = (TextView) findViewById(R.id.tv_aqi_text);
        tvPm05Text = (TextView) findViewById(R.id.tv_pm05_text);
        tvComfortText = (TextView) findViewById(R.id.tv_comfort_text);
        tvCarWashText = (TextView) findViewById(R.id.tv_car_wash_text);
        tvSportText = (TextView) findViewById(R.id.tv_sport_text);
        btnCity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 网络获取天气数据
     *
     * @param cityName
     */
    public void requestWeatherData(String cityName) {
        OkGo.<String>post(HttpModule.BASE_WEATHER_URL + HttpModule.BASIC_WEATHER + cityName +
                Constant.HEWEATHER_KEY)
                .tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Log.e("requestWeatherData", "onSuccess: " + response.body());
                String json = response.body().toString();
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = new Gson();
                    WeatherBean weatherBean = gson.fromJson(json, WeatherBean.class);
                    weatherList = weatherBean.getHeWeather5();
                    if (weatherList.size() > 0) {
                        weather5Bean = weatherList.get(0);
                        if (weather5Bean != null && "ok".equals(weather5Bean.getStatus())) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", json);
                            editor.apply();
                            showWeatherInfo(weather5Bean);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败",
                                    Toast.LENGTH_LONG).show();
                        }
                        refreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                Log.e("requestWeatherData", "onError: " + response.body());
                Toast.makeText(WeatherActivity.this, "网络请求失败",
                        Toast.LENGTH_LONG).show();
                refreshLayout.setRefreshing(false);
            }
        });

        loadBingImg();
    }

    /**
     * 显示天气信息
     *
     * @param bean
     */
    private void showWeatherInfo(WeatherBean.HeWeather5Bean bean) {
        String cityName = bean.getBasic().getCity();
        String updateTime = bean.getBasic().getUpdate().getLoc().split(" ")[1];
        String degree = bean.getNow().getTmp() + "°C";
//        String weatherInfo = bean.getNow().get
        tvCityName.setText(cityName);
        tvUpdateTime.setText(updateTime);
        tvDegreeText.setText(degree);
//        tvWeatherInfoText.setText(weatherInfo);
        llForecastLayout.removeAllViews();
        for (WeatherBean.HeWeather5Bean.DailyForecastBean forecase :
                bean.getDaily_forecast()) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item_weather_layout,
                    llForecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.tv_date_text);
            TextView infoText = (TextView) view.findViewById(R.id.tv_info_text);
            TextView maxText = (TextView) view.findViewById(R.id.tv_max_text);
            TextView minText = (TextView) view.findViewById(R.id.tv_min_text);
            dateText.setText(forecase.getDate());
//            infoText.setText(forecase.);
            maxText.setText(forecase.getTmp().getMax());
            minText.setText(forecase.getTmp().getMin());
            llForecastLayout.addView(view);
        }

        String comfort = "舒适度：" + bean.getSuggestion().getComf().getBrf();
        String carWash = "洗车指数：" + bean.getSuggestion().getCw().getBrf();
        String sport = "运动建议：" + bean.getSuggestion().getSport().getBrf();
        tvComfortText.setText(comfort);
        tvCarWashText.setText(carWash);
        tvSportText.setText(sport);
        svWeatherLayout.setVisibility(View.VISIBLE);

        // 启动后台更新天气服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

}
