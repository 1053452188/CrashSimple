package com.ayit.crashlibrary;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by lny on 2018/5/14.
 */

public class Crash extends HashMap<String, Object> {


    public static interface Field {
        String deviceSerial = "deviceSerial";
        String title = "title";
        String content = "content";
        String createTime = "createTime";
        String versionName = "versionName";
        String versionCode = "versionCode";
    }

    private String deviceSerial;
    private String title;
    private String content;
    private String createTime;
    private String versionName;
    private int versionCode;

    public Crash() {
    }

    public Crash(String deviceSerial, String title, String content, String createTime, String versionName, int versionCode) {
        this.deviceSerial = deviceSerial;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.versionName = versionName;
        this.versionCode = versionCode;
        put(Field.deviceSerial, deviceSerial);
        put(Field.title, title);
        put(Field.content, content);
        put(Field.createTime, createTime);
        put(Field.versionName, versionName);
        put(Field.versionCode, versionCode);
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        put(Field.deviceSerial,deviceSerial);
        this.deviceSerial = deviceSerial;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        put(Field.title,title);
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        put(Field.content,content);
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        put(Field.createTime,createTime);
        this.createTime = createTime;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        put(Field.versionName,versionName);
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        put(Field.versionCode,versionCode);
        this.versionCode = versionCode;
    }
}
