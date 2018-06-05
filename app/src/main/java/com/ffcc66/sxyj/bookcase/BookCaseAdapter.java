package com.ffcc66.sxyj.bookcase;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.entity.Book;
import com.ffcc66.sxyj.entity.BookList;

import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BookCaseAdapter extends ArrayAdapter {

    protected List<AsyncTask<Void, Void, Boolean>> myAsyncTasks = new ArrayList<>();
    private List<BookList> bilist;

    private int resourceId;
    public BookCaseAdapter(Context context, int viewResouceId, List<BookList> bookList) {
        super(context,viewResouceId,bookList);
        this.resourceId = viewResouceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BookList book = (BookList) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivCover = view.findViewById(R.id.ivCover);
            viewHolder.tvBookName = view.findViewById(R.id.tvBookName);
            viewHolder.tvLastReadTime = view.findViewById(R.id.tvLastReadTime);
            viewHolder.tvReadProcess = view.findViewById(R.id.tvReadProcess);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        SimpleDateFormat sp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        if (book.getCoverpath() == null) {
            viewHolder.ivCover.setImageResource(R.drawable.test);
        }
        viewHolder.tvBookName.setText(book.getBookname());
        viewHolder.tvReadProcess.setText("阅读进度"+book.getReadprocess());
        viewHolder.tvLastReadTime.setText("上次阅读时间："+sp.format(new Date(book.getLastreadtime())));

        return view;
    }

    class ViewHolder {
        public ImageView ivCover;
        public TextView tvBookName;
        public TextView tvReadProcess;
        public TextView tvLastReadTime;
    }

    /**
     * 两个item数据交换结束后，把不需要再交换的item更新到数据库中
     * @param position
     * @param bookLists
     */
    public void updateBookPosition (int position,int databaseId,List<BookList> bookLists) {
        BookList bookList = new BookList();
        String bookpath = bookLists.get(position).getBookpath();
        String bookname = bookLists.get(position).getBookname();
        bookList.setBookpath(bookpath);
        bookList.setBookname(bookname);
        bookList.setBegin(bookLists.get(position).getBegin());
        bookList.setCharset(bookLists.get(position).getCharset());
        //开线程保存改动的数据到数据库
        //使用litepal数据库框架update时每次只能update一个id中的一条信息，如果相同则不更新。
        upDateBookToSqlite3(databaseId , bookList);
    }


    /**
     * 删除书本
     * @param deletePosition
     */
    public void removeItem(int deletePosition) {

        String bookpath = bilist.get(deletePosition).getBookpath();
        DataSupport.deleteAll(BookList.class, "bookpath = ?", bookpath);
        bilist.remove(deletePosition);
        // Log.d("删除的书本是", bookpath);

        notifyDataSetChanged();

    }

    public void setBookList(List<BookList> bookLists){
        this.bilist = bookLists;
        notifyDataSetChanged();
    }
    /**
     * Book打开后位置移动到第一位
     * @param openPosition
     */
    public void setItemToFirst(int openPosition) {

        List<BookList> bookLists1 = new ArrayList<>();
        bookLists1 = DataSupport.findAll(BookList.class);
        int tempId = bookLists1.get(0).getId();  //列表中第一项的id
        BookList temp = bookLists1.get(openPosition);      //被打开书的详细信息
        // Log.d("setitem adapter ",""+openPosition);
        if(openPosition!=0) {
            for (int i = openPosition; i > 0 ; i--) {
                List<BookList> bookListsList = new ArrayList<>();
                bookListsList = DataSupport.findAll(BookList.class);
                int dataBasesId = bookListsList.get(i).getId();

                Collections.swap(bookLists1, i, i - 1);
                updateBookPosition(i, dataBasesId, bookLists1);
            }

            bookLists1.set(0, temp);
            updateBookPosition(0, tempId, bookLists1);
            for (int j = 0 ;j<bookLists1.size();j++) {
                String bookpath = bookLists1.get(j).getBookpath();
                //  Log.d("移动到第一位",bookpath);
            }
        }
        notifyDataSetChanged();
    }

    public void nitifyDataRefresh() {
        notifyDataSetChanged();
    }

    public void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
        myAsyncTasks.add(asyncTask.execute());
    }

    /**
     * 数据库书本信息更新
     * @param databaseId  要更新的数据库的书本ID
     * @param bookList
     */
    public void upDateBookToSqlite3(final int databaseId,final BookList bookList) {

        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    bookList.update(databaseId);
                } catch (DataSupportException e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {

                } else {
                    Log.d("保存到数据库结果-->", "失败");
                }
            }
        });
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
