package com.ayit.crashsimple;

import android.app.Application;
import android.os.Environment;

import com.ayit.crashlibrary.CrashHandler;

/**
 * Created by lny on 2018/5/12.
 */

public class SimpleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this,true,true,1000,MainActivity.class, Environment.getExternalStorageDirectory().getAbsolutePath()+"/crash.log");
    }
}
