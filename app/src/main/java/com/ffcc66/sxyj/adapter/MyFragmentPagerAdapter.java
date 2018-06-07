package com.ffcc66.sxyj.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by yanhe on 2018/5/7.
 * 首页viewpager适配器
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    private String[] titles;

    public MyFragmentPagerAdapter(FragmentManager fm,List<Fragment> fragmentList) {
        super(fm);
        this.titles = titles;
        this.fragmentList = fragmentList;
    }

    //获取指定位置的fragment的页面
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    //获取页面总数
    @Override
    public int getCount() {
        return fragmentList.size();
    }

//    //返回页面对应的标题
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return titles[position];
//    }
}
