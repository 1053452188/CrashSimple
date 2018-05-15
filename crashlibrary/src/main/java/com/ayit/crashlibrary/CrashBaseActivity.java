package com.ayit.crashlibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by lny on 2018/5/12.
 */

public class CrashBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextWrapperUtil.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ContextWrapperUtil.remove(this);
    }
}
