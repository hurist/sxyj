package com.ffcc66.sxyj.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.ReadActivity;
import com.ffcc66.sxyj.View.ListViewForScrollView;
import com.ffcc66.sxyj.adapter.BookDetailCommendAdapter;
import com.ffcc66.sxyj.dialog.DeleteDialog;
import com.ffcc66.sxyj.entity.Book;
import com.ffcc66.sxyj.entity.BookList;
import com.ffcc66.sxyj.entity.Commend;
import com.ffcc66.sxyj.entity.TempCommend;
import com.ffcc66.sxyj.entity.User;
import com.ffcc66.sxyj.response.EntityResponse;
import com.ffcc66.sxyj.response.entity.ResponseBook;
import com.ffcc66.sxyj.util.GsonUtil;
import com.ffcc66.sxyj.util.LoadingDialogUtils;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    @BindView(R.id.tvCommendEmpty)
    TextView tvCommendEmpty;
    @BindView(R.id.toolbar)
    Toolbar toolbar;



    private List<Commend> commends = new ArrayList<>();
    private ResponseBook responseBook;
    private static final String TAG = "BookDetailActivity";
    private String bookpath;
    private BookDetailCommendAdapter bookDetailCommendAdapter;


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
        getPopularCommends();
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

        bookDetailCommendAdapter = new BookDetailCommendAdapter(BookDetailActivity.this,R.layout.activity_book_detail_commend_item,commends);
        lvBookCommend.setAdapter(bookDetailCommendAdapter);
        lvBookCommend.setEmptyView(tvCommendEmpty);
        getPopularCommends();
    }

    private void getPopularCommends() {
        Log.d(TAG, "getPopularCommends: "+responseBook.getId());
        final Dialog dialog = LoadingDialogUtils.createLoadingDialog(BookDetailActivity.this,"请稍等");
        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/BookService/getPopularCommend")
                .addParams("bookid", ""+responseBook.getId())
                .build()
                .connTimeOut(5000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        Log.w("BookDetailActivity", "onError: ", e);
                        Toast.makeText(BookDetailActivity.this,
                                "网络错误", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject listresponse = new JSONObject(response);
                            JSONArray commendArray = listresponse.getJSONArray("datas");
                            List<Commend> templist = new ArrayList<>();
                            if (commendArray != null) {
                                commends.clear();
                                for (int i=0; i<commendArray.length(); i++) {
                                    JSONObject obj = (JSONObject) commendArray.get(i);
                                    Commend commend = new Commend();
                                    commend.setId(obj.getInt("id"));
                                    commend.setUsername(obj.getString("username"));
                                    commend.setBookid(obj.getInt("bookid"));
                                    commend.setAdddate(obj.getString("adddate"));
                                    commend.setTitle(obj.getString("title"));
                                    commend.setCommend(obj.getString("commend"));
                                    commend.setLikenum(obj.getInt("likenum"));
                                    templist.add(commend);
                                }
                                commends.addAll(templist);
                                bookDetailCommendAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(BookDetailActivity.this,
                                        "请求数据失败...", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }
                    }
                });

    }

    protected void initListener() {
        llRead.setOnClickListener(this);
        llAddToBookcase.setOnClickListener(this);
        llCommend.setOnClickListener(this);
        lvBookCommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Commend commend = (Commend) bookDetailCommendAdapter.getItem(i);
                Intent intent = new Intent(BookDetailActivity.this, CommendDatailActivity.class);
                intent.putExtra("commend",commend);
                startActivity(intent);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llRead:
                addNum("looknum", responseBook.getId());
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
                        book.setFileURL(responseBook.getFile());
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
                                    book.setFileURL(responseBook.getFile());
                                    book.setType(1);
                                    book.save();
                                    addNum("collectionnum", responseBook.getId());
                                    tvAddToBookcase.setText("取消收藏");
                                    updateBookInfo();
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
            case R.id.llCommend:
                Intent intent = new Intent(BookDetailActivity.this, BookCommendActivity.class);
                Log.d(TAG, "onClick: "+responseBook.getId());
                intent.putExtra("bookid",responseBook.getId());
                startActivity(intent);
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
                            responseBook.setCover_img(book.getCover_img());
                            responseBook.setWordcount(book.getWordcount());
                            responseBook.setAuthor(book.getAuthor());
                            responseBook.setIntroduction(book.getIntroduction());
                            responseBook.setSearchnum(book.getSearchnum());
                            responseBook.setLooknum(book.getLooknum());
                            responseBook.setType(book.getType());
                            responseBook.setCollectionnum(book.getCollectionnum());

                            Picasso.get().load(responseBook.getCover_img()).placeholder(R.drawable.test).into(ivCover);
                            tvIntroduction.setText(responseBook.getIntroduction());
                            tvWordCount.setText(responseBook.getWordcount()+"");
                            tvAuthorAndType.setText(responseBook.getAuthor()+"▪"+responseBook.getType());
                            tvLookNum.setText(""+responseBook.getLooknum());
                            tvCollectionNum.setText(""+responseBook.getCollectionnum());
                        }
                    }
                });
    }

    public static void addNum(String type, int id){

        OkHttpUtils.get().url("http://192.168.137.1:8080/SXYJApi/BookService/addNum")
                .addParams("id",""+id)
                .addParams("type",type).build().execute(new StringCallback() {
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
