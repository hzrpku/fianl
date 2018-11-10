package com.example.hanzhenrui.proj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private List<View> views;
    private Context context;

    public ViewPagerAdapter(List<View> views,Context context){
        this.views = views;
        this.context = context;
    }
    @Override
    public int getCount() {
        return views.size();
    }//返回滑动的视图的个数

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==object);
    }
}
