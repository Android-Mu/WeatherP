package com.codexiaosheng.weatherp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.codexiaosheng.weatherp.bean.ProvinceCityCountyBean;
import com.codexiaosheng.weatherp.constant.Constant;
import com.codexiaosheng.weatherp.db.CityBean;
import com.codexiaosheng.weatherp.db.CountyBean;
import com.codexiaosheng.weatherp.db.ProvinceBean;
import com.codexiaosheng.weatherp.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static com.codexiaosheng.weatherp.constant.Constant.LEVEL_CITY;
import static com.codexiaosheng.weatherp.constant.Constant.LEVEL_COUNTY;
import static com.codexiaosheng.weatherp.constant.Constant.LEVEL_PROVINCE;

/**
 * Description：展示省市区
 * <p>
 * Created by code-xiaosheng on 2017/8/2.
 */

public class ChooseAreaFragment extends Fragment {

    private Button btnBack;
    private TextView tvTitle;

    private ListView lvView;
    private ArrayAdapter<String> adapter;
    private List<String> datas = new ArrayList<>();

    // 当前选中的级别
    private int currentLevel;
    private ProvinceBean selectProvince; // 选中的省份
    private CityBean selectCity; // 选中的市

    private ProgressDialog progressDialog;

    // 省市县集合
    private List<ProvinceCityCountyBean> pccList = new ArrayList<>();
    private List<ProvinceBean> provinceList = new ArrayList<>();
    private List<CityBean> cityList = new ArrayList<>();
    private List<CountyBean> countyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        btnBack = (Button) view.findViewById(R.id.btn_back);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        lvView = (ListView) view.findViewById(R.id.lv_view);
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_expandable_list_item_1, datas);
        lvView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lvView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    queryCounties();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_CITY) {
                    queryDatas();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                }
            }
        });

        queryDatas();
    }

    /**
     * 查询区/县级别数据
     */
    private void queryCounties() {
        tvTitle.setText(selectCity.getName());
        btnBack.setVisibility(View.VISIBLE);
        showProgressDialog();
        countyList = DataSupport.where("cid = ?",
                String.valueOf(selectCity.getCid())).find(CountyBean.class);
        if (countyList.size() > 0) {
            datas.clear();
            for (CountyBean county :
                    countyList) {
                datas.add(county.getName());
            }
            closeProgressDialog();
            adapter.notifyDataSetChanged();
//            lvView.setSelection(0);
            currentLevel = Constant.LEVEL_COUNTY;
        }
    }

    /**
     * 查询市级别数据
     */
    private void queryCities() {
        tvTitle.setText(selectProvince.getName());
        btnBack.setVisibility(View.VISIBLE);
        showProgressDialog();
        cityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectProvince.getPid())).find(CityBean.class);
        if (cityList.size() > 0) {
            datas.clear();
            for (CityBean city : cityList) {
                datas.add(city.getName());
            }
            closeProgressDialog();
            adapter.notifyDataSetChanged();
            lvView.setSelection(0);
            currentLevel = Constant.LEVEL_CITY;
        }
    }

    /**
     * 查询所有省级数据，顺序，先查数据库，再从服务器获取
     */
    private void queryDatas() {
        tvTitle.setText("中国");
        btnBack.setVisibility(View.GONE);
        showProgressDialog();
        provinceList = DataSupport.findAll(ProvinceBean.class);
        if (provinceList.size() > 0) {
            datas.clear();
            for (ProvinceBean province : provinceList) {
                datas.add(province.getName());
            }
            closeProgressDialog();
            adapter.notifyDataSetChanged();
            lvView.setSelection(0);
            currentLevel = Constant.LEVEL_PROVINCE;
        } else {
            queryFromServer();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            int type = msg.what;
            switch (type) {
                case 10:
                    Log.e("ChooseAreaFragment", "handleMessage: Province->" + provinceList.size());
                    queryDatas();
                    break;
                case 20:
                    Log.e("ChooseAreaFragment", "handleMessage: City->" + cityList.size());
                    break;
                case 30:
                    Log.e("ChooseAreaFragment", "handleMessage: County->" + countyList.size());
                    break;
            }
        }
    };

    /**
     * 从本地解析json保存数据
     */
    private void queryFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = Util.getDataFromAssets(getActivity());
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = new Gson();
                    pccList = gson.fromJson(json, new TypeToken<List<ProvinceCityCountyBean>>() {
                    }.getType());

                    if (pccList.size() > 0) {
                        for (int i = 0; i < pccList.size(); i++) {
                            ProvinceCityCountyBean bean = pccList.get(i);
                            ProvinceBean province = new ProvinceBean();
                            province.setName(bean.getName());
                            province.setId(Integer.parseInt(bean.getId()));
                            province.setPid(Integer.parseInt(bean.getId()));
                            provinceList.add(province);
                            province.save();

                            if (i == pccList.size() - 1) {
                                handler.sendEmptyMessage(10);
                            }

                            List<ProvinceCityCountyBean.CityListBeanX> cList = bean.getCityList();
                            for (int j = 0; j < cList.size(); j++) {
                                ProvinceCityCountyBean.CityListBeanX cBean = cList.get(j);
                                CityBean city = new CityBean();
                                city.setName(cBean.getName());
                                city.setId(Integer.parseInt(cBean.getId()));
                                city.setProvinceid(Integer.parseInt(bean.getId()));
                                city.setCid(Integer.parseInt(cBean.getId()));
                                city.save();
                                cityList.add(city);
                                if (i == pccList.size() - 1 && j == cList.size() - 1) {
                                    handler.sendEmptyMessage(20);
                                    Log.e("city id", "run: " + cBean.getId() + "--" + city.getId());
                                }

                                List<ProvinceCityCountyBean.CityListBeanX.CityListBean> countyL =
                                        cBean.getCityList();
                                for (int k = 0; k < countyL.size(); k++) {
                                    ProvinceCityCountyBean.CityListBeanX.CityListBean countB =
                                            countyL.get(k);
                                    CountyBean county = new CountyBean();
                                    county.setId(Integer.parseInt(countB.getId()));
                                    county.setCountyid(Integer.parseInt(countB.getId()));
                                    county.setName(countB.getName());
                                    county.setCid(Integer.parseInt(cBean.getId()));
                                    county.save();
                                    countyList.add(county);
                                    if (i == pccList.size() - 1 && j == cList.size() - 1 && k == countyL.size() - 1) {
                                        handler.sendEmptyMessage(30);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 加载框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭加载框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
