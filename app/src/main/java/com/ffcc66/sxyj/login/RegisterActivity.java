package com.ffcc66.sxyj.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ffcc66.sxyj.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Log.d("DPI", "onCreate: "+metrics);
    }
}
