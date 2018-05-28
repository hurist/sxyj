package com.ffcc66.sxyj.bookcase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.entity.Book;

import java.util.List;

public class BookCaseAdapter extends ArrayAdapter {

    private int resourceId;
    public BookCaseAdapter(Context context, int viewResouceId, List<Book> bookList) {
        super(context,viewResouceId,bookList);
        this.resourceId = viewResouceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Book book = (Book) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivCover = view.findViewById(R.id.ivCover);
            viewHolder.tvBookName = view.findViewById(R.id.tvBookName);
            viewHolder.tvWriter = view.findViewById(R.id.tvWriter);
            viewHolder.tvReadProcess = view.findViewById(R.id.tvReadProcess);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.ivCover.setImageResource(book.getCover());
        viewHolder.tvBookName.setText(book.getBookname());
        viewHolder.tvWriter.setText(book.getWriter());
        viewHolder.tvReadProcess.setText("阅读进度"+book.getReadprocess());

        return view;
    }

    class ViewHolder {
        public ImageView ivCover;
        public TextView tvBookName;
        public TextView tvWriter;
        public TextView tvReadProcess;
    }
}
