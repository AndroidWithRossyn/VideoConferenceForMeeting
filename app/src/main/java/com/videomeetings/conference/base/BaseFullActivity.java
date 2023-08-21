package com.videomeetings.conference.base;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseFullActivity extends AppCompatActivity {
    public PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getLayoutId());
        bindViews();
        prefManager = new PrefManager(this);
        initView(savedInstanceState);
    }

    private void bindViews() {
        try {
            ButterKnife.bind(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView(Bundle savedInstanceState);
}
