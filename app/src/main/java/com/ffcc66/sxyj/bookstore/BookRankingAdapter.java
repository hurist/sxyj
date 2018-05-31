package com.ffcc66.sxyj.bookstore;

import android.content.Context;
import android.graphics.Color;
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
import com.ffcc66.sxyj.entity.TempCommend;

import java.util.List;

public class BookRankingAdapter extends ArrayAdapter {

    private int resourceId;
    public BookRankingAdapter(Context context, int viewResouceId, List<Book> books) {
        super(context,viewResouceId,books);
        this.resourceId = viewResouceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("aaaaaaaaa", "getView: "+position);
        Book book = (Book) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivCover = view.findViewById(R.id.ivCover);
            viewHolder.tvBookName = view.findViewById(R.id.tvBookName);
            viewHolder.tvWriter = view.findViewById(R.id.tvWriter);
            viewHolder.tvCollectionNum = view.findViewById(R.id.tvCollectionNum);
            viewHolder.tvIntroduction = view.findViewById(R.id.tvIntroduction);
            viewHolder.tvRankingNum = view.findViewById(R.id.tvRankingNum);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.ivCover.setImageResource(book.getCover());
        viewHolder.tvBookName.setText(book.getBookname());
        viewHolder.tvWriter.setText(book.getWriter());
        viewHolder.tvCollectionNum.setText(book.getCollectionNum());
        viewHolder.tvIntroduction.setText(book.getIntroduction());
        viewHolder.tvRankingNum.setText(book.getRankingNum());

        switch (position) {
            default:
                viewHolder.tvRankingNum.setTextColor(Color.rgb(204,204,204));
                break;
            case 0:
                viewHolder.tvRankingNum.setTextColor(Color.rgb(255,0,84));
                break;
            case 1:
                viewHolder.tvRankingNum.setTextColor(Color.rgb(255,153,0));
                break;
            case 2:
                viewHolder.tvRankingNum.setTextColor(Color.rgb(255,204,80));
                break;
        }

        return view;
    }

    class ViewHolder {
        public ImageView ivCover;
        public TextView tvBookName;
        public TextView tvWriter;
        public TextView tvCollectionNum;
        public TextView tvIntroduction;
        public TextView tvRankingNum;
    }
}
