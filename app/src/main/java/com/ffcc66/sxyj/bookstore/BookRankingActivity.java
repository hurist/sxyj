package com.ffcc66.sxyj.bookstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.base.BaseActivity;
import com.ffcc66.sxyj.entity.Book;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BookRankingActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{

    @BindView(R.id.lvBookRanking)
    ListView lvBookRanking;
    @BindView(R.id.radioGroupRanking)
    RadioGroup radioGroupRanking;
    private List<Book> books = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.activity_book_ranking;
    }

    @Override
    protected void initData() {
        initCollectionRankingData();
        BookRankingAdapter bookRankingAdapter = new BookRankingAdapter(BookRankingActivity.this,R.layout.activity_book_ranking_item,books);
        lvBookRanking.setAdapter(bookRankingAdapter);
    }

    @Override
    protected void initListener() {
        radioGroupRanking.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rbCollection:
                initCollectionRankingData();
                BookRankingAdapter bookRankingAdapter = new BookRankingAdapter(BookRankingActivity.this,R.layout.activity_book_ranking_item,books);
                lvBookRanking.setAdapter(bookRankingAdapter);
                break;
            case R.id.rbSearch:
                initSearchRankingData();
                BookRankingAdapter bookRankingAdapter1 = new BookRankingAdapter(BookRankingActivity.this,R.layout.activity_book_ranking_item,books);
                lvBookRanking.setAdapter(bookRankingAdapter1);
                break;
            case R.id.rbCommend:
                initCommendRankingData();
                BookRankingAdapter bookRankingAdapter2 = new BookRankingAdapter(BookRankingActivity.this,R.layout.activity_book_ranking_item,books);
                lvBookRanking.setAdapter(bookRankingAdapter2);
                break;
            case R.id.rbClick:
                initClickRankingData();
                BookRankingAdapter bookRankingAdapter3 = new BookRankingAdapter(BookRankingActivity.this,R.layout.activity_book_ranking_item,books);
                lvBookRanking.setAdapter(bookRankingAdapter3);
                break;
            case R.id.rbNewBook:
                initNewBookRankingData();
                BookRankingAdapter bookRankingAdapter4 = new BookRankingAdapter(BookRankingActivity.this,R.layout.activity_book_ranking_item,books);
                lvBookRanking.setAdapter(bookRankingAdapter4);
                break;
        }
    }

    public void initCollectionRankingData() {
        books.clear();
        for (int i=0; i<100; i++) {
            Book book = new Book();
            book.setCover(R.drawable.test);
            book.setBookname("这是收藏榜"+i);
            book.setWriter("作者");
            book.setCollectionNum("收藏：10万");
            book.setIntroduction("介绍介绍介绍介绍介绍介绍介绍介绍介绍");
            book.setRankingNum(""+i);
            books.add(book);
        }
    }
    public void initSearchRankingData() {
        books.clear();
        for (int i=0; i<100; i++) {
            Book book = new Book();
            book.setCover(R.drawable.test);
            book.setBookname("这是热搜榜"+i);
            book.setWriter("作者");
            book.setCollectionNum("收藏：10万");
            book.setIntroduction("介绍介绍介绍介绍介绍介绍介绍介绍介绍");
            book.setRankingNum(""+i);
            books.add(book);
        }
    }
    public void initCommendRankingData() {
        books.clear();
        for (int i=0; i<100; i++) {
            Book book = new Book();
            book.setCover(R.drawable.test);
            book.setBookname("这是好评榜"+i);
            book.setWriter("作者");
            book.setCollectionNum("收藏：10万");
            book.setIntroduction("介绍介绍介绍介绍介绍介绍介绍介绍介绍");
            book.setRankingNum(""+i);
            books.add(book);
        }
    }
    public void initClickRankingData() {
        books.clear();
        for (int i=0; i<100; i++) {
            Book book = new Book();
            book.setCover(R.drawable.test);
            book.setBookname("这是点击榜"+i);
            book.setWriter("作者");
            book.setCollectionNum("收藏：10万");
            book.setIntroduction("介绍介绍介绍介绍介绍介绍介绍介绍介绍");
            book.setRankingNum(""+i);
            books.add(book);
        }
    }
    public void initNewBookRankingData() {
        books.clear();
        for (int i=0; i<100; i++) {
            Book book = new Book();
            book.setCover(R.drawable.test);
            book.setBookname("这是新书榜"+i);
            book.setWriter("作者");
            book.setCollectionNum("收藏：10万");
            book.setIntroduction("介绍介绍介绍介绍介绍介绍介绍介绍介绍");
            book.setRankingNum(""+i);
            books.add(book);
        }
    }
}
