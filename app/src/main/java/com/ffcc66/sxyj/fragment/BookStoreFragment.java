package com.ffcc66.sxyj.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.ffcc66.sxyj.MainActivity;
import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.View.ListViewForScrollView;
import com.ffcc66.sxyj.activity.BookDetailActivity;
import com.ffcc66.sxyj.activity.BookRankingActivity;
import com.ffcc66.sxyj.activity.LoginActivity;
import com.ffcc66.sxyj.activity.SearchActivity;
import com.ffcc66.sxyj.adapter.BookStoreAdapter;
import com.ffcc66.sxyj.activity.BookCategaryActivity;
import com.ffcc66.sxyj.activity.BookListActivity;
import com.ffcc66.sxyj.base.BaseFragment;
import com.ffcc66.sxyj.entity.Book;
import com.ffcc66.sxyj.response.entity.ResponseBook;
import com.ffcc66.sxyj.util.GlideImageLoader;
import com.ffcc66.sxyj.util.LoadingDialogUtils;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


/**
 * 书城fragment
 */
public class BookStoreFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.llRanking)
    LinearLayout llRanking;
    @BindView(R.id.llCategary)
    LinearLayout llCategary;
    @BindView(R.id.lvNewBookRecommend)
    ListViewForScrollView lvNewBookRecommend;
    @BindView(R.id.ibSearch)
    ImageButton ibSearch;

    @BindView(R.id.ivPopularCover1)
    ImageView ivPopularCover1;
    @BindView(R.id.ivPopularCover2)
    ImageView ivPopularCover2;
    @BindView(R.id.ivPopularCover3)
    ImageView ivPopularCover3;

    @BindView(R.id.tvPopularName1)
    TextView tvPopularName1;
    @BindView(R.id.tvPopularName2)
    TextView tvPopularName2;
    @BindView(R.id.tvPopularName3)
    TextView tvPopularName3;
    @BindView(R.id.bookstorerefersh)
    MaterialRefreshLayout bookstorerefersh;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private List image = new ArrayList();
    private List<Book> books = new ArrayList<>();
    private List<ResponseBook> responseNewBookList = new ArrayList<>();
    private List<ResponseBook> responsePopularBookList = new ArrayList<>();
    BookStoreAdapter bookStoreAdapter;

    Dialog dialog1;
    Dialog dialog2;

    private static final String TAG = "BookStoreFragment";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {      //接收其他子线程的消息
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Picasso.get().load(responsePopularBookList.get(0).getCover_img()).into(ivPopularCover1);
                    Picasso.get().load(responsePopularBookList.get(1).getCover_img()).into(ivPopularCover2);
                    Picasso.get().load(responsePopularBookList.get(2).getCover_img()).into(ivPopularCover3);
                    tvPopularName1.setText(responsePopularBookList.get(0).getName());
                    tvPopularName2.setText(responsePopularBookList.get(1).getName());
                    tvPopularName3.setText(responsePopularBookList.get(2).getName());
                    break;
            }
        }
    };

    public BookStoreFragment() {
        // Required empty public constructor
        super();
    }


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//  //      RequestData();
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_store,container,false);
        ButterKnife.bind(this,view);
        initData();
        initListener();
        return view;
    }


    //初始化数据和加载数据
    protected void initData() {
        image.add(R.drawable.bannar_test1);
        image.add(R.drawable.bannar_test2);

        banner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        banner.setImages(image);
        banner.setImageLoader(new GlideImageLoader());
        banner.start();

        Log.d(TAG, "initData:Size: "+responsePopularBookList.size());
        if (responsePopularBookList.size() > 0) {
            Picasso.get().load(responsePopularBookList.get(0).getCover_img()).into(ivPopularCover1);
            Picasso.get().load(responsePopularBookList.get(1).getCover_img()).into(ivPopularCover2);
            Picasso.get().load(responsePopularBookList.get(2).getCover_img()).into(ivPopularCover3);

            tvPopularName1.setText(responsePopularBookList.get(0).getName());
            tvPopularName1.setText(responsePopularBookList.get(1).getName());
            tvPopularName1.setText(responsePopularBookList.get(2).getName());
        }

        bookStoreAdapter = new BookStoreAdapter(getActivity(),R.layout.item_fragment_book_store, responseNewBookList);
        lvNewBookRecommend.setAdapter(bookStoreAdapter);
    }

    protected void initListener() {
        lvNewBookRecommend.setOnItemClickListener(this);
        llRanking.setOnClickListener(this);
        llCategary.setOnClickListener(this);
        bookstorerefersh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                RequestData(true);
            }
        });
        ibSearch.setOnClickListener(this);

    }


    //新书推荐点击监听
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ResponseBook responseBook = (ResponseBook) bookStoreAdapter.getItem(i);
        startActivity(new Intent(getActivity(), BookDetailActivity.class).putExtra("bookinfo",responseBook));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llRanking:
                startActivity(new Intent(getActivity(), BookRankingActivity.class));
                break;
            case R.id.llCategary:
                startActivity(new Intent(getActivity(), BookCategaryActivity.class));
                break;
            case R.id.ibSearch:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
        }
    }

    public void RequestData(boolean isRefersh) {


        if (!isRefersh) {
            dialog1 = LoadingDialogUtils.createLoadingDialog(getActivity(), "加载中，请稍等");
            dialog1.show();
        }
        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/BookService/getPopularBook")
                .addParams("num","3")
                .build()
                .connTimeOut(5000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LoadingDialogUtils.closeDialog(dialog2);
                        Toast.makeText(getActivity(), "网络连接失败",
                                Toast.LENGTH_LONG).show();
                        bookstorerefersh.finishRefresh();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject listresponse = new JSONObject(response);
                            JSONArray books = listresponse.getJSONArray("datas");

                            responsePopularBookList.clear();
                            if (books != null) {
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

                                    responsePopularBookList.add(book);
                                }
                            } else {
                                Toast.makeText(getActivity(), "请求数据失败...", Toast.LENGTH_LONG).show();
                            }
                            LoadingDialogUtils.closeDialog(dialog2);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        if (!isRefersh) {
            dialog2 = LoadingDialogUtils.createLoadingDialog(getActivity(), "加载中，请稍等");
            dialog2.show();
        }
        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/BookService/getNewBook")
                .addParams("num", "10")
                .build()
                .connTimeOut(5000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LoadingDialogUtils.closeDialog(dialog1);
                        Toast.makeText(getActivity(), "网络连接失败",
                                Toast.LENGTH_LONG).show();
                        bookstorerefersh.finishRefresh();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        try {
                            JSONObject listresponse = new JSONObject(response);
                            JSONArray books = listresponse.getJSONArray("datas");

                            responseNewBookList.clear();
                            if (books != null) {
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

                                    responseNewBookList.add(book);
                                }
                                bookStoreAdapter.notifyDataSetChanged();
                                Message message = new Message();
                                message.what = 0;
                                mHandler.sendMessage(message);
                            } else {
                                Toast.makeText(getActivity(), "请求数据失败...", Toast.LENGTH_LONG).show();
                            }
                            LoadingDialogUtils.closeDialog(dialog1);
                            bookstorerefersh.finishRefresh();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });




    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }

    @Override
    public void onResume() {
        super.onResume();
        RequestData(false);
    }
}
