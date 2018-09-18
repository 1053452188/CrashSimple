package com.ayit.crashsimple;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ayit.crashlibrary.CrashBaseService;

public class CrashService extends CrashBaseService {
    private final String TAG = "CrashHandler";
    public CrashService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d(TAG,"CrashService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(TAG,"CrashService onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
