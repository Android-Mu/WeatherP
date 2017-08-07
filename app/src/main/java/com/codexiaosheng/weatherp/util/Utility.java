package com.codexiaosheng.weatherp.util;

import android.text.TextUtils;

import com.codexiaosheng.weatherp.bean.CountyBean;
import com.codexiaosheng.weatherp.bean.ProvinceBean;
import com.codexiaosheng.weatherp.db.City;
import com.codexiaosheng.weatherp.db.County;
import com.codexiaosheng.weatherp.db.Province;
import com.google.gson.Gson;

import java.util.List;

/**
 * Description：json解析工具类
 * <p>
 * Created by code-xiaosheng on 2017/8/2.
 */

public class Utility {

    /**
     * 省级数据
     *
     * @param response
     * @return
     */
    public static boolean handleProvinceJson(String response) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new Gson();
            ProvinceBean provinceBean = gson.fromJson(response, ProvinceBean.class);
            List<ProvinceBean.ResultBean> pList = provinceBean.getResult();
            if (null != pList && pList.size() > 0) {
                for (int i = 0; i < pList.size(); i++) {
                    Province province = new Province();
                    province.setProvinceCode(pList.get(i).getParentid());
                    province.setProvinceName(pList.get(i).getName());
                    province.save();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 市级数据
     *
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityJson(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new Gson();
            ProvinceBean provinceBean = gson.fromJson(response, ProvinceBean.class);
            List<ProvinceBean.ResultBean> pList = provinceBean.getResult();
            if (null != pList && pList.size() > 0) {
                for (int i = 0; i < pList.size(); i++) {
                    City city = new City();
                    city.setProvinceId(pList.get(i).getParentid());
                    city.setCityName(pList.get(i).getName());
                    city.save();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 区（县）级数据
     *
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountyJson(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new Gson();
            CountyBean provinceBean = gson.fromJson(response, CountyBean.class);
            List<CountyBean.ResultBean> pList = provinceBean.getResult();
            if (null != pList && pList.size() > 0) {
                for (int i = 0; i < pList.size(); i++) {
                    County county = new County();
//                    county.setId(pList.get(i).getParentid());
                    county.setCityId(pList.get(i).getParentid());
                    county.setCountyName(pList.get(i).getName());
                    county.save();
                }
                return true;
            }
        }
        return false;
    }

}
