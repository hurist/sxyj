package com.ffcc66.sxyj.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.base.BaseActivity;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditInformationActivity extends BaseActivity implements View.OnClickListener{

    CircleImageView circleimgHead;
    @BindView(R.id.btnLogout)
    Button btnLogout;


    @Override
    public int getLayoutRes() {
        return R.layout.activity_edit_information;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogout:
                SharedPreferences.Editor editor = getSharedPreferences("userdata", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(EditInformationActivity.this, LoginActivity.class));
                break;
        }
    }
}
