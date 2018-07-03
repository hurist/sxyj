package com.ffcc66.sxyj.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.ffcc66.sxyj.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 图书分类Activity
 */
public class BookCategaryActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.llForeignLiterature)
    LinearLayout llForeignLiterature;
    @BindView(R.id.llXDDWX)
    LinearLayout llXDDWX;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_categary);
        ButterKnife.bind(this);

        initData();
        initListener();

    }

    private void initListener() {

        llXDDWX.setOnClickListener(this);
        llForeignLiterature.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initData() {
    }

    @Override
    public void onClick(View view) {
        String type = (String) view.getTag();
        Intent intent = new Intent(BookCategaryActivity.this, BookCategaryListActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);
    }
}
