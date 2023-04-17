package com.bichngoc.android_day9;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);//h can fai sd Application de stetho chay
    }
}
