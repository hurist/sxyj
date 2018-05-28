package com.ffcc66.sxyj.bookstore;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.View.ListViewForScrollView;
import com.ffcc66.sxyj.entity.Book;
import com.ffcc66.sxyj.util.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookStoreFragment extends Fragment {

    private Banner banner;
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

        BookStoreAdapter bookStoreAdapter = new BookStoreAdapter(getContext(),R.layout.fragment_book_store_item,books);
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


    }

}
