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
import com.ffcc66.sxyj.entity.Commend;
import com.ffcc66.sxyj.entity.TempCommend;

import java.util.ArrayList;
import java.util.List;

/**
 * 图书详情页书评adapter
 */
public class BookDetailCommendAdapter extends ArrayAdapter {

    private int resourceId;
    private List<Commend> commendList = new ArrayList<>();
    public BookDetailCommendAdapter(Context context, int viewResouceId, List<Commend> commends) {
        super(context,viewResouceId,commends);
        this.resourceId = viewResouceId;
        this.commendList = commends;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tvTitle);
            viewHolder.tvUsername = view.findViewById(R.id.tvUsername);
            viewHolder.tvCommend = view.findViewById(R.id.tvCommend);
            viewHolder.tvDate = view.findViewById(R.id.tvDate);
            viewHolder.tvReplayNum = view.findViewById(R.id.tvReplyNum);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvTitle.setText(commendList.get(position).getTitle());
        viewHolder.tvUsername.setText(commendList.get(position).getUsername());
        viewHolder.tvCommend.setText(commendList.get(position).getCommend());
        viewHolder.tvDate.setText(commendList.get(position).getAdddate());
        viewHolder.tvReplayNum.setText("");

        return view;
    }

    class ViewHolder {
        public TextView tvTitle;
        public TextView tvUsername;
        public TextView tvCommend;
        public TextView tvDate;
        public TextView tvReplayNum;
    }
}
