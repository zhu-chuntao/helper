package com.wiitel.tvhelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.wiitel.tvhelper.view.ClearFragment;
import com.wiitel.tvhelper.view.DnsFragment;
import com.wiitel.tvhelper.view.FlowFragment;
import com.wiitel.tvhelper.view.NetSpeedFragment;

import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @BindView(R.id.main_clear)
    ImageButton mainClear;
    @BindView(R.id.main_netspeed)
    ImageButton mainNetspeed;
    @BindView(R.id.main_flow)
    ImageButton mainFlow;
    @BindView(R.id.main_dns)
    ImageButton mainDns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ClearFragment f1 = new ClearFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //fl为占位布局
        ft.add(R.id.main_fragment, f1);
        ft.commit();
    }


    @OnClick({R.id.main_clear, R.id.main_netspeed, R.id.main_flow, R.id.main_dns})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_clear:
                showClear();
                break;
            case R.id.main_netspeed:
                showNetSpeed();
                break;
            case R.id.main_flow:
                showFlow();
                break;
            case R.id.main_dns:
                showDns();
                break;
        }
    }


    private void showClear() {
        System.out.println("showClear");
        Fragment t=getFragmentManager().findFragmentById(R.id.main_fragment);
        if(t instanceof ClearFragment){
            return;
        }
        ClearFragment f1 = new ClearFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //fl为占位布局
        ft.replace(R.id.main_fragment, f1);
        ft.commit();
    }

    private void showNetSpeed() {
        Fragment t=getFragmentManager().findFragmentById(R.id.main_fragment);
        if(t instanceof NetSpeedFragment){
            return;
        }
        NetSpeedFragment f1 = new NetSpeedFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //fl为占位布局
        ft.replace(R.id.main_fragment, f1);
        ft.commit();
    }

    private void showFlow() {
        Fragment t=getFragmentManager().findFragmentById(R.id.main_fragment);
        if(t instanceof FlowFragment){
            return;
        }
        FlowFragment f1 = new FlowFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //fl为占位布局
        ft.replace(R.id.main_fragment, f1);
        ft.commit();
    }


    private void showDns() {
        Fragment t=getFragmentManager().findFragmentById(R.id.main_fragment);
        if(t instanceof DnsFragment){
            return;
        }
        DnsFragment f1 = new DnsFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //fl为占位布局
        ft.replace(R.id.main_fragment, f1);
        ft.commit();
    }





    private PackageManager pm;
    StringBuilder sb = new StringBuilder();
    StringBuilder sbCache = new StringBuilder();

    private long cacheS;
    Handler mHadler = new Handler();


    class MyPackageStateObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            String packageName = pStats.packageName;
            long cacheSize = pStats.cacheSize;
            long codeSize = pStats.codeSize;
            long dataSize = pStats.dataSize;
            cacheS += cacheSize;
//            sb.delete(0, sb.length());
            if (cacheSize > 0) {
                sb.append("packageName = " + packageName + "\n")
                        .append("   cacheSize: " + cacheSize + "\n")
                        .append("   dataSize: " + dataSize + "\n")
                        .append("-----------------------\n")
                ;

                Log.e("aaaa", sb.toString());
            }

        }
    }


    class ClearCacheObj extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, final boolean succeeded) throws RemoteException {
            mHadler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "清楚状态： " + succeeded, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 清理全部应用程序缓存的点击事件
     *
     * @param view
     */
    public void cleanAll(View view) {
        //freeStorageAndNotify
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods) {
            if ("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(pm, Long.MAX_VALUE, new ClearCacheObj());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    private void getCaches() {
        // scan
        pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        int max = packages.size();
        int current = 0;
        sb.delete(0, sb.length());
        sb.append("所有已安装的app信息：\n");
        sb.append("所有App 总和：" + max + " \n");
        //tvShowCaches.setText(sb.toString());
        for (PackageInfo pinfo : packages) {
            String packageName = pinfo.packageName;
            try {

                Method getPackageSizeInfo = PackageManager.class
                        .getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, packageName, new MyPackageStateObserver());
                current++;
            } catch (Exception e) {
                current++;
                e.printStackTrace();
            }

        }
        //===到这里，数据准备完成
        mHadler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "缓存信息获取完成", Toast.LENGTH_SHORT).show();
                sbCache.append(Formatter.formatFileSize(getApplicationContext(), cacheS) + "\n");
                //tvShowCaches.setText(sb.toString());
                //tvAppCache.setText(sbCache.toString());
                sbCache.delete(0, sbCache.length());
            }
        }, 1000);
        //ok,所有应用程序信息显示完成
    }
}
