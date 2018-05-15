package com.ayit.crashsimple;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.ayit.crashlibrary.Crash;
import com.ayit.crashlibrary.CrashHandler;

/**
 * Created by lny on 2018/5/12.
 */

public class SimpleApplication extends Application {
    public final String TAG = "CrashHandler";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");

        startService(new Intent(this,CrashService.class));

        CrashHandler.getInstance().init(this,true,false,1000,MainActivity.class, Environment.getExternalStorageDirectory().getAbsolutePath()+"/crash.log");

        CrashHandler.getInstance().setOnCrashCallBack(new CrashHandler.OnCrashCallBack() {
            @Override
            public void onCrash(Crash crash) {
                Log.d(TAG,"call_back : "+crash.toString());
            }
        });
    }
}
