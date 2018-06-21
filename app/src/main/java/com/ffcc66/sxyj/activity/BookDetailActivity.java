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
import com.ffcc66.sxyj.ReadActivity;
import com.ffcc66.sxyj.View.ListViewForScrollView;
import com.ffcc66.sxyj.adapter.BookDetailCommendAdapter;
import com.ffcc66.sxyj.entity.BookList;
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
    @BindView(R.id.tvRead)
    TextView tvRead;


    private List<TempCommend> tempCommends = new ArrayList<>();

    private ResponseBook responseBook;
    private static final String TAG = "BookDetailActivity";
    private String bookpath;
    private String coverpath;


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

        bookpath = this.getExternalFilesDir("txt").getAbsolutePath()+"/"+ responseBook.getFile().split("/")[4];
        coverpath = this.getExternalFilesDir("cover").getAbsolutePath()+"/"+ responseBook.getFile().split("/")[4];
        if (fileIsCached(bookpath)) {
            tvRead.setText("开始阅读"+"\n"+"（已缓存）");
            llRead.setTag(true);
        } else {
            tvRead.setText("缓存到本地");
            llRead.setTag(false);
        }



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
                if ((Boolean) view.getTag()) {
                    downloadCover();
                    BookList book = new BookList();
                    book.setBookname(responseBook.getName());
                    book.setBookpath(bookpath);
                    book.setCoverpath(coverpath);
                    book.setType(-1);
                    book.save();
                    ReadActivity.openBook(book,BookDetailActivity.this);
                }else {
                    downloadingFile();
                }
                break;
        }
    }

    private void downloadingFile() {
        OkHttpUtils.get()
                .url(responseBook.getFile())
                .build()
                .execute(new FileCallBack(this.getExternalFilesDir("txt").getAbsolutePath(),responseBook.getFile().split("/")[4]) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: ",e );
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        llRead.setClickable(false);
                        tvRead.setText("正在缓存"+(100*progress)+"%");
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        llRead.setClickable(true);
                        tvRead.setText("开始阅读"+"\n"+"（已缓存）");
                        llRead.setTag(true);
                    }
                });
    }

    private void downloadCover() {
        OkHttpUtils.get()
                .url(responseBook.getFile())
                .build()
                .execute(new FileCallBack(this.getExternalFilesDir("cover").getAbsolutePath(),responseBook.getCover_img().split("/")[4]) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: ",e );
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        Log.d(TAG, "onResponse:封面缓存完成 ");
                    }
                });
    }


    public boolean fileIsCached(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
