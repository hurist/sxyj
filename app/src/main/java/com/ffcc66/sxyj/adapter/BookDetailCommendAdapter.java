package com.ffcc66.sxyj.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

/**
 * 图书详情页书评adapter
 */
public class BookDetailCommendAdapter extends ArrayAdapter {

    private int resourceId;
    public BookDetailCommendAdapter(Context context, int viewResouceId, List<TempCommend> tempCommendList) {
        super(context,viewResouceId,tempCommendList);
        this.resourceId = viewResouceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TempCommend commend = (TempCommend) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivHeadImg = view.findViewById(R.id.ivHeadImg);
            viewHolder.tvUsername = view.findViewById(R.id.tvUsername);
            viewHolder.tvCommend = view.findViewById(R.id.tvCommend);
            viewHolder.tvDate = view.findViewById(R.id.tvDate);
            viewHolder.tvReplayNum = view.findViewById(R.id.tvReplyNum);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.ivHeadImg.setImageResource(commend.getHeadimg());
        viewHolder.tvUsername.setText(commend.getUsername());
        viewHolder.tvCommend.setText(commend.getCommend());
        viewHolder.tvDate.setText(commend.getDate());
        viewHolder.tvReplayNum.setText(commend.getNum());

        return view;
    }

    class ViewHolder {
        public ImageView ivHeadImg;
        public TextView tvUsername;
        public TextView tvCommend;
        public TextView tvDate;
        public TextView tvReplayNum;
    }
}
