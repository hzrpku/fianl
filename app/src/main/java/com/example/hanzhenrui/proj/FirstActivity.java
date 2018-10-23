package com.example.hanzhenrui.proj;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import cn.pku.hzr.util.NetUtil;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mUpdateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);//增加点击事件
        mUpdateBtn.setOnClickListener(this);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
           // Log.d("myweather","网络已连接");
            Toast.makeText(FirstActivity.this,"网络已连接",Toast.LENGTH_LONG).show();
        }
        else{
           // Log.d("myweather","网络未连接");
            Toast.makeText(FirstActivity.this,"网络未连接",Toast.LENGTH_LONG).show();

        }
    }
@Override
    public void onClick(View view){
        if (view.getId() == R.id.title_update_btn){
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myweather",cityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
                Log.d("myweather","网络已连接");
                queryWeatherCode(cityCode);
            }
            else{
                Log.d("myweather","网络未连接");
                Toast.makeText(FirstActivity.this,"网络未连接",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void queryWeatherCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("myweather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con =null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                                 response.append(str);
                                 Log.d("myweather", str);

                    }
                    String responseStr = response.toString();
                    Log.d("myweather",responseStr);

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

}














