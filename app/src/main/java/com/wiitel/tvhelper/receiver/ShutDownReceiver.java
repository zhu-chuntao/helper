package com.wiitel.tvhelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.AsyncTask;

import com.wiitel.tvhelper.R;
import com.wiitel.tvhelper.data.AppFlow;
import com.wiitel.tvhelper.data.AppInfo;
import com.wiitel.tvhelper.db.DBManager;
import com.wiitel.tvhelper.util.TimePatternUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuchuntao on 16-12-8.
 */

public class ShutDownReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("ShutDownReceiver onReceive");
        this.context = context;
        new UpdateFlowTask().execute("");
    }

    public ShutDownReceiver() {
        super();
    }

    public class UpdateFlowTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            //接收总量
            long nowRx = TrafficStats.getTotalRxBytes();

            //发送总量
            long nowTx = TrafficStats.getTotalTxBytes();


            AppFlow flowAll = new AppFlow();
            flowAll.setAppid(1);
            flowAll.setRx(nowRx);
            flowAll.setTx(nowTx);

            if(setAppFlowAll(flowAll)){
                //update
                DBManager.getInstance(context).updateFlow(flowAll);
            }else{
                //insert
                DBManager.getInstance(context).insertFlow(flowAll);
            }

            PackageManager pm = context.getPackageManager();
            List<PackageInfo> pinfos = pm.getInstalledPackages
                    (PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);

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

                            if(setAppFlowAll(flow)){
                                //update
                                DBManager.getInstance(context).updateFlow(flow);
                            }else{
                                //insert
                                DBManager.getInstance(context).insertFlow(flow);
                            }
                            break;
                        }
                    }
                }
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }
    //设置历史的流量
    private boolean setAppFlowAll(AppFlow flow) {
        DBManager dbManager = DBManager.getInstance(context);
        try {
            flow.setCurrentMonth(TimePatternUtil.getDate(System.currentTimeMillis(), TimePatternUtil.TimePattern.YM));
            AppFlow dbFlow = dbManager.queryFlowList(TimePatternUtil.getDate(System.currentTimeMillis(), TimePatternUtil.TimePattern.YM), flow.getAppid());
            if (dbFlow != null) {
                flow.setRxHistory(dbFlow.getRxHistory()+flow.getRx());
                flow.setTxHistory(dbFlow.getTxHistory()+flow.getTx());
                return true;
            }else{
                return false;
            }
        } catch (ParseException e) {

        }
        return false;
    }




}
