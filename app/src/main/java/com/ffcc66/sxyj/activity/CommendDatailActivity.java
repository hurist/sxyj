package com.ffcc66.sxyj.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.entity.Commend;
import com.ffcc66.sxyj.entity.User;
import com.ffcc66.sxyj.response.EntityResponse;
import com.ffcc66.sxyj.util.GsonUtil;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class CommendDatailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvCommend)
    TextView tvCommend;
    @BindView(R.id.tvDisLikeNum)
    TextView tvDisLikeNum;
    @BindView(R.id.tvLikeNum)
    TextView tvLikeNum;
    @BindView(R.id.rlLike)
    RelativeLayout rlLike;
    @BindView(R.id.rlDislike)
    RelativeLayout rlDislike;
    @BindView(R.id.ivLike)
    ImageView ivLike;
    @BindView(R.id.ivDislike)
    ImageView ivDislike;

    private Commend commend;
    private int userid;
    private int likenum;
    private int dislikenum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commend);
        ButterKnife.bind(this);
        initDate();
        initListener();
    }

    private void initListener() {
        rlLike.setOnClickListener(this);
        rlDislike.setOnClickListener(this);
    }



    private void initDate() {

        userid = getSharedPreferences("userdata", Context.MODE_PRIVATE).getInt("id",0);
        commend = (Commend) getIntent().getSerializableExtra("commend");
        if (commend != null) {
            tvUsername.setText(commend.getUsername());
            tvDate.setText(commend.getAdddate());
            tvTitle.setText(commend.getTitle());
            tvCommend.setText(commend.getCommend());
        }

        getLikeNum();
        checkLike();

    }

    private void getLikeNum() {
        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/BookService/getLikeNum")
                .addParams("commendid",""+commend.getId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("", "onError: ",e );
                        Toast.makeText(CommendDatailActivity.this,"获取点赞数量失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        EntityResponse<Integer[]> entityResponse = GsonUtil.GsonToBean(response,new TypeToken<EntityResponse<Integer[]>>(){}.getType());
                        Integer[] islike = entityResponse.getObject();
                        if (islike != null) {

                            likenum = islike[0].intValue();
                            dislikenum = islike[1].intValue();

                            tvLikeNum.setText(likenum+"");
                            tvDisLikeNum.setText(dislikenum+"");
                        }
                    }
                });
    }

    public void addLikeNum(int isLike) {
        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/BookService/addLike")
                .addParams("userid",""+userid)
                .addParams("commendid",""+commend.getId())
                .addParams("islike",isLike+"")
                .build()
                .connTimeOut(5000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("", "onError: ",e );
                        Toast.makeText(CommendDatailActivity.this,"点击失败",Toast.LENGTH_SHORT).show();
                        ivLike.setImageResource(R.mipmap.like_unselect);
                        ivDislike.setImageResource(R.mipmap.dislike_unselect);
                        rlLike.setClickable(true);
                        rlDislike.setClickable(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        EntityResponse<String> entityResponse = GsonUtil.GsonToBean(response,new TypeToken<EntityResponse<String>>(){}.getType());
                        String result = entityResponse.getObject();
                        if (result != null) {
                            if (result.equals("Failure")) {
                                ivLike.setImageResource(R.mipmap.like_unselect);
                                ivDislike.setImageResource(R.mipmap.dislike_unselect);
                                rlLike.setClickable(true);
                                rlDislike.setClickable(true);
                                Toast.makeText(CommendDatailActivity.this, "点击失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void checkLike() {
        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/BookService/checkLike")
                .addParams("userid",""+userid)
                .addParams("commendid",""+commend.getId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("", "onError: ",e );
                        Toast.makeText(CommendDatailActivity.this,"获取点赞状态失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        EntityResponse<Integer> entityResponse = GsonUtil.GsonToBean(response,new TypeToken<EntityResponse<Integer>>(){}.getType());
                        Integer islike = entityResponse.getObject();
                        if (islike != null) {
                            if (islike.intValue() == 1) {
                                rlLike.setClickable(false);
                                rlDislike.setClickable(false);
                                ivLike.setImageResource(R.mipmap.like_selected);
                            } else {
                                rlLike.setClickable(false);
                                rlDislike.setClickable(false);
                                ivDislike.setImageResource(R.mipmap.dislike_selected);
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlLike:
                rlLike.setClickable(false);
                rlDislike.setClickable(false);
                ivLike.setImageResource(R.mipmap.like_selected);
                tvLikeNum.setText((likenum+1)+"");
                addLikeNum(1);
                break;
            case R.id.rlDislike:
                rlLike.setClickable(false);
                rlDislike.setClickable(false);
                ivDislike.setImageResource(R.mipmap.dislike_selected);
                tvDisLikeNum.setText((dislikenum+1)+"");
                addLikeNum(0);
                break;
        }
    }
}
