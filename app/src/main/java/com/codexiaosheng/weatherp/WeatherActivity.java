package com.codexiaosheng.weatherp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codexiaosheng.weatherp.constant.Constant;
import com.codexiaosheng.weatherp.http.HttpModule;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

/**
 * 天气界面
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
            cityName = cityName.replaceAll(" ",""); // 去掉空格问题
            Log.e("WeatherActivity-城市名->", "onCreate: " + cityName);
            svWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeatherData(cityName);
        }
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
            }

            @Override
            public void onError(Response<String> response) {
                Log.e("requestWeatherData", "onError: " + response.body());

            }
        });
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

}
