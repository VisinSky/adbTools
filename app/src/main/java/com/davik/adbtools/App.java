package com.davik.adbtools;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.davik.adbtools.adb.Adb;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("L");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Adb.init(this);
    }
}
