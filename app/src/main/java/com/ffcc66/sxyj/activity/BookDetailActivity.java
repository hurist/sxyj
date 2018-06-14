package com.ffcc66.sxyj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.View.ListViewForScrollView;
import com.ffcc66.sxyj.adapter.BookDetailCommendAdapter;
import com.ffcc66.sxyj.entity.TempCommend;
import com.ffcc66.sxyj.response.entity.ResponseBook;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 图书详情页activity
 */
public class BookDetailActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.lvBookCommend)
    ListViewForScrollView lvBookCommend;
    @BindView(R.id.ivCover)
    ImageView ivCover;
    @BindView(R.id.tvWordCount)
    TextView tvWordCount;
    @BindView(R.id.tvLookNum)
    TextView tvLookNum;
    @BindView(R.id.tvCollectionNum)
    TextView tvCollectionNum;
    @BindView(R.id.tvIntroduction)
    TextView tvIntroduction;
    @BindView(R.id.tvBookname)
    TextView tvBookname;
    @BindView(R.id.tvAuthorAndType)
    TextView tvAuthorAndType;
    @BindView(R.id.llRead)
    LinearLayout llRead;
    @BindView(R.id.llAddToBookcase)
    LinearLayout llAddToBookcase;
    @BindView(R.id.llCommend)
    LinearLayout llCommend;

    private List<TempCommend> tempCommends = new ArrayList<>();

    private ResponseBook responseBook;
    private static final String TAG = "BookDetailActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Intent intent = getIntent();
        responseBook = (ResponseBook) intent.getSerializableExtra("bookinfo");
        ButterKnife.bind(this);
        initData();
        initListener();

    }

    public void initData() {

        Picasso.get().load(responseBook.getCover_img()).placeholder(R.drawable.test).into(ivCover);
        tvWordCount.setText(""+responseBook.getWordcount());
        tvCollectionNum.setText(""+responseBook.getCollectionnum());
        tvLookNum.setText(""+responseBook.getLooknum());
        tvIntroduction.setText(responseBook.getIntroduction());
        tvBookname.setText(responseBook.getName());
        tvAuthorAndType.setText(responseBook.getAuthor()+"▪"+responseBook.getType());

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

    protected void initListener() {
        llRead.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llRead:
                break;
        }
    }

    private void downloadingFile() {
        OkHttpUtils.get()
                .url(responseBook.getFile())
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(),responseBook.getFile().split(".")[0]) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: ",e );
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                    }

                    @Override
                    public void onResponse(File response, int id) {

                    }
                });
    }
}
