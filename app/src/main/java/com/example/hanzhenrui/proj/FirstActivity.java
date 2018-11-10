package com.example.hanzhenrui.proj;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
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
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.pku.hzr.bean.Cn2Spell;
import cn.pku.hzr.bean.TodayWeather;
import cn.pku.hzr.util.NetUtil;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
                      temperatureTv, climateTv, windTv, city_name_Tv;

    private TextView next1,next1text1,next1text2,next1text3,next2,next2text1,next2text2,next2text3
            ,next3,next3text1,next3text2,next3text3;
    private ImageView next1weather,next2weather,next3weather;//声明TextView ImageView对象




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


        next1 = (TextView) findViewById(R.id.next1);
        next2 = (TextView) findViewById(R.id.next2);
        next3 = (TextView) findViewById(R.id.next3);

        next1text1 = (TextView) findViewById(R.id.next1text1);
        next1text2 = (TextView) findViewById(R.id.next1text2);
        next1text3 = (TextView) findViewById(R.id.next1text3);

        next2text1 = (TextView) findViewById(R.id.next2text1);
        next2text2 = (TextView) findViewById(R.id.next2text2);
        next2text3 = (TextView) findViewById(R.id.next2text3);

        next3text1 = (TextView) findViewById(R.id.next3text1);
        next3text2 = (TextView) findViewById(R.id.next3text2);
        next3text3 = (TextView) findViewById(R.id.next3text3);

        next1weather = (ImageView) findViewById(R.id.next1weather);
        next2weather = (ImageView) findViewById(R.id.next2weather);
        next3weather = (ImageView) findViewById(R.id.next3weather);




    }
    void updateTodayWeather(TodayWeather todayWeather){   //更新天气

        int pmValue =Integer.parseInt(todayWeather.getPm25().trim());
        String pmImgStr ="0_50";
        if (pmValue>=0&&pmValue<=50){
          pmImgStr ="0_50";
        }
        else if (pmValue>=51&&pmValue<=100){
            pmImgStr="51_100";
        }
        else if (pmValue>=101&&pmValue<=150){
            pmImgStr="101_150";
        }
        else if (pmValue>=151&&pmValue<=200){
            pmImgStr="151_200";
        }
        else if(pmValue>=201&&pmValue<301){
            pmImgStr="201_300";
        }
        else if(pmValue>=301){
            pmImgStr = "greater_300";
        }
        String typeImg = "biz_plugin_weather_"+Cn2Spell.converterToSpell(todayWeather.getType());
        String typeImg1 = "biz_plugin_weather_"+Cn2Spell.converterToSpell(todayWeather.getType1());
        String typeImg2 = "biz_plugin_weather_"+Cn2Spell.converterToSpell(todayWeather.getType2());
        String typeImg3 = "biz_plugin_weather_"+Cn2Spell.converterToSpell(todayWeather.getType3());
        R.mipmap mipmap = new R.mipmap();
        Class aClass = mipmap.getClass();
        int typeId =  -1;
        int pmImgId = -1;
        int typeId1 = -1;
        int typeId2 = -1;
        int typeId3 = -1;
        try{
            Field field = aClass.getField(typeImg);//反射，通过变量名称生成field对象
            Field field1 = aClass.getField(typeImg1);
            Field field2 = aClass.getField(typeImg2);
            Field field3 = aClass.getField(typeImg3);
            Object value = field.get(aClass); //获得变量的值
            Object value1 = field1.get(aClass);
            Object value2 = field2.get(aClass);
            Object value3 = field3.get(aClass);
            typeId =  (int)value;
            typeId1 = (int)value1;
            typeId2 = (int)value2;
            typeId3 = (int)value3;
            Field pmField =aClass.getField("biz_plugin_weather_" + pmImgStr);//反射，通过变量名称生成field对象
            Object pmImg0 = pmField.get(aClass);//获得变量的值
            pmImgId = (int)pmImg0;
        }catch (Exception e){
            if (-1==typeId)
                typeId =R.mipmap.biz_plugin_weather_qing;
            if (-1==pmImgId)
                pmImgId = R.mipmap.biz_plugin_weather_0_50;
        }finally {
            Drawable drawable = ContextCompat.getDrawable(this,typeId);
            weatherImg.setImageDrawable(drawable);
            drawable =ContextCompat.getDrawable(this,pmImgId);
            pmImg.setImageDrawable(drawable);

            Drawable drawable1 = ContextCompat.getDrawable(this,typeId1);
            next1weather.setImageDrawable(drawable1);

            Drawable drawable2 = ContextCompat.getDrawable(this,typeId2);
            next2weather.setImageDrawable(drawable2);

            Drawable drawable3 = ContextCompat.getDrawable(this,typeId3);
            next3weather.setImageDrawable(drawable3);

            Toast.makeText(FirstActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
        }





        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        if (todayWeather.getPm25()=="0")
            pmDataTv.setText(" ");
        else {
            pmDataTv.setText(todayWeather.getPm25());
        }
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow()+"~"+todayWeather.getHigh());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力："+todayWeather.getFengli());

        next1.setText(todayWeather.getDate1());
        next2.setText(todayWeather.getDate2());
        next3.setText(todayWeather.getDate3());
        next1text1.setText(todayWeather.getLow1()+"~"+todayWeather.getHigh1());
        next2text1.setText(todayWeather.getLow2()+"~"+todayWeather.getHigh2());
        next3text1.setText(todayWeather.getLow3()+"~"+todayWeather.getHigh3());

        next1text2.setText(todayWeather.getType1());
        next2text2.setText(todayWeather.getType2());
        next3text2.setText(todayWeather.getType3());

        next1text3.setText(todayWeather.getFengli1());
        next2text3.setText(todayWeather.getFengli2());
        next3text3.setText(todayWeather.getFengli3());
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
            //zstartActivity(i);
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
                    Log.d("myweather",responseStr);
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


                                  else if (xmlPullParser.getName().equals("fengli") && fengliCount == 1) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengli1(xmlPullParser.getText());
                                    fengliCount++;
                                } else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setDate1(xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setHigh1(xmlPullParser.getText().substring(2).trim());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setLow1(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 1) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setType1(xmlPullParser.getText());
                                    typeCount++;
                                }


                                else if (xmlPullParser.getName().equals("fengli") && fengliCount == 2) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengli2(xmlPullParser.getText());
                                    fengliCount++;
                                } else if (xmlPullParser.getName().equals("date") && dateCount == 2) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setDate2(xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setHigh2(xmlPullParser.getText().substring(2).trim());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setLow2(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 2) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setType2(xmlPullParser.getText());
                                    typeCount++;
                                }


                                else if (xmlPullParser.getName().equals("fengli") && fengliCount == 3) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengli3(xmlPullParser.getText());
                                    fengliCount++;
                                } else if (xmlPullParser.getName().equals("date") && dateCount == 3) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setDate3(xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setHigh3(xmlPullParser.getText().substring(2).trim());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setLow3(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 3) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setType3(xmlPullParser.getText());
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














