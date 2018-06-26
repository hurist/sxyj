package com.ffcc66.sxyj.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ffcc66.sxyj.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 图书分类Activity
 */
public class BookCategaryActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.llForeignLiterature)
    LinearLayout llForeignLiterature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_categary);
        ButterKnife.bind(this);

        initData();
        initListener();

    }

    private void initListener() {

        llForeignLiterature.setOnClickListener(this);

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
