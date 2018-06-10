package com.ffcc66.sxyj.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.activity.EditInformationActivity;
import com.ffcc66.sxyj.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 个人中心fragment
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "PersonalFragment";

    @BindView(R.id.circleimgHead)
    CircleImageView circleimgHead;
    @BindView(R.id.llMyMessage)
    LinearLayout llMyMessage;
    @BindView(R.id.llMyBookList)
    LinearLayout llMyBookList;
    @BindView(R.id.llReadHistory)
    LinearLayout llReadHistory;
    @BindView(R.id.llUserFeedback)
    LinearLayout llUserFeedback;
    @BindView(R.id.llCheckUpdate)
    LinearLayout llCheckUpdate;
    @BindView(R.id.llSetSkin)
    LinearLayout llSetSkin;
    @BindView(R.id.llSetting)
    LinearLayout llSetting;


    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_personal;
    }

    @Override
    protected void initData(View view) {

    }

    @Override
    protected void initListener() {
        circleimgHead.setOnClickListener(this);
    }

    @Override
    @OnClick({R.id.llMyMessage, R.id.llMyBookList, R.id.llReadHistory,
            R.id.llUserFeedback, R.id.llCheckUpdate, R.id.llSetSkin, R.id.llSetting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circleimgHead:
                getActivity().startActivity(new Intent(getContext(), EditInformationActivity.class));
                break;
        }
    }
}
