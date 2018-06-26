package com.ffcc66.sxyj.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ffcc66.sxyj.MainActivity;
import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.entity.User;
import com.ffcc66.sxyj.response.EntityResponse;
import com.ffcc66.sxyj.response.entity.ResponseAcountInfo;
import com.ffcc66.sxyj.util.GsonUtil;
import com.ffcc66.sxyj.util.LoadingDialogUtils;
import com.ffcc66.sxyj.util.StringUtils;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 注册activity
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    @BindView(R.id.btnInfoConfirm)
    Button btnInfoConfirm;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirm)
    EditText etConfirm;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etEmail)
    EditText etEmail;


    private String username,password,confirmpassword,phone,email;
    private String TAG = "222222222";
    private EditText[] editTexts = {etPassword,etConfirm,etPhone,etUsername,etPhone};
    static boolean acountIsExist = true;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {      //接收其他子线程的消息
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:

                    break;
                case 1:

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initData();
        initListener();
    }

    private void initListener() {

        btnInfoConfirm.setOnClickListener(this);
        etUsername.setOnFocusChangeListener(this);
        etPassword.setOnFocusChangeListener(this);
        etConfirm.setOnFocusChangeListener(this);
        etPhone.setOnFocusChangeListener(this);
        etEmail.setOnFocusChangeListener(this);

    }

    private void initData() {
        EditText[] tempeditText = {etPassword,etConfirm,etPhone,etUsername,etEmail};
        editTexts = tempeditText;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnInfoConfirm:
                confirmInfoMatino();
                break;

        }
    }

    private Boolean confirmInfoMatino() {
        getString();
        if (checkEmpty()) {
            if (checkForm()) {
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setPhone(phone);
                String userinnfo_string = GsonUtil.GsonString(user);
                checkEmailAndUsername(userinnfo_string);
            }

        } else {
            return false;
        }

        return false;
    }




    private void getString() {
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();
        confirmpassword = etConfirm.getText().toString();
        phone = etPhone.getText().toString();
        email = etEmail.getText().toString();

    }

    private boolean checkForm() {

        if (username.length() < 6){
            etUsername.requestFocus();
            etUsername.setError("用户名长度小于6位");
            return false;
        }
        else if (!StringUtils.checkEmaile(email)) {
            etEmail.setError("邮箱格式不正确");
            etEmail.requestFocus();
            return false;
        } else if(!StringUtils.isChinaPhoneLegal(phone)) {
            etPhone.setError("电话格式不正确");
            etPhone.requestFocus();
            return false;
        } else if(!password.equals(confirmpassword)) {
            etConfirm.setError("两次密码输入不一致");
            etConfirm.requestFocus();
            return false;
        } else if (password.trim().length() < 6 ) {
            etPassword.setError("密码长度小于6位(空格将无效)");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkEmpty() {

        if (username.trim().length() == 0) {
            etUsername.setError("用户名不能为空");
            etUsername.requestFocus();
            return false;
        } else if(password.trim().length() == 0 ) {
            etPassword.setError("密码不能为空");
            etPassword.requestFocus();
            return false;
        } else if(confirmpassword.trim().length() == 0 ) {
            etConfirm.setError("确认密码不能为空");
            etConfirm.requestFocus();
            return false;
        } else if(phone.trim().length() == 0 ) {
            etPhone.setError("密码不能为空");
            etPhone.requestFocus();
            return false;
        } else if(email.trim().length() == 0) {
            etEmail.setError("邮箱不能为空");
            etEmail.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.etUsername: if (b) { cancelError(view.getId()); } break;
            case R.id.etEmail: if (b) { cancelError(view.getId()); }break;
            case R.id.etConfirm: if (b) { cancelError(view.getId()); }break;
            case R.id.etPassword: if (b) { cancelError(view.getId()); }break;
            case R.id.etPhone: if (b) { cancelError(view.getId()); }break;
        }
    }

    private void cancelError(int id) {
        for (int i=0; i<5; i++) {
            if (editTexts[i].getId() != id){
                editTexts[i].setError(null,null);
            }
        }
    }

    private void checkEmailAndUsername(final String userinfo_string) {

        OkHttpUtils.get()
                .url("http://192.168.137.1:8080/SXYJApi/UserService/checkAcountIsExist")
                .addParams("userinfo_string",userinfo_string)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: ",e );
                        Toast.makeText(RegisterActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        EntityResponse<ResponseAcountInfo> entityResponse = GsonUtil.GsonToBean(response,new TypeToken<EntityResponse<ResponseAcountInfo>>(){}.getType());
                        ResponseAcountInfo responseAcountInfo = new ResponseAcountInfo();
                        if (entityResponse.getCode().equals("ok")) {

                            responseAcountInfo = (ResponseAcountInfo) entityResponse.getObject();
                            if (responseAcountInfo == null){
                                Toast.makeText(RegisterActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                            } else {
                                if(responseAcountInfo.isUsernameIsExist()) {
                                    etUsername.requestFocus();
                                    etUsername.setError("用户名已存在");
                                } else if (responseAcountInfo.isEmailIsExist()) {
                                    etEmail.requestFocus();
                                    etEmail.setError("邮箱已存在");
                                } else {
                                    registerUserInfo(userinfo_string);
                                }
                            }

                        }
                    }
                });

    }


    private void registerUserInfo(String userinfo_string) {

        OkHttpUtils.post()
                .url("http://192.168.137.1:8080/SXYJApi/UserService/registerUserinfo")
                .addParams("userinfo_string",userinfo_string)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: ",e );
                        Toast.makeText(RegisterActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        EntityResponse<String> entityResponse = GsonUtil.GsonToBean(response,new TypeToken<EntityResponse<String>>(){}.getType());
                        String result;
                        if (entityResponse.getCode().equals("ok")) {

                            result = (String) entityResponse.getObject();
                            if (result == null){
                                Toast.makeText(RegisterActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                            } else {
                                if (result.equals("OK")) {
                                    new AlertDialog.Builder(RegisterActivity.this).setMessage("注册成功").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                            if (i == KeyEvent.KEYCODE_BACK
                                                    && keyEvent.getRepeatCount() == 0) {
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                            return false;
                                        }
                                    }).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this,"注册失败！",Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                });

    }

}
