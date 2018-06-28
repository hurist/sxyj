package com.ffcc66.sxyj.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.adapter.BookStoreAdapter;
import com.ffcc66.sxyj.adapter.SearchHistoryAdapter;
import com.ffcc66.sxyj.entity.SearchHistory;
import com.ffcc66.sxyj.response.entity.ResponseBook;
import com.ffcc66.sxyj.util.LoadingDialogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    @BindView(R.id.searchToolbar)
    Toolbar searchToolbar;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.lvSearchResult)
    ListView lvSearchResult;
    @BindView(R.id.lvSearchHistory)
    ListView lvSearchHistory;
    @BindView(R.id.searchHistoryEmptyText)
    TextView searchHistoryEmptyText;
    @BindView(R.id.searchResuleEmptyText)
    TextView searchResuleEmptyText;
    @BindView(R.id.tvDeleteHistory)
    TextView tvDeleteHistory;
    @BindView(R.id.llSearchContent)
    LinearLayout llSearchContent;


    private List<ResponseBook> results = new ArrayList<>();
    private List<SearchHistory> searchhistory = new ArrayList<>();

    private SearchHistoryAdapter searchHistoryAdapter;
    private BookStoreAdapter bookStoreAdapter;
    private String username = "anyone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initData();
        initListener();
        initSearchHistory();

    }

    private void initData() {

        username = getSharedPreferences("userdata",MODE_PRIVATE).getString("username","anyone");

        setActionBar(searchToolbar);
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        searchView.setSubmitButtonEnabled(true);
        searchView.clearFocus();
        //设置searchview字体颜色
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(Color.parseColor("#e4e4e4e4"));

        searchHistoryAdapter = new SearchHistoryAdapter(SearchActivity.this,R.layout.item_search_history,searchhistory);
        lvSearchHistory.setAdapter(searchHistoryAdapter);
        lvSearchHistory.setEmptyView(searchHistoryEmptyText);

        lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResponseBook responseBook = (ResponseBook) bookStoreAdapter.getItem(i);
                Intent intent = new Intent(SearchActivity.this, BookDetailActivity.class);
                intent.putExtra("bookinfo", responseBook);
                BookDetailActivity.addNum("searchnum",responseBook.getId());
                startActivity(intent);
            }
        });

        lvSearchHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchHistory searchHistory = (SearchHistory) searchHistoryAdapter.getItem(i);
                getSearchResult(searchHistory.getSearchword());
            }
        });
        tvDeleteHistory.setOnClickListener(this);
    }


    private void initListener() {
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String queryword) {
        saveHistory(queryword);
        getSearchResult(queryword);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }


    public void getSearchResult(String word) {

        bookStoreAdapter = new BookStoreAdapter(SearchActivity.this,R.layout.item_fragment_book_store, results);
        llSearchContent.setVisibility(View.GONE);
        lvSearchHistory.setEmptyView(null);
        lvSearchResult.setEmptyView(searchResuleEmptyText);
        lvSearchResult.setAdapter(bookStoreAdapter);
        final Dialog dialog = LoadingDialogUtils.createLoadingDialog(SearchActivity.this,"正在搜索，请稍等");
        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/BookService/searchBooks")
                .addParams("keyword", word)
                .build()
                .connTimeOut(5000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        Toast.makeText(SearchActivity.this, "网络连接失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject listresponse = new JSONObject(response);
                            JSONArray books = listresponse.getJSONArray("datas");
                            List<ResponseBook> templist = new ArrayList<>();
                            if (books != null) {
                                if (books.length() == 0) { Toast.makeText(SearchActivity.this, "无搜索结果...", Toast.LENGTH_SHORT).show();}
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
                                Toast.makeText(SearchActivity.this,
                                        "请求数据失败...", Toast.LENGTH_SHORT).show();
                            }
                            results.clear();
                            results.addAll(templist);
                            bookStoreAdapter.notifyDataSetChanged();
                            searchView.clearFocus();
                            dialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void saveHistory(String searchword) {

        boolean exist = false;
        for (int i=0; i<searchhistory.size(); i++) {
            if(searchword.equals(searchhistory.get(i).getSearchword())) {
                exist = true;
            }
        }
        if (!exist) {
            SearchHistory searchHistory = new SearchHistory(username, searchword);
            searchhistory.add(searchHistory);
            searchHistory.save();
        }

    }

    private void initSearchHistory() {

        searchhistory.clear();
        searchhistory.addAll(DataSupport.where("username = ?", username).find(SearchHistory.class));
        searchHistoryAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvDeleteHistory:
                if (searchhistory.size() > 0) {
                    new AlertDialog.Builder(SearchActivity.this)
                            .setTitle("清空历史")
                            .setMessage("确认要清除所有历史记录吗？")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    searchhistory.clear();
                                    DataSupport.deleteAll(SearchHistory.class, "username = ?", username);
                                    searchHistoryAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                }
        }
    }
}
