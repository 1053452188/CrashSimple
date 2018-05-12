package com.ayit.crashlibrary;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lny on 2018/5/12.
 */

public class ActivityUtil {
    private static final List<Activity> list = new ArrayList<>();

    public static void add(Activity activity) {
        list.add(activity);
    }

    public static void remove(Activity activity) {
        list.remove(activity);
    }

    public static void removeAll() {
        if (!list.isEmpty()) {
            for (Activity activity : list) {
                if (activity != null)
                    activity.finish();
            }
        }
        list.clear();

        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
