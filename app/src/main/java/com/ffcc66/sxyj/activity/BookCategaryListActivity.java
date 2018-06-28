package com.ffcc66.sxyj.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.adapter.BookStoreAdapter;
import com.ffcc66.sxyj.response.entity.ResponseBook;
import com.ffcc66.sxyj.util.LoadingDialogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


/**
 * 图书分类点进去之后的列表页activity
 */
public class BookCategaryListActivity extends AppCompatActivity {

    @BindView(R.id.toolbarCategaryList)
    Toolbar toolbar;
    @BindView(R.id.lvCategaryList)
    ListView lvCategaryList;
    @BindView(R.id.tvEmptyListView)
    TextView tvEmptyListView;
    Dialog dialog;
    @BindView(R.id.refresh)
    MaterialRefreshLayout refersh;


    private String type;
    private List<ResponseBook> responseBookList = new ArrayList<>();
    private BookStoreAdapter bookStoreAdapter;
    private int pagenum = 1;
    private boolean isRefershAndLoadMore = false;
    private boolean canLoadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_categary_list);

        Intent intent = getIntent();
        ButterKnife.bind(this);
        type = intent.getStringExtra("type");
        intitDate();
        initListener();

    }

    private void initListener() {

        refersh.setLoadMore(true);
        refersh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                isRefershAndLoadMore = true;
                pagenum = 1;
                getCategaryDatas(1, type, isRefershAndLoadMore);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                isRefershAndLoadMore = true;
                pagenum = pagenum + 1;
                getCategaryDatas(pagenum, type, isRefershAndLoadMore);

            }
        });

        lvCategaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResponseBook book = (ResponseBook) bookStoreAdapter.getItem(i);
                Intent intent = new Intent(BookCategaryListActivity.this, BookDetailActivity.class);
                intent.putExtra("bookinfo",book);
                startActivity(intent);
            }
        });

    }

    private void intitDate() {

        lvCategaryList.setEmptyView(tvEmptyListView);
        toolbar.setTitle(type);
        toolbar.setTitleTextColor(Color.WHITE);
        setActionBar(toolbar);
        bookStoreAdapter = new BookStoreAdapter(BookCategaryListActivity.this,
                R.layout.item_fragment_book_store, responseBookList);
        lvCategaryList.setAdapter(bookStoreAdapter);
        getCategaryDatas(1, type, isRefershAndLoadMore);
    }

    public void getCategaryDatas(final int pagenum, String type, boolean isRefershAndLoadMore) {

        if (pagenum==1 && (!isRefershAndLoadMore)) {
            dialog = LoadingDialogUtils.createLoadingDialog(BookCategaryListActivity.this,
                    "加载中");
            dialog.show();
        }

        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/BookService/getOnePageBookByType")
                .addParams("pagenum",""+pagenum)
                .addParams("type", type)
                .build()
                .connTimeOut(5000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.w("BookCategaryListActivit", "onError: ", e);
                        Toast.makeText(BookCategaryListActivity.this,
                                "网络错误", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            LoadingDialogUtils.closeDialog(dialog);
                        }
                        refersh.finishRefresh();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject listresponse = new JSONObject(response);
                            JSONArray books = listresponse.getJSONArray("datas");
                            if (pagenum == 1) {
                                responseBookList.clear();
                            }
                            List<ResponseBook> templist = new ArrayList<>();
                            if (books != null) {
                                if (books.length() == 0) { Toast.makeText(BookCategaryListActivity.this, "已经没有更多数据了...", Toast.LENGTH_SHORT).show();}
                                for (int i=0; i<books.length(); i++) {
                                    JSONObject obj = (JSONObject) books.get(i);
                                    ResponseBook book = new ResponseBook();

                                    book.setId(obj.getInt("id"));
                                    book.setName(obj.getString("name"));
                                    book.setAuthor(obj.getString("author"));
                                    book.setIntroduction(obj.getString("introduction"));
                                    book.setCover_img(obj.getString("cover_img"));
                                    book.setFile(obj.getString("filename"));
                                    book.setType(obj.getString("type"));
                                    book.setWordcount(obj.getInt("wordcount"));
                                    book.setCollectionnum(obj.getInt("collectionnum"));
                                    book.setLooknum(obj.getInt("looknum"));
                                    book.setSearchnum(obj.getInt("searchnum"));
                                    templist.add(book);
                                }
                            } else {
                                Toast.makeText(BookCategaryListActivity.this,
                                        "请求数据失败...", Toast.LENGTH_SHORT).show();
                            }
                            responseBookList.addAll(templist);
                            bookStoreAdapter.notifyDataSetChanged();
                            LoadingDialogUtils.closeDialog(dialog);
                            refersh.finishRefreshLoadMore();
                            refersh.finishRefresh();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}
