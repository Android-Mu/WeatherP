package com.codexiaosheng.weatherp.bean;

import java.util.List;

/**
 * Decription:
 * <p>
 * Created by code-xiaosheng on 2017/8/7.
 */

public class CountyBean {

    /**
     * status : 0
     * msg : ok
     * result : [{"id":"499","name":"东城区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"100000","depth":"2"},{"id":"500","name":"西城区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"100000","depth":"2"},{"id":"501","name":"海淀区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"100000","depth":"2"},{"id":"502","name":"朝阳区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"100000","depth":"2"},{"id":"505","name":"丰台区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"100000","depth":"2"},{"id":"506","name":"石景山区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"100000","depth":"2"},{"id":"507","name":"房山区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"102400","depth":"2"},{"id":"508","name":"门头沟区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"102300","depth":"2"},{"id":"509","name":"通州区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"101100","depth":"2"},{"id":"510","name":"顺义区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"101300","depth":"2"},{"id":"511","name":"昌平区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"102200","depth":"2"},{"id":"512","name":"怀柔区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"101400","depth":"2"},{"id":"513","name":"平谷区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"101200","depth":"2"},{"id":"514","name":"大兴区","parentid":"1","parentname":"北京","areacode":"010","zipcode":"102600","depth":"2"},{"id":"515","name":"密云县","parentid":"1","parentname":"北京","areacode":"010","zipcode":"101500","depth":"2"},{"id":"516","name":"延庆县","parentid":"1","parentname":"北京","areacode":"010","zipcode":"102100","depth":"2"}]
     */

    private String status;
    private String msg;
    private List<ResultBean> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : 499
         * name : 东城区
         * parentid : 1
         * parentname : 北京
         * areacode : 010
         * zipcode : 100000
         * depth : 2
         */

        private String id;
        private String name;
        private int parentid;
        private String parentname;
        private String areacode;
        private String zipcode;
        private String depth;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getParentid() {
            return parentid;
        }

        public void setParentid(int parentid) {
            this.parentid = parentid;
        }

        public String getParentname() {
            return parentname;
        }

        public void setParentname(String parentname) {
            this.parentname = parentname;
        }

        public String getAreacode() {
            return areacode;
        }

        public void setAreacode(String areacode) {
            this.areacode = areacode;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

        public String getDepth() {
            return depth;
        }

        public void setDepth(String depth) {
            this.depth = depth;
        }
    }
}
