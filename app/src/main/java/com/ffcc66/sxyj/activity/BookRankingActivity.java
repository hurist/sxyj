package com.ffcc66.sxyj.activity;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.base.BaseActivity;
import com.ffcc66.sxyj.adapter.BookRankingAdapter;
import com.ffcc66.sxyj.entity.Book;
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
import okhttp3.Call;

/**
 * 图书排行页activity
 */
public class BookRankingActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{

    @BindView(R.id.lvBookRanking)
    ListView lvBookRanking;
    @BindView(R.id.radioGroupRanking)
    RadioGroup radioGroupRanking;
    @BindView(R.id.tvRankListEmpty)
    TextView tvRankListEmpty;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private List<ResponseBook> books = new ArrayList<>();
    private BookRankingAdapter bookRankingAdapter;
    private String type = "collectionnum";
    private int listPos = 0;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_book_ranking;
    }

    @Override
    protected void initData() {
        bookRankingAdapter = new BookRankingAdapter(BookRankingActivity.this, R.layout.item_activity_book_ranking, books);
        lvBookRanking.setAdapter(bookRankingAdapter);
        lvBookRanking.setEmptyView(tvRankListEmpty);
        initRankingData("collectionnum");
        lvBookRanking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResponseBook book = (ResponseBook) bookRankingAdapter.getItem(i);
                Intent intent = new Intent(BookRankingActivity.this, BookDetailActivity.class);
                intent.putExtra("bookinfo", book);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initListener() {

        radioGroupRanking.setOnCheckedChangeListener(this);
        //记录滚动位置
        lvBookRanking.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                listPos = lvBookRanking.getFirstVisiblePosition();
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

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
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rbCollection:
                type = "collectionnum";
                bookRankingAdapter.setNumType("收藏数：");
                initRankingData(type);
                lvBookRanking.setSelection(0);
                break;
            case R.id.rbSearch:
                type = "searchnum";
                bookRankingAdapter.setNumType("搜索数：");
                initRankingData(type);
                lvBookRanking.setSelection(0);
                break;
            case R.id.rbCommend:
                type = "collectionnum";
                bookRankingAdapter.setNumType("评论数：");
                initRankingData(type);
                lvBookRanking.setSelection(0);
                break;
            case R.id.rbClick:
                type = "looknum";
                bookRankingAdapter.setNumType("点击数：");
                initRankingData(type);
                lvBookRanking.setSelection(0);
                break;
            case R.id.rbWordCount:
                type = "wordcount";
                bookRankingAdapter.setNumType("字数：");
                initRankingData(type);
                lvBookRanking.setSelection(0);
                break;
            case R.id.rbNewBook:
                type = "id";
                bookRankingAdapter.setNumType("收藏数：");
                initRankingData(type);
                lvBookRanking.setSelection(0);
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRankingData(type);
        lvBookRanking.setSelection(listPos);
    }

    public void initRankingData(String type) {

        final Dialog dialog = LoadingDialogUtils.createLoadingDialog(BookRankingActivity.this, "加载中");

        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/BookService/getBookRankListByType")
                .addParams("type", type)
                .build()
                .connTimeOut(5000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.w("BookCategaryListActivit", "onError: ", e);
                        Toast.makeText(BookRankingActivity.this,
                                "网络错误", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            LoadingDialogUtils.closeDialog(dialog);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject listresponse = new JSONObject(response);
                            JSONArray tempbooks = listresponse.getJSONArray("datas");
                            List<ResponseBook> templist = new ArrayList<>();
                            if (tempbooks != null) {

                                books.clear();
                                for (int i=0; i<tempbooks.length(); i++) {
                                    JSONObject obj = (JSONObject) tempbooks.get(i);
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
                                books.addAll(templist);
                            } else {
                                Toast.makeText(BookRankingActivity.this,
                                        "请求数据失败...", Toast.LENGTH_SHORT).show();
                            }
                            bookRankingAdapter.notifyDataSetChanged();
                            LoadingDialogUtils.closeDialog(dialog);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
