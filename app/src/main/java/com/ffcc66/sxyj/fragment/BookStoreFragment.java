package com.ffcc66.sxyj.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.View.ListViewForScrollView;
import com.ffcc66.sxyj.activity.BookDetailActivity;
import com.ffcc66.sxyj.activity.BookRankingActivity;
import com.ffcc66.sxyj.adapter.BookStoreAdapter;
import com.ffcc66.sxyj.activity.BookCategaryActivity;
import com.ffcc66.sxyj.activity.BookListActivity;
import com.ffcc66.sxyj.entity.Book;
import com.ffcc66.sxyj.util.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 书城fragment
 */
public class BookStoreFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    private Banner banner;
    private LinearLayout llRanking,llCategary,llBookList;
    private ListViewForScrollView lvNewBookRecommend;
    private List image = new ArrayList();
    private List<Book> books = new ArrayList<>();


    public BookStoreFragment() {
        // Required empty public constructor
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_store, container, false);



        initData();
        initView(view);

        BookStoreAdapter bookStoreAdapter = new BookStoreAdapter(getContext(),R.layout.item_fragment_book_store,books);
        lvNewBookRecommend.setAdapter(bookStoreAdapter);
        
        return view;
    }

    private void initData() {

        image.add(R.drawable.bannar_test1);
        image.add(R.drawable.bannar_test2);

        for (int i=0; i<10; i++) {
            Book book = new Book();
            book.setCover(R.drawable.test);
            book.setBookname("这是第"+i+"本书");
            book.setWriter("作者");
            book.setIntroduction("这是简介这是简介这是简介这是简介这是简介这是简介这是简介这是简介这是简介这是简介这是简介这是简介这是简介这是简介");
            books.add(book);
        }

    }

    private void initView(View view) {

        banner = view.findViewById(R.id.banner);
        banner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        banner.setImages(image);
        banner.setImageLoader(new GlideImageLoader());
        banner.start();

        lvNewBookRecommend = view.findViewById(R.id.lvNewBookRecommend);
        lvNewBookRecommend.setOnItemClickListener(this);

        llRanking = view.findViewById(R.id.llRanking);
        llRanking.setOnClickListener(this);
        llCategary = view.findViewById(R.id.llCategary);
        llCategary.setOnClickListener(this);
        llBookList = view.findViewById(R.id.llBookList);
        llBookList.setOnClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(getActivity(), BookDetailActivity.class));
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
            case R.id.llBookList:
                startActivity(new Intent(getActivity(), BookListActivity.class));
                break;

        }
    }
}
