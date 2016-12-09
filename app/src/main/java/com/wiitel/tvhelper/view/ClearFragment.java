package com.wiitel.tvhelper.view;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wiitel.library.ColorArcProgressBar;
import com.wiitel.tvhelper.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private Context context;

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
        View view = inflater.inflate(R.layout.clear_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println(TAG + "onActivityCreated");
        setData();
        clearProgress.setUnit(String.format(context.getResources().getString(R.string.device_avail_memory), getTotalMemory() / 1024 / 1024 / 1024));
    }

    private void setData() {
        clearProgress.setCurrentValues(getPercent());
    }


    @OnClick(R.id.clear_execute)
    public void onClick() {
        clear();
    }

    private void clear() {
        clearProgress.setCurrentValues(getPercent());
        //To change body of implemented methods use File | Settings | File Templates.
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = getActivity().getPackageManager();
        List<PackageInfo> pinfos = pm.getInstalledPackages
                (PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : pinfos) {
            if (!info.applicationInfo.processName.equals("com.wiitel.tvhelper")) {
                am.killBackgroundProcesses(info.applicationInfo.processName);
                setData();
            }
        }


    }

    //获取已经使用内存的百分比
    private int getPercent() {
        System.out.println("getAvailMemory() ==" + getAvailMemory() + ";getTotalMemory()==" + getTotalMemory() + ";getProcessCpuRate()==" + getProcessCpuRate());
        return 100 - (int) (getAvailMemory() * 100 / getTotalMemory());
    }

    //获取可用内存大小
    private long getAvailMemory() {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        System.out.println("可用内存---->>>" + mi.availMem / (1024 * 1024));
        return mi.availMem;
    }

    public static long getTotalMemory() {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
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
