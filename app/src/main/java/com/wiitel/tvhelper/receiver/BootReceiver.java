package com.wiitel.tvhelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.wiitel.tvhelper.data.AppFlow;
import com.wiitel.tvhelper.db.DBManager;

import java.util.List;

/**
 * Created by zhuchuntao on 16-12-12.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("BootReceiver onReceive");
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> pinfos = pm.getInstalledPackages
                (PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);

        for (PackageInfo info : pinfos) {
            //请求每个程序包对应的androidManifest.xml里面的权限
            String[] premissions = info.requestedPermissions;

            if (premissions != null && premissions.length > 0) {
                //找出需要网络服务的应用程序
                for (String premission : premissions) {
                    if ("android.permission.INTERNET".equals(premission)) {

                        int uId = info.applicationInfo.uid;
                        AppFlow flow = new AppFlow();
                        flow.setAppid(uId);

                        //如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
                        long rx = TrafficStats.getUidRxBytes(uId);
                        //如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
                        long tx = TrafficStats.getUidTxBytes(uId);
                        //设置开机后的流量
                        flow.setRx(rx);
                        flow.setTx(tx);
                      System.out.println("uId==="+uId+"rx====="+rx+";tx=="+tx);
                        break;
                    }
                }
            }
        }
    }
}
