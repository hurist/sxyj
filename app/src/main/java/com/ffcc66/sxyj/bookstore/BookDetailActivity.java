package com.ffcc66.sxyj.bookstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.View.ListViewForScrollView;
import com.ffcc66.sxyj.base.BaseActivity;
import com.ffcc66.sxyj.entity.TempCommend;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BookDetailActivity extends BaseActivity {
    @BindView(R.id.lvBookCommend)
    ListViewForScrollView lvBookCommend;
    private List<TempCommend> tempCommends = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.activity_book_detail;
    }

    @Override
    public void initData() {

        for (int i=0; i<10; i++) {
            TempCommend tempCommend = new TempCommend();
            tempCommend.setHeadimg(R.drawable.testhead);
            tempCommend.setUsername("用户名"+i);
            tempCommend.setCommend("评论评论评论评论评论评论");
            tempCommend.setDate("2018年01月1"+i+"日");
            tempCommend.setNum(""+i);
            tempCommends.add(tempCommend);
        }
        BookDetailCommendAdapter bookDetailCommendAdapter = new BookDetailCommendAdapter(BookDetailActivity.this,R.layout.activity_book_detail_commend_item,tempCommends);
        lvBookCommend.setAdapter(bookDetailCommendAdapter);
    }

    @Override
    protected void initListener() {

    }

}
