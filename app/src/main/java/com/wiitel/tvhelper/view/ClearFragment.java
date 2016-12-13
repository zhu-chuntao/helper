package com.wiitel.tvhelper.view;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.wiitel.library.ColorArcProgressBar;
import com.wiitel.tvhelper.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhuchuntao on 16-12-8.
 */

public class ClearFragment extends Fragment {

    private static final String TAG = ClearFragment.class.getName();
    @BindView(R.id.clear_progress)
    ColorArcProgressBar clearProgress;
    @BindView(R.id.clear_execute)
    Button clearExecute;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clear_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setData();
        clearProgress.setUnit(String.format(context.getResources().getString(R.string.device_avail_memory), getTotalMemory() / 1024 / 1024));
        clearExecute.requestFocus();
        //android_command();
    }

    private void setData() {
        clearProgress.setCurrentValues(getPercent());
    }


    @OnClick(R.id.clear_execute)
    public void onClick() {
        clear();
        cleanAll();
        setData();
    }

    /**
     * 杀进程
     */
    private void clear() {
        clearProgress.setCurrentValues(getPercent());
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = getActivity().getPackageManager();
        List<PackageInfo> pinfos = pm.getInstalledPackages
                (PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : pinfos) {
            if (!info.applicationInfo.processName.equals("com.wiitel.tvhelper")) {
                am.killBackgroundProcesses(info.applicationInfo.processName);
                setData();
            }
        }
    }

    //获取已经使用内存的百分比
    private int getPercent() {
        return 100 - (int) (getAvailMemory() * 100 / getTotalMemory());
    }

    //获取可用内存大小
    private long getAvailMemory() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    //获取设备总内存
    public static long getTotalMemory() {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();

            String[] arrayOfString = memoryLine.split("\\s+");

            br.close();
            return Integer.parseInt(arrayOfString[1]) * 1024l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * get CPU rate
     *
     * @return
     */
    private int getProcessCpuRate() {

        StringBuilder tv = new StringBuilder();
        int rate = 0;

        try {
            String Result;
            Process p;
            p = Runtime.getRuntime().exec("top -n 1");

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((Result = br.readLine()) != null) {
                if (Result.trim().length() < 1) {
                    continue;
                } else {
                    String[] CPUusr = Result.split("%");
                    tv.append("USER:" + CPUusr[0] + "\n");
                    String[] CPUusage = CPUusr[0].split("User");
                    String[] SYSusage = CPUusr[1].split("System");
                    tv.append("CPU:" + CPUusage[1].trim() + " length:" + CPUusage[1].trim().length() + "\n");
                    tv.append("SYS:" + SYSusage[1].trim() + " length:" + SYSusage[1].trim().length() + "\n");

                    rate = Integer.parseInt(CPUusage[1].trim()) + Integer.parseInt(SYSusage[1].trim());
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rate;
    }


    //缓存相关的业务
    private PackageManager pm;

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
            System.out.println("onGetStatsCompleted======packageName==" + packageName);

            //===到这里，数据准备完成
            mHadler.post(new Runnable() {
                @Override
                public void run() {
                }
            });

        }
    }


    class ClearCacheObj extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, final boolean succeeded) throws RemoteException {
            mHadler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("ClearCacheObj onRemoveCompleted");
                    Toast.makeText(context, "清楚状态： " + succeeded, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 清理全部应用程序缓存
     *
     */
    public void cleanAll() {
        //freeStorageAndNotify
//        Method[] methods = PackageManager.class.getMethods();
//        for (Method method : methods) {
//            if ("freeStorageAndNotify".equals(method.getName())) {
//                try {
//                    method.invoke(pm, Long.MAX_VALUE, new ClearCacheObj());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return;
//            }
//        }

        try {
            PackageManager packageManager = context.getPackageManager();
            Method localMethod = packageManager.getClass().getMethod("freeStorageAndNotify", Long.TYPE,
                    IPackageDataObserver.class);
            Long localLong = Long.valueOf(getEnvironmentSize() - 1L);
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = localLong;
            localMethod.invoke(packageManager, localLong, new IPackageDataObserver.Stub() {

                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                    System.out.println("onRemoveCompleted packageName=="+succeeded);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cleanAll.......=="+e.toString());
        }
    }

    private static long getEnvironmentSize() {
        File localFile = Environment.getDataDirectory();
        long l1;
        if (localFile == null)
            l1 = 0L;
        while (true) {
            String str = localFile.getPath();
            StatFs localStatFs = new StatFs(str);
            long l2 = localStatFs.getBlockSize();
            l1 = localStatFs.getBlockCount() * l2;
            return l1;
        }

    }

    private void getCaches() {
        // scan
        pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo pinfo : packages) {
            String packageName = pinfo.packageName;
            try {
                Method getPackageSizeInfo = PackageManager.class
                        .getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, packageName, new MyPackageStateObserver());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("error===" + e.getMessage());
            }

        }


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
