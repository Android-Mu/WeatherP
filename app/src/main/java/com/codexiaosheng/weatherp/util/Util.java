package com.codexiaosheng.weatherp.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Description：工具类
 * <p>
 * Created by code-xiaosheng on 2017/8/2.
 */

public class Util {

    /**
     * 读取assets文件夹下的json文件
     *
     * @param context
     * @return
     */
    public static String getDataFromAssets(Context context) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open("city_20170724.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String responses = "";
            while (null != (responses = bufferedReader.readLine())) {
                sb.append(responses);
            }
        } catch (IOException e) {
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        return sb.toString().trim();
    }

}
