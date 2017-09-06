package com.codexiaosheng.weatherp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.codexiaosheng.weatherp.bean.WeatherBean;
import com.codexiaosheng.weatherp.constant.Constant;
import com.codexiaosheng.weatherp.http.HttpModule;
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
    private List<WeatherBean.HeWeather5Bean.NowBean> nowList = new ArrayList<>();
    private List<WeatherBean.HeWeather5Bean.SuggestionBean> suggestionList = new ArrayList<>();
    private List<WeatherBean.HeWeather5Bean.DailyForecastBean> dailyForecastList = new ArrayList<>();
//    private List<WeatherBean.HeWeather5Bean> dailyForecastList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initView();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = prefs.getString("weather", null);
        if (weatherStr != null) {
            // 有缓存时直接解析天气数据

        } else {
            // 无缓存时去服务器请求数据
            String cityName = getIntent().getStringExtra("city_name");
            cityName = cityName.replaceAll("\\s*", ""); // 去掉空格(空白)
            Log.e("WeatherActivity-城市名->", "onCreate: " + cityName);
            svWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeatherData(cityName);
        }
    }

    private void initView() {
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
    }

    /**
     * 网络获取天气数据
     *
     * @param cityName
     */
    private void requestWeatherData(String cityName) {
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
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                Log.e("requestWeatherData", "onError: " + response.body());
                Toast.makeText(WeatherActivity.this, "网络请求失败",
                        Toast.LENGTH_LONG).show();
            }
        });
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
    }

}
