package com.example.hanzhenrui.proj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import cn.pku.hzr.bean.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pku.hzr.app.MyApplication;
import cn.pku.hzr.bean.Cn2Spell;

public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;
    private MyApplication myApplication;
    private SearchView searchView; //增加搜索框控件
    private ArrayAdapter<String> adapter;
    private String returnCode = "101010100";
    private ArrayList<String> mSearchResult = new ArrayList<>();//搜索结果，只存放城市名
    private Map<String,String> nameToCode = new HashMap<>();//城市名到编码
    private Map<String,String> nameToPinyin = new HashMap<>();//城市名到拼音
    private ListView listView = null;
    private TextView cityselected = null;
    private List<City> listcity = MyApplication.getInstance().getmCityList();
    private int listSize = listcity.size();
    private String[] city = new String[listSize];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        cityselected = (TextView) findViewById(R.id.title_name);
        mBackBtn.setOnClickListener(this);
        for (int i=0;i<listSize;i++){
            city[i] = listcity.get(i).getCity();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,city);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String returnCityName = mSearchResult.get(i);
                Toast.makeText(SelectCity.this,"你已选择： "+returnCityName,Toast.LENGTH_SHORT).show();
                returnCode = nameToCode.get(returnCityName);
                cityselected.setText("当前城市： "+returnCityName);

            }
        });


        searchView = (SearchView) findViewById(R.id.search);//
        searchView.setIconified(true);//需要点击搜索图标才展开搜索框
        searchView.setQueryHint("请输入城市名称或拼音");//隐藏文字
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    if (mSearchResult != null)
                        mSearchResult.clear();
                    for (String str : nameToPinyin.keySet()) {
                        if (str.contains(newText) || nameToPinyin.get(str).contains(newText)) {
                            mSearchResult.add(str);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SelectCity.this, "检索中", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        initView();
    }





        protected void initView(){
            myApplication = MyApplication.getInstance();
            ArrayList<City> mCityList = (ArrayList<City>) myApplication.getmCityList();
            String strName;
            String strNamePinyin;
            String strCode;
            for (City city : mCityList){
                strCode = city.getNumber();
                strName = city.getCity();
                strNamePinyin = Cn2Spell.converterToSpell(strName);
                nameToCode.put(strName,strCode);
                nameToPinyin.put(strName,strNamePinyin);
                mSearchResult.add(strName);
            }
            adapter = new ArrayAdapter<>(SelectCity.this,android.R.layout.simple_list_item_1,mSearchResult);
            listView.setAdapter(adapter);
        }






    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
               // int position = listView.getCheckedItemPosition();
               // String select_cityCode = listcity.get(position).getNumber();
                Intent i = new Intent();
                i.putExtra("cityCode",returnCode);
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }
}