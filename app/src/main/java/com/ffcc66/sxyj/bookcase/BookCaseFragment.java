package com.ffcc66.sxyj.bookcase;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.ReadActivity;
import com.ffcc66.sxyj.entity.Book;
import com.ffcc66.sxyj.entity.BookList;
import com.ffcc66.sxyj.filechooser.FileChooserActivity;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookCaseFragment extends Fragment implements Toolbar.OnMenuItemClickListener,AdapterView.OnItemClickListener {

    private ListView lvBookcase;
    private List<BookList> booklist = new ArrayList<BookList>();
    BookCaseAdapter bookCaseAdapter;
    private Toolbar toolbar;

    private int itemPosition;


    public BookCaseFragment() {
        super();
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_book_case, container, false);
        initData();
        setHasOptionsMenu(true);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        //getActivity().setActionBar(toolbar);   使用这句后无法显示menu
        toolbar.inflateMenu(R.menu.menu_bookcase);
        //toolbar.getMenu().findItem(R.id.itemScan).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); 设置showAsAction无效时使用
        toolbar.setOverflowIcon(ContextCompat.getDrawable(getContext(),R.drawable.bookcase_manager));
        toolbar.setOnMenuItemClickListener(this);

        lvBookcase = view.findViewById(R.id.lvBookcase);
        bookCaseAdapter = new BookCaseAdapter(getActivity(), R.layout.fragment_book_case_item, booklist);
        lvBookcase.setAdapter(bookCaseAdapter);
        lvBookcase.setOnItemClickListener(this);

        return view;
    }

    private void initData() {
        booklist = DataSupport.findAll(BookList.class);
    }



    /**
     * 设置listview的高度随item的高度变化
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ArrayAdapter listAdapter = (ArrayAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.itemScan:
                getActivity().startActivity(new Intent(getContext(), FileChooserActivity.class));
                break;
            case R.id.itemBatchManagement:
                DataSupport.deleteAll("booklist");
                bookCaseAdapter.nitifyDataRefresh();
                break;
        }

        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (booklist.size() > position) {
            itemPosition = position;
            String bookname = booklist.get(itemPosition).getBookname();

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
}
