package com.ffcc66.sxyj.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.entity.SearchHistory;

import java.util.List;

public class SearchHistoryAdapter extends ArrayAdapter {

    private int resourceId;
    private List<SearchHistory> history;
    public SearchHistoryAdapter(Context context, int viewResouceId, List<SearchHistory> history) {
        super(context,viewResouceId,history);
        this.resourceId = viewResouceId;
        this.history = history;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvSeachText = view.findViewById(R.id.tvSeachText);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvSeachText.setText(history.get(position).getSearchword());

        return view;
    }

    class ViewHolder {
        public TextView tvSeachText;
    }


}
