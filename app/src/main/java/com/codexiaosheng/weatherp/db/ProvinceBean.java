package com.codexiaosheng.weatherp.db;

import org.litepal.crud.DataSupport;

/**
 * Decription:省份
 * <p>
 * Created by code-xiaosheng on 2017/8/7.
 */

public class ProvinceBean extends DataSupport {

    private String pinYin;
    private Integer id;
    private Integer pid;
    private double gisGcj02Lng;
    private double gisBd09Lng;
    private String name;
    private double gisBd09Lat;
    private double gisGcj02Lat;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getGisGcj02Lng() {
        return gisGcj02Lng;
    }

    public void setGisGcj02Lng(double gisGcj02Lng) {
        this.gisGcj02Lng = gisGcj02Lng;
    }

    public double getGisBd09Lng() {
        return gisBd09Lng;
    }

    public void setGisBd09Lng(double gisBd09Lng) {
        this.gisBd09Lng = gisBd09Lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGisBd09Lat() {
        return gisBd09Lat;
    }

    public void setGisBd09Lat(double gisBd09Lat) {
        this.gisBd09Lat = gisBd09Lat;
    }

    public double getGisGcj02Lat() {
        return gisGcj02Lat;
    }

    public void setGisGcj02Lat(double gisGcj02Lat) {
        this.gisGcj02Lat = gisGcj02Lat;
    }
}
