package com.ffcc66.sxyj.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.ReadActivity;
import com.ffcc66.sxyj.base.BaseFragment;
import com.ffcc66.sxyj.adapter.BookCaseAdapter;
import com.ffcc66.sxyj.entity.BookList;
import com.ffcc66.sxyj.filechooser.FileChooserActivity;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.support.constraint.Constraints.TAG;


/**
 * 书架Fragment
 */
public class BookCaseFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener,AdapterView.OnItemClickListener {

    @BindView(R.id.lvBookcase)
    ListView lvBookcase;
    @BindView(R.id.toolbarBookCase)
    Toolbar toolbar;
    private List<BookList> booklist = new ArrayList<BookList>();
    BookCaseAdapter bookCaseAdapter;

    private boolean isCreate = false;


    private int itemPosition;


    public BookCaseFragment() {
        super();
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_book_case;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData(View view) {
        setHasOptionsMenu(true);

        toolbar.setTitle("");
        //getActivity().setActionBar(toolbar);   使用这句后无法显示menu
        toolbar.inflateMenu(R.menu.menu_bookcase);
        //toolbar.getMenu().findItem(R.id.itemScan).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); 设置showAsAction无效时使用
        toolbar.setOverflowIcon(ContextCompat.getDrawable(getContext(),R.drawable.bookcase_manager));
        toolbar.setOnMenuItemClickListener(this);

        //booklist = DataSupport.findAll(BookList.class);
        bookCaseAdapter = new BookCaseAdapter(getActivity(), R.layout.item_fragment_book_case, booklist);
        lvBookcase.setAdapter(bookCaseAdapter);
        lvBookcase.setOnItemClickListener(this);
        isCreate = true;
    }

    @Override
    protected void initListener() {
        toolbar.setOnMenuItemClickListener(this);
        lvBookcase.setOnItemClickListener(this);
    }

    @Override
    protected void LazyLoad() {
        super.LazyLoad();
        Log.d(TAG, "LazyLoad: ");
        booklist = DataSupport.findAll(BookList.class);

        if (isCreate) {
            Log.d(TAG, "LazyLoad: "+"notifyDataSetChanged");
            bookCaseAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.itemScan:
                getActivity().startActivity(new Intent(getContext(), FileChooserActivity.class));
                break;
            case R.id.itemBatchManagement:
                DataSupport.deleteAll("booklist");
                bookCaseAdapter.notifyDataSetChanged();
                break;
        }

        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (booklist.size() > position) {
            itemPosition = position;
//            String bookname = booklist.get(itemPosition).getBookname();

            bookCaseAdapter.setItemToFirst(itemPosition);
//                bookLists = DataSupport.findAll(BookList.class);
            final BookList bookList = booklist.get(itemPosition);   //获取当前点击书本的图书信息
            bookList.setId(booklist.get(0).getId());   //将这本点击的书在书架中设置成第一本
            final String path = bookList.getBookpath();   //获取图书路径
            File file = new File(path);         //打开文件
            if (!file.exists()) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getString(R.string.app_name))
                        .setMessage(path + "文件不存在,是否删除该书本？")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataSupport.deleteAll(BookList.class, "bookpath = ?", path);
                                booklist = DataSupport.findAll(BookList.class);
                                bookCaseAdapter.setBookList(booklist);
                            }
                        }).setCancelable(true).show();
                return;
            }

            ReadActivity.openBook(bookList,getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bookCaseAdapter.setBookList(DataSupport.findAll(BookList.class));
    }
}
