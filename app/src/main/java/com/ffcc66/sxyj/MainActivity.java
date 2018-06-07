package com.ffcc66.sxyj;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.ffcc66.sxyj.adapter.MyFragmentPagerAdapter;
import com.ffcc66.sxyj.base.BaseActivity;
import com.ffcc66.sxyj.fragment.BookCaseFragment;
import com.ffcc66.sxyj.fragment.BookStoreFragment;
import com.ffcc66.sxyj.entity.BookList;
import com.ffcc66.sxyj.fragment.PersonalFragment;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.tab_layoutMain)
    TabLayout tab_layoutMain;
    @BindView(R.id.view_pagerMain)
    ViewPager view_pagerMain;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    private static final String TAG = "\nMainActivity";

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private String[] tabTitles = {"书架","书城","我"};

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

        fragmentList.add(new BookCaseFragment());
        fragmentList.add(new BookStoreFragment());
        fragmentList.add(new PersonalFragment());




        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList);
        view_pagerMain.setAdapter(myFragmentPagerAdapter);

    }

    @Override
    protected void initListener() {
        view_pagerMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layoutMain));
        tab_layoutMain.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pagerMain.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<BookList> books = DataSupport.findAll(BookList.class);
        for (BookList book: books) {
            Log.d(TAG, "initData: "+book.getBookname());
            Log.d(TAG, "initData: "+book.getBookpath());
            Log.d(TAG, "initData: "+book.getLastreadtime());
        }
    }
}
