package com.ffcc66.sxyj.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.entity.Book;
import com.ffcc66.sxyj.response.entity.ResponseBook;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.zhy.http.okhttp.log.LoggerInterceptor.TAG;

/**
 * 书城首页图书列表页适配器adapter
 */
public class BookStoreAdapter extends ArrayAdapter {

    private int resourceId;
    public BookStoreAdapter(Context context, int viewResouceId, List<ResponseBook> bookList) {
        super(context,viewResouceId,bookList);
        this.resourceId = viewResouceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ResponseBook book = (ResponseBook) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivCover = view.findViewById(R.id.ivCover);
            viewHolder.tvBookName = view.findViewById(R.id.tvBookName);
            viewHolder.tvWriter = view.findViewById(R.id.tvWriter);
            viewHolder.tvIntroduction = view.findViewById(R.id.tvIntroduction);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        Picasso.get().load(book.getCover_img()).placeholder(R.drawable.test).resize(60,80).centerCrop().into(viewHolder.ivCover);
        viewHolder.tvBookName.setText(book.getName());
        viewHolder.tvWriter.setText(book.getAuthor());
        viewHolder.tvIntroduction.setText(book.getIntroduction());

        return view;
    }

    class ViewHolder {
        public ImageView ivCover;
        public TextView tvBookName;
        public TextView tvWriter;
        public TextView tvIntroduction;
    }
}
