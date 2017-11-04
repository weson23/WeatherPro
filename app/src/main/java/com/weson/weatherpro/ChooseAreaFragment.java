package com.weson.weatherpro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weson.weatherpro.database.City;
import com.weson.weatherpro.database.County;
import com.weson.weatherpro.database.Province;
import com.weson.weatherpro.util.HttpUtil;
import com.weson.weatherpro.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter adapter;

    private List<String> dataList = new ArrayList<>();
    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;


    /**
     * onCreateView方法进行控件的实例化，初始化ArrayAdapter，设置适配器
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //设置listView的Item点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    //获取点击省份的名称
                    selectedProvince = provinceList.get(position);
                    //调用查询市的方法
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    //获取点击的城市的名称
                    selectedCity = cityList.get(position);
                    //调用查询县的方法
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        //设置返回按键的点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果选中的级别是县级，则返回去查询城市列表
                if (currentLevel == LEVEL_COUNTY) {
                    //调用查询城市的方法
                    queryCities();
                    //如果选中的级别是县级，则返回去查询城市列表
                } else if (currentLevel == LEVEL_CITY) {
                    //调用查询省份的方法
                    queryProvinces();
                }
            }
        });
        //如果是省份级别，调用查询省份的方法来加载页面
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询
     * 如果没有查询到再调用queryFromServer方法去服务器上查询。
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        //从数据库找到Province类省级的数据，
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            //遍历循环provinceList集合，取出集合中的所有数据，赋给province类
            for (Province province : provinceList) {
                //将province中的省份名称封装到dataList
                dataList.add(province.getProvinceName());
            }
            //通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
            adapter.notifyDataSetChanged();
            //表示将列表移动到指定的0位置处
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;

        } else {
            //通过网址去服务器寻找数据
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询
     * 如果没有查询到再调用queryFromServer方法去服务器上查询。
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        //设置backButton为可见
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid=？", String.valueOf(
                selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            //通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
            adapter.notifyDataSetChanged();
            //表示将列表移动到指定的0位置处
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            //去服务器寻找数据
            //获得省份的ID
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询
     * 如果没有查询到再调用queryFromServer方法去服务器上查询。
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=？", String.valueOf(
                selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            //通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
            adapter.notifyDataSetChanged();
            //表示将列表移动到指定的0位置处
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            //获得省份的ID
            int provinceCode = selectedProvince.getProvinceCode();
            //获得城市的ID
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(String address, final String type) {
        //显示获取数据的进度条的方法
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //失败的回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),
                                "加载数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // 成功的回调方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到服务器返回的数据，进行数据装换成string类型
                String responseText = response.body().toString();
                boolean result = false;
                if ("province".equals(type)) {
                    //将从服务器返回的省份的数据进行解析，将数据保存到数据库中，返回Boolean类型值
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    //将从服务器返回的城市的数据进行解析，将数据保存到数据库中，返回Boolean类型值
                    result = Utility.handleCityResponse(responseText,
                            selectedProvince.getId());
                } else if ("county".equals(type)) {
                    //将从服务器返回的县的数据进行解析，将数据保存到数据库中，返回Boolean类型值
                    result = Utility.handleCountyResponse(responseText,
                            selectedCity.getId());
                }
                //如果解析返回的值为true,则将数据保存成功，则利用方法进行数据更新
                if (result) {
                    //切换到UI线程进行数据更新
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭进度条显示
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
     * 显示进度对话框
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
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}

