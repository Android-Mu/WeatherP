package com.codexiaosheng.weatherp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codexiaosheng.weatherp.constant.Constant;
import com.codexiaosheng.weatherp.db.City;
import com.codexiaosheng.weatherp.db.County;
import com.codexiaosheng.weatherp.db.Province;
import com.codexiaosheng.weatherp.http.HttpModule;
import com.codexiaosheng.weatherp.util.HttpUtil;
import com.codexiaosheng.weatherp.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Description：选择省市区
 * <p>
 * Created by code-xiaosheng on 2017/8/2.
 */

public class ChooseAreaFragment extends Fragment {

    private Button btnBack;
    private TextView tvTitle;

    private ListView lvView;
    private ArrayAdapter<String> adapter;
    private List<String> datas = new ArrayList<>();

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    // 当前选中的级别
    private int currentLevel;

    private ProgressDialog progressDialog;
    // 省市县集合
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    // 选中的省、市
    private Province selectProvince;
    private City selectCity;

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
                    queryProvinces();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                }
            }
        });

        queryProvinces();
    }

    /**
     * 查询所有省级数据，顺序，先查数据库，再从服务器获取
     */
    private void queryProvinces() {
        tvTitle.setText("中国");
        btnBack.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        Log.e("queryProvinces-->>>", "queryProvinces: " + provinceList.size());
        if (provinceList.size() > 0) {
            datas.clear();
            for (Province province : provinceList) {
                datas.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lvView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String url = HttpModule.BASE_URL + "province?appkey=" + Constant.APP_KEY;
            queryFromServer(url, "province");
        }
    }

    /**
     * 查询城市数据
     */
    private void queryCities() {
        tvTitle.setText(selectProvince.getProvinceName());
        btnBack.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?",
                String.valueOf(selectProvince.getId())).find(City.class);
        Log.e("queryCities--->>", "queryCities: " + cityList.size());
        if (cityList.size() > 0) {
            datas.clear();
            for (City city : cityList) {
                datas.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lvView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectProvince.getId();
            String url = HttpModule.BASE_URL + "city?parentid=" + provinceCode + "&appkey=" + Constant.APP_KEY;
            queryFromServer(url, "city");
        }
    }

    /**
     * 查询县/区数据
     */
    private void queryCounties() {
        tvTitle.setText(selectCity.getCityName());
        btnBack.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?",
                String.valueOf(selectCity.getId())).find(County.class);
        Log.e("queryCounties--->>", "queryCounties: " + countyList.size());
        if (countyList.size() > 0) {
            datas.clear();
            for (County county : countyList) {
                datas.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lvView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
//            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getId();
            String url = HttpModule.BASE_URL + "town?parentid=" + cityCode + "&appkey=" + Constant.APP_KEY;
            queryFromServer(url, "county");
        }
    }

    /**
     * 从服务器获取对应数据
     *
     * @param url
     * @param type
     */
    private void queryFromServer(String url, final String type) {
        showProgressDialog();
        HttpUtil.sendHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ChooseAreaFragment", "onFailure: " + e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.e("ChooseAreaFragment", "onResponse: " + responseText);
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceJson(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityJson(responseText, selectProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyJson(responseText, selectCity.getId());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 加载框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
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
