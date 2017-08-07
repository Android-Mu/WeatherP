package com.codexiaosheng.weatherp.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Description：网络请求工具类
 * <p>
 * Created by code-xiaosheng on 2017/8/2.
 */

public class HttpUtil {

    public static void sendHttpRequest(String url, Callback call) {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        httpClient.newCall(request).enqueue(call);
    }

}
