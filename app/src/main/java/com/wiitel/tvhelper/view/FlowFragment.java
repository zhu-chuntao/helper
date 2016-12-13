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
import com.wiitel.tvhelper.util.FlowUtil;
import com.wiitel.tvhelper.util.TimePatternUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flow_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //删除3个月以前的数据
        DBManager.getInstance(context).deleteFlow(TimePatternUtil.getLastMonth(3));

        //接收总量
        long nowRx = TrafficStats.getTotalRxBytes();
        //发送总量
        long nowTx = TrafficStats.getTotalTxBytes();

        AppFlow flowAll = new AppFlow();
        flowAll.setAppid(1);
        flowAll.setRx(nowRx);
        flowAll.setTx(nowTx);
        try {
            flowAll.setCurrentMonth(TimePatternUtil.getDate(System.currentTimeMillis(), TimePatternUtil.TimePattern.YM));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        flowAll = getAppFlow(flowAll);


        flowDayDown.setText(String.format(context.getText(R.string.flow_day_down).toString(), FlowUtil.calculateFlow(flowAll.getRx())));
        flowDayUp.setText(String.format(context.getText(R.string.flow_day_up).toString(), FlowUtil.calculateFlow(flowAll.getTx())));
        flowAllDown.setText(String.format(context.getText(R.string.flow_all_down).toString(), FlowUtil.calculateFlow(flowAll.getRxAll())));
        flowAllUp.setText(String.format(context.getText(R.string.flow_all_up).toString(), FlowUtil.calculateFlow(flowAll.getTxAll())));


        PackageManager pm = getActivity().getPackageManager();
        List<PackageInfo> pinfos = pm.getInstalledPackages
                (PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);

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
                        try {
                            flow.setCurrentMonth(TimePatternUtil.getDate(System.currentTimeMillis(), TimePatternUtil.TimePattern.YM));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
                        long rx = TrafficStats.getUidRxBytes(uId);
                        //如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
                        long tx = TrafficStats.getUidTxBytes(uId);
                        //System.out.println("uId==="+uId+"rx====="+rx+";tx=="+tx);
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
        Collections.sort(appInfoList, new SortRx());
        FlowAdapter adapter = new FlowAdapter(context, appInfoList);
        flowAppList.setAdapter(adapter);

    }

    /**
     * 针对流量进行排序
     */
    class SortRx implements Comparator<AppInfo> {
        public int compare(AppInfo r1, AppInfo r2) {
            if (r1.getFlow().getRxAll() > r2.getFlow().getRxAll()) {
                return -1;
            } else if (r1.getFlow().getRxAll() < r2.getFlow().getRxAll())
                return 1;
            else {
                return 0;
            }

        }
    }

    @OnClick(R.id.flow_save)
    public void onClick() {
        //接收总量
        long nowRx = TrafficStats.getTotalRxBytes();

        //发送总量
        long nowTx = TrafficStats.getTotalTxBytes();

        AppFlow flowAll = new AppFlow();
        flowAll.setAppid(1);
        flowAll.setRx(nowRx);
        flowAll.setTx(nowTx);
        try {
            flowAll.setCurrentMonth(TimePatternUtil.getDate(System.currentTimeMillis(), TimePatternUtil.TimePattern.YM));
            flowAll = getAppFlow(flowAll);
            flowAll.setRxHistory(100);
            flowAll.setTxHistory(99);
            DBManager.getInstance(context).insertFlow(flowAll);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("id=" + flowAll.getAppid() + ";" + flowAll.getCurrentMonth() + "---------------" + e.getMessage());
        }


    }

    private String getMByte(long byteNumber) {
        return byteNumber / 1024 + "Kb";
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
