package com.ffcc66.sxyj.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ffcc66.sxyj.MainActivity;
import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.base.BaseActivity;
import com.ffcc66.sxyj.entity.BookList;
import com.ffcc66.sxyj.entity.User;
import com.ffcc66.sxyj.response.EntityResponse;
import com.ffcc66.sxyj.util.GsonUtil;
import com.ffcc66.sxyj.util.LoadingDialogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 登录Activity
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvForgetPassword)
    TextView tvForgetPassword;
    @BindView(R.id.tvRegister)
    TextView tvRegister;

    Dialog logindialog;
    private static final String TAG = "LoginActivity";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {      //接收其他子线程的消息
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    LoadingDialogUtils.closeDialog(logindialog);
                    Toast.makeText(LoginActivity.this,"用户登录失败，账号或密码错误",Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    LoadingDialogUtils.closeDialog(logindialog);
                    Toast.makeText(LoginActivity.this,"用户："+msg.obj.toString()+"登录成功",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    break;
                case 2:
                    LoadingDialogUtils.closeDialog(logindialog);
                    Toast.makeText(LoginActivity.this,"网络错误，请检查网络设置",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        List<BookList> books = DataSupport.findAll(BookList.class);
        Log.d(TAG, "initData: "+books.size());
        for (BookList book: books) {
            Log.d(TAG, "initData: "+book.getBookname());
            Log.d(TAG, "initData: "+book.getBookpath());
            Log.d(TAG, "initData: "+book.getLastreadtime());
        }
        etUsername.setHintTextColor(Color.WHITE);
        etPassword.setHintTextColor(Color.WHITE);
    }

    @Override
    protected void initListener() {
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
    }

    private void login() {
        logindialog = LoadingDialogUtils.createLoadingDialog(LoginActivity.this,"正在登陆");

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        HashMap<String,String> loginparams = new HashMap<String,String>();
        loginparams.put("username",username);
        loginparams.put("password",password);

        OkHttpUtils.post()
                .url("http://192.168.137.1:8080/SXYJApi/UserService/loginByUsername")
                .params(loginparams)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Message msg = new Message();
                        msg.what = 2;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        Gson gson = new Gson();
                        EntityResponse<User> entityResponse = GsonUtil.GsonToBean(response,new TypeToken<EntityResponse<User>>(){}.getType());

                        if (entityResponse.getCode().equals("ok")) {
                            User user = (User) entityResponse.getObject();
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = user.getUsername();
                            mHandler.sendMessage(msg);
                        } else {

                            Message msg = new Message();
                            msg.what = 0;
                            mHandler.sendMessage(msg);

                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnLogin:
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
               // login();
                break;
            case R.id.tvRegister:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;

        }
    }
}
