package com.wiitel.tvhelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zhuchuntao on 16-12-8.
 */

public class ShutDownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      System.out.println("ShutDownReceiver onReceive");
    }

    public ShutDownReceiver() {
        super();
    }
}
