package com.ayit.crashlibrary;

import android.app.Activity;
import android.app.Service;
import android.content.ContextWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lny on 2018/5/12.
 */

public class ContextWrapperUtil {
    private static final List<ContextWrapper> list = new ArrayList<>();

    public static void add(ContextWrapper contextWrapper) {
        list.add(contextWrapper);
    }

    public static void remove(ContextWrapper contextWrapper) {
        list.remove(contextWrapper);
    }

    public static void removeAll() {
        if (!list.isEmpty()) {
            for (ContextWrapper context : list) {
                if (context instanceof Service){
                    ((Service) context).stopSelf();
                }else if (context instanceof Activity){
                    ((Activity) context).finish();
                }
            }
        }
        list.clear();
    }
}
