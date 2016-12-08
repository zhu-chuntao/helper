package com.wiitel.tvhelper.view;

import android.app.Fragment;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wiitel.tvhelper.R;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuchuntao on 16-12-8.
 */

public class NetSpeedFragment extends Fragment {


    private static final String TAG = NetSpeedFragment.class.getName();
    @BindView(R.id.netspeed_value)
    TextView netspeedValue;


    private MyHandler mHandler;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;
    private Timer time;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        View view = inflater.inflate(R.layout.netspeed_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println(TAG + "onActivityCreated");
        mHandler = new MyHandler(getActivity().getMainLooper());
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeStamp = System.currentTimeMillis();
        time = new Timer();
        time.schedule(task, 1000, 2000);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            showNetSpeed();
        }
    };

    private void showNetSpeed() {

        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        Message msg = mHandler.obtainMessage();
        msg.what = 100;
        msg.obj = String.valueOf(speed) + " kb/s";

        mHandler.sendMessage(msg);//更新界面
    }


    private long getTotalRxBytes() {
        try {
            return TrafficStats.getUidRxBytes(getActivity().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
        } catch (Exception e) {
            return 0;
        }

    }


    class MyHandler extends Handler {

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法，接受数据
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 此处可以更新UI
            netspeedValue.setText(msg.obj + "");
        }
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
        time.cancel();
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
