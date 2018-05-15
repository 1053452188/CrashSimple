package com.ayit.crashsimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ayit.crashlibrary.CrashBaseActivity;

public class MainActivity extends CrashBaseActivity {

    public static final String TAG = "CrashHandler";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"MainActivity onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"MainActivity onDestroy");
    }

    public void mainThread(View view){
        int i = 5/0;
    }

    public void subThread(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str = null;
                str.toString();
            }
        }).start();
    }
}
