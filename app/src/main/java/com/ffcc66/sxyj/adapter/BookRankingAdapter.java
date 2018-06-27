package com.ffcc66.sxyj.adapter;

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
import com.ffcc66.sxyj.response.entity.ResponseBook;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 图书排行列表页adapter
 */
public class BookRankingAdapter extends ArrayAdapter {

    private int resourceId;
    private List<ResponseBook> responseBookList = new ArrayList<>();
    private String numtype = "收藏数：";
    public BookRankingAdapter(Context context, int viewResouceId, List<ResponseBook> books) {
        super(context,viewResouceId,books);
        this.resourceId = viewResouceId;
        this.responseBookList = books;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivCover = view.findViewById(R.id.ivCover);
            viewHolder.tvBookName = view.findViewById(R.id.tvBookName);
            viewHolder.tvWriter = view.findViewById(R.id.tvWriter);
            viewHolder.tvNumType = view.findViewById(R.id.tvNumType);
            viewHolder.tvNum = view.findViewById(R.id.tvNum);
            viewHolder.tvIntroduction = view.findViewById(R.id.tvIntroduction);
            viewHolder.tvRankingNum = view.findViewById(R.id.tvRankingNum);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        Picasso.get().load(responseBookList.get(position).getCover_img()).placeholder(R.drawable.test).resize(60,80).centerCrop().into(viewHolder.ivCover);
        viewHolder.tvBookName.setText(responseBookList.get(position).getName().trim());
        viewHolder.tvWriter.setText(responseBookList.get(position).getAuthor());
        viewHolder.setNumAndType(numtype, responseBookList.get(position));
        viewHolder.tvIntroduction.setText(responseBookList.get(position).getIntroduction());
        viewHolder.tvRankingNum.setText(""+(position+1));

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
        public TextView tvNumType;
        public TextView tvNum;
        public TextView tvIntroduction;
        public TextView tvRankingNum;

        public void setNumAndType(String type, ResponseBook responseBook) {
            switch (type) {
                case "收藏数：":
                    tvNumType.setText(type);
                    tvNum.setText(""+responseBook.getCollectionnum());
                    break;
                case "搜索数：":
                    tvNumType.setText(type);
                    tvNum.setText(""+responseBook.getCollectionnum());
                    break;
                case "评论数：":
                    tvNumType.setText(type);
                    tvNum.setText(""+responseBook.getCollectionnum());
                    break;
                case "点击数：":
                    tvNumType.setText(type);
                    tvNum.setText(""+responseBook.getLooknum());
                    break;
                case "字数：":
                    tvNumType.setText(type);
                    tvNum.setText(""+responseBook.getWordcount());
                    break;
            }
        }
    }

    public void setNumType(String numtype) {
        this.numtype = numtype;
    }
}
