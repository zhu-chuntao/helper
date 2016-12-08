package com.wiitel.tvhelper.view;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.wiitel.tvhelper.R;
import com.wiitel.tvhelper.adapter.FlowAdapter;
import com.wiitel.tvhelper.data.AppFlow;
import com.wiitel.tvhelper.data.AppInfo;
import com.wiitel.tvhelper.db.DBManager;
import com.wiitel.tvhelper.util.TimePatternUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuchuntao on 16-12-8.
 */

public class FlowFragment extends Fragment {


    private static final String TAG = FlowFragment.class.getName();
    @BindView(R.id.flow_day_down)
    Button flowDayDown;
    @BindView(R.id.flow_day_up)
    Button flowDayUp;
    @BindView(R.id.flow_all_down)
    Button flowAllDown;
    @BindView(R.id.flow_all_up)
    Button flowAllUp;
    @BindView(R.id.flow_app_list)
    ListView flowAppList;

    private Context context;

    private List<AppInfo> appInfoList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        System.out.println(TAG + "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(TAG + "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println(TAG + "onCreateView");
        View view = inflater.inflate(R.layout.flow_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //接收总量
        long nowRx = TrafficStats.getTotalRxBytes();

        //发送总量
        long nowTx = TrafficStats.getTotalTxBytes();


        AppFlow flowAll = new AppFlow();
        flowAll.setAppid(1);
        flowAll.setRx(nowRx);
        flowAll.setTx(nowTx);

        flowAll = getAppFlow(flowAll);

        flowDayDown.setText(String.format(context.getText(R.string.flow_day_down).toString(),getMByte(flowAll.getRx())));
        flowDayUp.setText(String.format(context.getText(R.string.flow_day_up).toString(),getMByte(flowAll.getTx())));
        flowAllDown.setText(String.format(context.getText(R.string.flow_all_down).toString(),getMByte(flowAll.getRxAll())));
        flowAllUp.setText(String.format(context.getText(R.string.flow_all_down).toString(),getMByte(flowAll.getTxAll())));



        PackageManager pm = getActivity().getPackageManager();
        List<PackageInfo> pinfos = pm.getInstalledPackages
                (PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);

        System.out.println(TAG + "onActivityCreated==" + nowTx + ";" + pinfos.size());
        appInfoList = new ArrayList<>();
        for (PackageInfo info : pinfos) {
            //请求每个程序包对应的androidManifest.xml里面的权限
            String[] premissions = info.requestedPermissions;

            if (premissions != null && premissions.length > 0) {
                //找出需要网络服务的应用程序
                for (String premission : premissions) {
                    if ("android.permission.INTERNET".equals(premission)) {
                        AppInfo app = new AppInfo();
                        app.setIcon(info.applicationInfo.loadIcon(pm));
                        app.setLabel(info.applicationInfo.loadLabel(pm).toString());
                        //获取每个应用程序在操作系统内的进程id
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

                        flow = getAppFlow(flow);
                        app.setFlow(flow);

                        appInfoList.add(app);
                        break;
                    }
                }
            }
        }

        FlowAdapter adapter = new FlowAdapter(context,appInfoList);
        flowAppList.setAdapter(adapter);

    }

    private String getMByte(long byteNumber){
            return byteNumber/1024+"Kb";
    }

    //设置历史的流量
    private AppFlow getAppFlow(AppFlow flow) {
        DBManager dbManager = DBManager.getInstance(context);
        try {
            AppFlow dbFlow = dbManager.queryFlowList(TimePatternUtil.getDate(System.currentTimeMillis(), TimePatternUtil.TimePattern.YM), flow.getAppid());
            if (dbFlow != null) {
                flow.setRxHistory(dbFlow.getRxHistory());
                flow.setTxHistory(dbFlow.getTxHistory());
            }
        } catch (ParseException e) {
        }

        return flow;
    }


    @Override
    public void onStart() {
        super.onStart();
        System.out.println(TAG + "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println(TAG + "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println(TAG + "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println(TAG + "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println(TAG + "onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println(TAG + "onDetach");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println(TAG + "onDestroy");
    }
}
