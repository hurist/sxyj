package com.ffcc66.sxyj.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 登录Activity
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

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
    private User user;

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
                    SharedPreferences.Editor editor = getSharedPreferences("userdata", Context.MODE_PRIVATE).edit();
                    editor.putInt("id",user.getId());
                    editor.putString("username",user.getUsername());
                    editor.putString("password",user.getPassword());
                    editor.putString("email",user.getEmail());
                    editor.putString("phone",user.getPhone());
                    editor.putString("headimg",user.getHeadimg());
                    editor.putInt("sex",user.getSex());
                    editor.commit();
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("userdata",Context.MODE_PRIVATE);

        if (sharedPreferences.getString("username",null) != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
        initListener();
    }


    protected void initData() {
        List<BookList> books = DataSupport.findAll(BookList.class);
        etUsername.setHintTextColor(Color.WHITE);
        etPassword.setHintTextColor(Color.WHITE);
    }

    protected void initListener() {
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
    }

    private void login() {
        logindialog = LoadingDialogUtils.createLoadingDialog(LoginActivity.this,"正在登陆");

        String url;

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        final HashMap<String,String> loginparams = new HashMap<String,String>();

        loginparams.put("password",password);

        if (checkEmaile(username)) {
            url = "http://192.168.137.1:8080/SXYJApi/UserService/loginByEmail";
            loginparams.put("email",username);
        }else {
            loginparams.put("username",username);
            url = "http://192.168.137.1:8080/SXYJApi/UserService/loginByUsername";
        }

        OkHttpUtils.post()
                .url(url)
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

                        Message msg = new Message();
                        EntityResponse<User> entityResponse = GsonUtil.GsonToBean(response,new TypeToken<EntityResponse<User>>(){}.getType());

                        if (entityResponse.getCode().equals("ok")) {

                            user = (User) entityResponse.getObject();
                            if (user == null){
                                msg.what = 0;
                                mHandler.sendMessage(msg);
                                return;
                            }
                            msg.what = 1;
                            msg.obj = user.getUsername();
                            mHandler.sendMessage(msg);
                            return;
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnLogin:
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                login();
                break;
            case R.id.tvRegister:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;

        }
    }

    public boolean checkEmaile(String emaile){
        /**
         *   ^匹配输入字符串的开始位置
         *   $结束的位置
         *   \转义字符 eg:\. 匹配一个. 字符  不是任意字符 ，转义之后让他失去原有的功能
         *   \t制表符
         *   \n换行符
         *   \\w匹配字符串  eg:\w不能匹配 因为转义了
         *   \w匹配包括字母数字下划线的任何单词字符
         *   \s包括空格制表符换行符
         *   *匹配前面的子表达式任意次
         *   .小数点可以匹配任意字符
         *   +表达式至少出现一次
         *   ?表达式0次或者1次
         *   {10}重复10次
         *   {1,3}至少1-3次
         *   {0,5}最多5次
         *   {0,}至少0次 不出现或者出现任意次都可以 可以用*号代替
         *   {1,}至少1次  一般用+来代替
         *   []自定义集合     eg:[abcd]  abcd集合里任意字符
         *   [^abc]取非 除abc以外的任意字符
         *   |  将两个匹配条件进行逻辑“或”（Or）运算
         *   [1-9] 1到9 省略123456789
         *    邮箱匹配 eg: ^[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\.){1,3}[a-zA-z\-]{1,}$
         *
         */
        String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        //正则表达式的模式 编译正则表达式
        Pattern p = Pattern.compile(RULE_EMAIL);
        //正则表达式的匹配器
        Matcher m = p.matcher(emaile);
        //进行正则匹配
        return m.matches();
    }
}
