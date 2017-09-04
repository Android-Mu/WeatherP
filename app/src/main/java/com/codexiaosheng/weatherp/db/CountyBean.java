package com.codexiaosheng.weatherp.db;

import org.litepal.crud.DataSupport;

/**
 * Decription: 区/县
 * <p>
 * Created by code-xiaosheng on 2017/8/7.
 */

public class CountyBean extends DataSupport {

    private Integer id;
    private Integer countyid;
    private Integer cid;
    private String name;
    private int status;
    private Object orderId;
    private Object createAccount;
    private Object createTime;
    private Object modifyAccount;
    private Object modifyTime;
    private double gisBd09Lat;
    private double gisBd09Lng;
    private double gisGcj02Lat;
    private double gisGcj02Lng;
    private int stubGroupCnt;
    private String pinYin;

    public Integer getCountyid() {
        return countyid;
    }

    public void setCountyid(Integer countyid) {
        this.countyid = countyid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getOrderId() {
        return orderId;
    }

    public void setOrderId(Object orderId) {
        this.orderId = orderId;
    }

    public Object getCreateAccount() {
        return createAccount;
    }

    public void setCreateAccount(Object createAccount) {
        this.createAccount = createAccount;
    }

    public Object getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Object createTime) {
        this.createTime = createTime;
    }

    public Object getModifyAccount() {
        return modifyAccount;
    }

    public void setModifyAccount(Object modifyAccount) {
        this.modifyAccount = modifyAccount;
    }

    public Object getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Object modifyTime) {
        this.modifyTime = modifyTime;
    }

    public double getGisBd09Lat() {
        return gisBd09Lat;
    }

    public void setGisBd09Lat(double gisBd09Lat) {
        this.gisBd09Lat = gisBd09Lat;
    }

    public double getGisBd09Lng() {
        return gisBd09Lng;
    }

    public void setGisBd09Lng(double gisBd09Lng) {
        this.gisBd09Lng = gisBd09Lng;
    }

    public double getGisGcj02Lat() {
        return gisGcj02Lat;
    }

    public void setGisGcj02Lat(double gisGcj02Lat) {
        this.gisGcj02Lat = gisGcj02Lat;
    }

    public double getGisGcj02Lng() {
        return gisGcj02Lng;
    }

    public void setGisGcj02Lng(double gisGcj02Lng) {
        this.gisGcj02Lng = gisGcj02Lng;
    }

    public int getStubGroupCnt() {
        return stubGroupCnt;
    }

    public void setStubGroupCnt(int stubGroupCnt) {
        this.stubGroupCnt = stubGroupCnt;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }
}
