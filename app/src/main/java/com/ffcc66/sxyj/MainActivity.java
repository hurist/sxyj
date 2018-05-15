package com.ffcc66.sxyj;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

import com.ffcc66.sxyj.adapter.MyFragmentPagerAdapter;
import com.ffcc66.sxyj.bookcase.BookCaseFragment;
import com.ffcc66.sxyj.bookstore.BookStoreFragment;
import com.ffcc66.sxyj.personal.PersonalFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tab_layoutMain;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private ViewPager view_pagerMain;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private Toolbar toolbar;
    private String[] tabTitles = {"书架","书城","我"};

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();





    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView() {

        tab_layoutMain = findViewById(R.id.tab_layoutMain);
        view_pagerMain = findViewById(R.id.view_pagerMain);
        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        fragmentList.add(new BookCaseFragment());
        fragmentList.add(new BookStoreFragment());
        fragmentList.add(new PersonalFragment());


        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList);

        view_pagerMain.setAdapter(myFragmentPagerAdapter);
        view_pagerMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layoutMain));

    }
}
