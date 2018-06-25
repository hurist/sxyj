package com.ffcc66.sxyj.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.ReadActivity;
import com.ffcc66.sxyj.View.ListViewForScrollView;
import com.ffcc66.sxyj.adapter.BookDetailCommendAdapter;
import com.ffcc66.sxyj.dialog.DeleteDialog;
import com.ffcc66.sxyj.entity.Book;
import com.ffcc66.sxyj.entity.BookList;
import com.ffcc66.sxyj.entity.TempCommend;
import com.ffcc66.sxyj.entity.User;
import com.ffcc66.sxyj.response.EntityResponse;
import com.ffcc66.sxyj.response.entity.ResponseBook;
import com.ffcc66.sxyj.util.GsonUtil;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
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
    @BindView(R.id.tvAddToBookcase)
    TextView tvAddToBookcase;



    private List<TempCommend> tempCommends = new ArrayList<>();
    private ResponseBook responseBook;
    private static final String TAG = "BookDetailActivity";
    private String bookpath;


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

    @Override
    protected void onResume() {
        super.onResume();
        updateBookInfo();
        List<BookList> bookLists = DataSupport.where("bookid = ?", ""+responseBook.getId()).find(BookList.class);
        if (bookLists.size() > 0) {
            tvAddToBookcase.setText("取消收藏");
        }
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
        if (fileIsCached(bookpath)) {
            tvRead.setText("开始阅读"+"\n"+"（已缓存）");
            llRead.setTag(true);
        } else {
            tvRead.setText("缓存到本地");
            llRead.setTag(false);
        }
        List<BookList> bookLists = DataSupport.where("bookid = ?", ""+responseBook.getId()).find(BookList.class);
        if (bookLists.size() > 0) {
            tvAddToBookcase.setText("取消收藏");
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
        llAddToBookcase.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llRead:
                addLookNum();
                if ((Boolean) view.getTag()) {

                    List<BookList> bookLists = DataSupport.where("bookname = ? and writer = ?", responseBook.getName(), responseBook.getAuthor()).find(BookList.class);
                    BookList book = new BookList();
                    if (bookLists.size() > 0) {
                        book = bookLists.get(0);
                    }else {
                        book.setBookid(responseBook.getId());
                        book.setBookname(responseBook.getName());
                        book.setWriter(responseBook.getAuthor());
                        book.setWordcount(responseBook.getWordcount());
                        book.setBookpath(bookpath);
                        book.setCoverpath(responseBook.getCover_img());
                        book.setType(-1);
                        book.save();
                    }
                    ReadActivity.openBook(book,BookDetailActivity.this);
                }else {
                    downloadingFile();
                }
                break;
            case R.id.llAddToBookcase:
                if (tvAddToBookcase.getText().toString().equals("加入书架")) {
                    new AlertDialog.Builder(BookDetailActivity.this)
                            .setTitle("确认加入书架？")
                            .setMessage("加入书架将会缓存全书")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (tvRead.getText().toString().equals("缓存到本地")) {
                                        downloadingFile();
                                    }
                                    BookList book = new BookList();
                                    book.setBookid(responseBook.getId());
                                    book.setBookname(responseBook.getName());
                                    book.setWriter(responseBook.getAuthor());
                                    book.setWordcount(responseBook.getWordcount());
                                    book.setBookpath(bookpath);
                                    book.setCoverpath(responseBook.getCover_img());
                                    book.setType(1);
                                    book.save();
                                    tvAddToBookcase.setText("取消收藏");
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                } else {
                    List<BookList> bookLists = DataSupport.where("bookid = ?", ""+responseBook.getId()).find(BookList.class);
                    if (bookLists.size() > 0) {
                        DataSupport.delete(BookList.class,bookLists.get(0).getId());
                        Toast.makeText(BookDetailActivity.this, "已将《"+bookLists.get(0).getBookname()+"》移除",Toast.LENGTH_LONG).show();
                        tvAddToBookcase.setText("加入书架");
                    }
                }
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

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

    public boolean fileIsCached(String strFile) {
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

    private void updateBookInfo() {
        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/BookService/getBookInfoById")
                .addParams("id",""+responseBook.getId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: ",e );
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        EntityResponse<ResponseBook> entityResponse = GsonUtil.GsonToBean(response,new TypeToken<EntityResponse<ResponseBook>>(){}.getType());

                        if (entityResponse.getCode().equals("ok")) {
                            ResponseBook book = entityResponse.getObject();
                            responseBook.setLooknum(book.getLooknum());
                            responseBook.setCollectionnum(book.getCollectionnum());
                            tvLookNum.setText(""+responseBook.getLooknum());
                            tvCollectionNum.setText(""+responseBook.getCollectionnum());
                        }
                    }
                });
    }

    public void addLookNum(){

        OkHttpUtils.get().url("http://192.168.137.1:8080/SXYJApi/BookService/addLookNum")
                .addParams("id",""+responseBook.getId()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "onError: ",e );
            }

            @Override
            public void onResponse(String response, int id) {

            }
        });
    }

}
