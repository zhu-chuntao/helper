package com.wiitel.tvhelper;

import android.app.Application;

import com.android.volley.manager.RequestManager;

/**
 * Created by zhuchuntao on 16/12/13.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RequestManager.getInstance().init(this);
    }
}
