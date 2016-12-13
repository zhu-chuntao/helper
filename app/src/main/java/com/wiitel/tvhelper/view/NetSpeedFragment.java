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
import android.widget.Button;
import android.widget.TextView;

import com.wiitel.tvhelper.R;
import com.wiitel.tvhelper.download.AppUpdateAsync;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhuchuntao on 16-12-8.
 */

public class NetSpeedFragment extends Fragment {


    private static final String TAG = NetSpeedFragment.class.getName();
    private static final int MAX_COUNT = 5;

    @BindView(R.id.netspeed_value)
    TextView netspeedValue;
    @BindView(R.id.netspeed_final_value)
    TextView netspeedFinalValue;
    @BindView(R.id.netspeed_restart)
    Button netspeedRestart;


    private MyHandler mHandler;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;
    private Timer time;
    private Context context;

    private String url = "http://live.wiitelott.com/test.test";

    private int speedCount;

    private String resultBuffer;
    private MyTask task;

    private long finalSpeed = 0;
    private AppUpdateAsync downloadTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.netspeed_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = new MyHandler(getActivity().getMainLooper());
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeStamp = System.currentTimeMillis();

        downloadTask= new AppUpdateAsync(context, false, true, null);
        downloadTask.execute(url);

        start();
    }

    private void start() {
        resultBuffer = "";
        speedCount = 0;
        finalSpeed = 0;
        time = new Timer();
        task = new MyTask();
        time.schedule(task, 1000, 1000);
    }


    private class MyTask extends TimerTask {

        @Override
        public void run() {
            showNetSpeed();
        }
    }

    private void showNetSpeed() {

        if (speedCount >= MAX_COUNT) {
            time.cancel();
            time = null;
        } else {
            long nowTotalRxBytes = getTotalRxBytes();
            long nowTimeStamp = System.currentTimeMillis();
            long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

            lastTimeStamp = nowTimeStamp;
            lastTotalRxBytes = nowTotalRxBytes;

            Message msg = mHandler.obtainMessage();
            msg.what = 100;
            finalSpeed = finalSpeed + speed;
            resultBuffer = resultBuffer + String.format(context.getResources().getString(R.string.speed_test_message), speedCount + 1, String.valueOf(speed)) + "\n";
            msg.obj = resultBuffer;
            mHandler.sendMessage(msg);//更新界面
            speedCount++;
        }
    }


    private long getTotalRxBytes() {
        try {
            return TrafficStats.getUidRxBytes(getActivity().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
        } catch (Exception e) {
            return 0;
        }

    }

    @OnClick(R.id.netspeed_restart)
    public void onClick() {
        if (null != time) {
            time.cancel();
            time = null;
        }
        start();
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
            netspeedFinalValue.setText(String.format(context.getString(R.string.speed_final_message), String.valueOf(finalSpeed / (speedCount + 1))));
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
        if (null != time) {
            time.cancel();
            time = null;
        }
        downloadTask.stopDownload(true);

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
