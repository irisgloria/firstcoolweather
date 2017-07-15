package com.example.fisrtcoolweather;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fisrtcoolweather.db.City;
import com.example.fisrtcoolweather.db.County;
import com.example.fisrtcoolweather.db.Province;
import com.example.fisrtcoolweather.util.HttpUtil;
import com.example.fisrtcoolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/14.
 */
public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;  ///进度条
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;   //省列表
    private List<City> cityList;   //市列表
    private List<County> countyList;   //县列表
    private Province selectedProvince;  //选中的省
    private City selectedCity;  //选中的市
    private int currentLevel;   //当前选中的级别
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        //数组中数据无法直接传递给ListView，需要借助适配器来完成。  ArrayAdapter通过泛型来制定要适配的数据类型，然后在构造函数中把要适配的数据传入。
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList); //依次传入的参数：当前的上下文，ListView的子布局的id，以及要适配的数据。
        listView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //listview的点击事件，点击任何一个子项，就会回调onItemClick()方法，通过position来判断点击的是哪个子项，获取到响应的值。
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    if(getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);   //用该值进行天气数据的查询
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }

                }
            }
        });
//        返回到上一层
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
        /**
        * 查询全国所有的省，优先查询数据库，若没有再去服务器查询
        **/
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);  //查询数据库中省的列表
        if(provinceList.size()>0){
            dataList.clear();  //清空数据列表
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());  //将省的名字依次显示出来
            }
            adapter.notifyDataSetChanged();  //数据修改后，不用重刷activity，自动加载最新的listview.
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            //数据库中没有则去服务器端获取
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    /**
     * 查询选中省内所有的市，优先查询数据库，若没有再去服务器查询
     **/
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }
    /**
     * 查询选中市内所有的县，优先查询数据库，若没有再去服务器查询
     **/
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }
        /**
         * 根据传入的地址和类型从服务器上进行查询省市县的数据
         **/
    private void queryFromServer(String address, final String type) {
        showProgressDialog();  //显示进度条
        //从服务器端获取数据
        HttpUtil.sendOkHttpRequest(address, new Callback() {  //发送请求，并回调数据
            @Override
            public void onResponse(Call call, Response response) throws IOException {//向服务器发送请求
                String responseText = response.body().string();  //只能有效调用一次，并转化为STRING类型
                boolean result = false;  //首先设置无返回结果。
                //调用Utility的方法来解析和处理服务器得到的json数据并存储到数据库中
                if("province".equals(type)){//如果获取的是province类型的数据
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());  //获取当前所选用省的id
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                //如果读取失败
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());  //构建一个progressdialog对象，getActivity()：获得Fragment依附的Activity对象
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);  //在loading时，如果触摸屏幕其他区域progressdialog不会消失，点击物理返回键则消失。
        }
        progressDialog.show();
    }
}
