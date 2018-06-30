package com.ffcc66.sxyj.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.base.BaseActivity;
import com.ffcc66.sxyj.entity.User;
import com.ffcc66.sxyj.response.EntityResponse;
import com.ffcc66.sxyj.util.GsonUtil;
import com.ffcc66.sxyj.util.LoadingDialogUtils;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class EditInformationActivity extends BaseActivity implements View.OnClickListener{

    CircleImageView circleimgHead;
    @BindView(R.id.btnLogout)
    Button btnLogout;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etSign)
    EditText etSign;
    @BindView(R.id.radioGroupSex)
    RadioGroup radioGroupSex;
    @BindView(R.id.rbMan)
    RadioButton rbMan;
    @BindView(R.id.rbWoman)
    RadioButton rbWoman;
    @BindView(R.id.tvComplete)
    TextView tvComplete;
    SharedPreferences sharedPreferences;

    private User user = new User();


    @Override
    public int getLayoutRes() {
        return R.layout.activity_edit_information;
    }

    @Override
    protected void initData() {
        sharedPreferences = getSharedPreferences("userdata",Context.MODE_PRIVATE);
        user.setId(sharedPreferences.getInt("id",0));
        user.setUsername(sharedPreferences.getString("username",""));
        user.setPassword(sharedPreferences.getString("password",""));
        user.setPhone(sharedPreferences.getString("phone",""));
        user.setEmail(sharedPreferences.getString("email",""));
        user.setSex(sharedPreferences.getInt("sex",0));
        user.setSign(sharedPreferences.getString("sign",""));

        etUsername.setText(user.getUsername());
        etSign.setText(user.getSign());
        if (user.getSex() == 0) {
            radioGroupSex.check(rbWoman.getId());
        } else {
            radioGroupSex.check(rbMan.getId());
        }
    }

    @Override
    protected void initListener() {
        btnLogout.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        radioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == rbMan.getId()) {
                    user.setSex(1);
                } else {
                    user.setSex(0);
                }
            }
        });
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
            case R.id.tvComplete:
                user.setSign(etSign.getText().toString());
                new AlertDialog.Builder(EditInformationActivity.this)
                        .setTitle("确认修改")
                        .setMessage("是否保存信息？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateUserinfo(user);
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                break;
        }
    }

    private void updateUserinfo(final User user) {

        final String userinfo_string = GsonUtil.GsonString(user);
        final Dialog dialog = LoadingDialogUtils.createLoadingDialog(EditInformationActivity.this, "请稍等");
        OkHttpUtils.post()
                .url("http://192.168.137.1:8080/SXYJApi/UserService/updateUserinfo")
                .addParams("userinfo_string",userinfo_string)
                .build()
                .connTimeOut(5000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        Toast.makeText(EditInformationActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        EntityResponse<String> entityResponse = GsonUtil.GsonToBean(response,new TypeToken<EntityResponse<String>>(){}.getType());
                        String result = (String) entityResponse.getObject();
                        if (result.equals("OK")) {
                            Toast.makeText(EditInformationActivity.this,"修改成功",Toast.LENGTH_SHORT).show();

                            sharedPreferences.edit().putString("sign",user.getSign());
                            sharedPreferences.edit().putInt("sex",user.getSex());
                            sharedPreferences.edit().commit();

                        } else {
                            Toast.makeText(EditInformationActivity.this,"修改失败，请重试",Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }
}
