package com.ayit.crashlibrary;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CrashBaseService extends Service {
    public CrashBaseService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ContextWrapperUtil.add(this);
    }

    @Override
    public void onDestroy() {
        ContextWrapperUtil.remove(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
