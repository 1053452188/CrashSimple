package com.ayit.crashlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 异常处理类
 * <p>
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-07-21  15:22
 */

public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    // CrashHandler 实例
    private static CrashHandler INSTANCE;

    // 系统默认的 UncaughtException 处理类
    private UncaughtExceptionHandler mDefaultHandler;

    // 程序的 Context 对象
    private Context mContext;

    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
//    @SuppressLint("SimpleDateFormat")
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    //是否是Debug模式
    private boolean mIsDebug;
    //是否重启APP
    private boolean mIsRestartApp; // 默认需要重启
    //重启APP时间
    private long mRestartTime;

    // 重启后跳转的Activity
    private Class mRestartActivity;

    // log地址
    private String mLogPath;

    // Toast 显示文案
    private String mTips;

    /**
     * 保证只有一个 CrashHandler 实例
     */
    private CrashHandler() {
    }

    /**
     * 获取 CrashHandler 实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }


    /**
     * @param context         上下文
     * @param isDebug         是否是Debug模式
     * @param isRestartApp    是否支持重启APP
     * @param restartTime     延迟重启时间
     * @param restartActivity 重启后跳转的 Activity，我们建议是 SplashActivity
     */
    public void init(Context context, boolean isDebug, boolean isRestartApp, long restartTime, Class<? extends Activity> restartActivity,String logPath) {
        mIsRestartApp = isRestartApp;
        mRestartTime = restartTime;
        mRestartActivity = restartActivity;
        this.mLogPath = logPath;
        init(context, isDebug);
    }


    public void init(Context context, boolean isDebug) {
        mTips = "很抱歉，程序出现异常，即将退出...";
        mIsDebug = isDebug;
        mContext = context;
        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    /**
     * 当 UncaughtException 发生时会转入该函数来处理
     */
    @SuppressWarnings("WrongConstant")
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            if (mIsRestartApp) { // 如果需要重启
                Intent intent = new Intent(mContext.getApplicationContext(), mRestartActivity);
                AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                //重启应用，得使用PendingIntent
                PendingIntent restartIntent = PendingIntent.getActivity(
                        mContext.getApplicationContext(), 0, intent,
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Android6.0以上，包含6.0
                    mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); //解决Android6.0省电机制带来的不准时问题
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //Android4.4到Android6.0之间，包含4.4
                    mAlarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 解决set()在api19上不准时问题
                } else {
                    mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
                }
            }
            // 结束应用
            ActivityUtil.removeAll();
        }
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
     *
     * @param ex
     * @return true：如果处理了该异常信息；否则返回 false
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }

        // 使用 Toast 来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, getTips(ex), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();


        //  如果用户不赋予外部存储卡的写权限导致的崩溃，会造成循环崩溃
        if (mIsDebug) {
            // 收集设备参数信息
//            collectDeviceInfo(mContext);
            // 保存日志文件
            saveCrashInfo2File(ex);
        }

        if (onCrashCallBack!=null){
            Crash crash = new Crash();
            crash.setCreateTime(formatter.format(new Date()));
            try{
                PackageManager pm = mContext.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                        PackageManager.GET_ACTIVITIES);

                if (pi != null) {
                    String versionName = pi.versionName == null ? "null"
                            : pi.versionName;
                    int versionCode = pi.versionCode;
//                    infos.put("versionName", versionName);
//                    infos.put("versionCode", versionCode);
                    crash.setVersionName(versionName);
                    crash.setVersionCode(versionCode);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            crash.setDeviceSerial(Build.SERIAL);
            crash.setTitle(ex.getMessage());
            crash.setContent(throwable2Log(ex,false));
            onCrashCallBack.onCrash(crash);
        }

        return true;
    }

    private String getTips(Throwable ex) {
        if (ex instanceof SecurityException) {
            if (ex.getMessage().contains("android.permission.CAMERA")) {
                mTips = "请授予应用相机权限，程序出现异常，即将退出.";
            } else if (ex.getMessage().contains("android.permission.RECORD_AUDIO")) {
                mTips = "请授予应用麦克风权限，程序出现异常，即将退出。";
            } else if (ex.getMessage().contains("android.permission.WRITE_EXTERNAL_STORAGE")) {
                mTips = "请授予应用存储权限，程序出现异常，即将退出。";
            } else if (ex.getMessage().contains("android.permission.READ_PHONE_STATE")) {
                mTips = "请授予应用电话权限，程序出现异常，即将退出。";
            } else if (ex.getMessage().contains("android.permission.ACCESS_COARSE_LOCATION") || ex.getMessage().contains("android.permission.ACCESS_FINE_LOCATION")) {
                mTips = "请授予应用位置信息权，很抱歉，程序出现异常，即将退出。";
            } else {
                mTips = "很抱歉，程序出现异常，即将退出，请检查应用权限设置。";
            }
        }
        return mTips;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);

            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中 *
     *
     * @param throwable
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private void saveCrashInfo2File(Throwable throwable) {
       if (!TextUtils.isEmpty(mLogPath)){
           File logFile = new File(mLogPath);
           if (!logFile.getParentFile().exists()){
               logFile.getParentFile().mkdirs();
           }
           if (!logFile.exists()){
               try {
                   logFile.createNewFile();
               } catch (IOException e) {
                   e.printStackTrace();
                   logFile = null;
               }
           }
           if (logFile!=null){
               FileWriter fileWriter = null;
               try {
                   fileWriter = new FileWriter(logFile,true);
                   fileWriter.write(throwable2Log(throwable,true));
                   fileWriter.flush();
               } catch (IOException e) {
                   e.printStackTrace();
               }finally {
                   if (fileWriter!=null){
                       try {
                           fileWriter.close();
                       } catch (IOException e) {
                           e.printStackTrace();
                           fileWriter = null;
                       }
                   }
               }
           }
       }
    }

    /**
     * 生成log
     * @param throwable
     * @return
     */
    public static String throwable2Log(Throwable throwable,boolean localLog){
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        StringBuilder builder = new StringBuilder();
        if (localLog){
            builder.append("\r\n");
            builder.append("\r\n");
            builder.append("本次崩溃日志:"+formatter.format(new Date()));
            builder.append("\r\n");
            builder.append(throwable.getClass().getName()+":"+throwable.getMessage());
            builder.append("\r\n");
            if (stackTrace!=null){
                for (StackTraceElement element : stackTrace){
                    builder.append("at ");
                    builder.append(element.getClassName()+".");
                    builder.append(element.getMethodName());
                    builder.append("(");
                    builder.append(element.getFileName());
                    builder.append(":");
                    builder.append(element.getLineNumber());
                    builder.append(")");
                    builder.append("\r\n");
                }
            }
        }else{
            builder.append("crash_time :"+formatter.format(new Date()));
            builder.append("\r\n");
            builder.append(throwable.getClass().getName()+":"+throwable.getMessage());
            builder.append("\r\n");
            if (stackTrace!=null){
                for (StackTraceElement element : stackTrace){
                    builder.append("at ");
                    builder.append(element.getClassName()+".");
                    builder.append(element.getMethodName());
                    builder.append("(");
                    builder.append(element.getFileName());
                    builder.append(":");
                    builder.append(element.getLineNumber());
                    builder.append(")");
                    builder.append("\r\n");
                }
            }
        }
        return builder.toString();
    }


    public interface  OnCrashCallBack{
        void onCrash(Crash crash);
    }
    private OnCrashCallBack onCrashCallBack;

    public OnCrashCallBack getOnCrashCallBack() {
        return onCrashCallBack;
    }

    public void setOnCrashCallBack(OnCrashCallBack onCrashCallBack) {
        this.onCrashCallBack = onCrashCallBack;
    }
}
