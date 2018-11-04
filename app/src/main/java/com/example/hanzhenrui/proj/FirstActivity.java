package com.example.hanzhenrui.proj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.pku.hzr.bean.TodayWeather;
import cn.pku.hzr.util.NetUtil;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
                      temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);  //更新天气
                    break;
                    default:
                        break;
            }
        }
    };
    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);




    }
    void updateTodayWeather(TodayWeather todayWeather){   //更新天气
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow()+"~"+todayWeather.getHigh());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力："+todayWeather.getFengli());
        Toast.makeText(FirstActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);


        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        String cityCode = sharedPreferences.getString("main_city_code","101010100");
        queryWeatherCode(cityCode);//调用获取网络数据的方法


        mUpdateBtn.setOnClickListener(this);//增加点击事件
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
           // Log.d("myweather","网络已连接");
            Toast.makeText(FirstActivity.this,"网络已连接",Toast.LENGTH_LONG).show();
        }
        else{
           // Log.d("myweather","网络未连接");
            Toast.makeText(FirstActivity.this,"网络未连接",Toast.LENGTH_LONG).show();

        }
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        initView();
    }
@Override
    public void onClick(View view){
        if(view.getId()==R.id.title_city_manager){
            Intent i = new Intent(this,SelectCity.class);
            //startActivity(i);
            startActivityForResult(i,1);
        }
        if (view.getId() == R.id.title_update_btn){
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myweather",cityCode);//通过SharedPreferences读取城市id，缺省为北京的id
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){  //若检查到网络已经连接
                Log.d("myweather","网络已连接");
                queryWeatherCode(cityCode);//调用获取网络数据的方法
            }
            else{
                Log.d("myweather","网络未连接");
                Toast.makeText(FirstActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==1 && resultCode ==RESULT_OK){
            String newCityCode =data.getStringExtra("cityCode");
            //Log.d("myweather","选择的城市代码为"+newCityCode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
                Log.d("myweather","网络已连接");
                queryWeatherCode(newCityCode);//根据选择更新天气

            }
            else{
                Log.d("myweather","网络未连接");
                Toast.makeText(FirstActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void queryWeatherCode(String cityCode){//一种获取网络数据的方法
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myweather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con =null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in  = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                                 response.append(str);
                                // Log.d("myweather", str);

                    }
                    String responseStr = response.toString();
                    //Log.d("myweather",responseStr);
                    todayWeather = parseXML(responseStr);   //解析获取到的网络数据以得到一个天气对象
                    if (todayWeather!=null){
                        Log.d("myweather",todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (con!=null){
                        con.disconnect();
                    }
                }
            }
        }).start();

    }

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount =0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount =0;
        int lowCount =0;
        int typeCount = 0;
        try{
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myweather","parseXML");
            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;

                        case XmlPullParser.START_TAG:
                            if(xmlPullParser.getName().equals("resp")){
                                todayWeather = new TodayWeather();
                            }
                            if(todayWeather != null) {
                                if (xmlPullParser.getName().equals("city")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setCity(xmlPullParser.getText());

                                } else if (xmlPullParser.getName().equals("updatetime")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setUpdatetime(xmlPullParser.getText());

                                } else if (xmlPullParser.getName().equals("shidu")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setShidu(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("wendu")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setWendu(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("pm25")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setPm25(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("quality")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setQuality(xmlPullParser.getText());

                                } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengxiang(xmlPullParser.getText());
                                    fengxiangCount++;
                                } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengli(xmlPullParser.getText());
                                    fengliCount++;
                                } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setDate(xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setType(xmlPullParser.getText());
                                    typeCount++;
                                }
                            }
                            break;
                            case XmlPullParser.END_TAG:
                                break;

                }
                eventType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return todayWeather;
    }

}














