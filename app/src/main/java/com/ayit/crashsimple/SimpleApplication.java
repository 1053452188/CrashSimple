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

        //重启
//        CrashHandler.getInstance().initRestart(this,true,1000,MainActivity.class);
        //不重启
//        CrashHandler.getInstance().initKillApp(this,true);
        //无感知crash
        CrashHandler.getInstance().initUnCrash(this,true);


        CrashHandler.getInstance().setOnCrashCallBack(new CrashHandler.OnCrashCallBack() {
            @Override
            public void onCrash(Crash crash) {
                Log.d(TAG,"call_back : "+crash.toString());
            }
        });
    }
}
