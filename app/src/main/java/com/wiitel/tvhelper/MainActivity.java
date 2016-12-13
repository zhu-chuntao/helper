package com.wiitel.tvhelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.wiitel.tvhelper.http.LocalUpdateRequest;
import com.wiitel.tvhelper.http.UpdateData;
import com.wiitel.tvhelper.view.ClearFragment;
import com.wiitel.tvhelper.view.DnsFragment;
import com.wiitel.tvhelper.view.FlowFragment;
import com.wiitel.tvhelper.view.NetSpeedFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity implements LocalUpdateRequest.UpdateListListener {

    @BindView(R.id.main_clear)
    Button mainClear;
    @BindView(R.id.main_netspeed)
    Button mainNetspeed;
    @BindView(R.id.main_flow)
    Button mainFlow;
    @BindView(R.id.main_dns)
    Button mainDns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ClearFragment f1 = new ClearFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //fl为占位布局
        ft.add(R.id.main_fragment, f1);
        ft.commit();
        LocalUpdateRequest request = new LocalUpdateRequest(this);
        request.setListener(this);
        request.serverRequestData(null);
       
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
        Fragment t = getFragmentManager().findFragmentById(R.id.main_fragment);
        if (t instanceof ClearFragment) {
            return;
        }
        ClearFragment f1 = new ClearFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //fl为占位布局
        ft.replace(R.id.main_fragment, f1);
        ft.commit();
    }

    private void showNetSpeed() {
        Fragment t = getFragmentManager().findFragmentById(R.id.main_fragment);
        if (t instanceof NetSpeedFragment) {
            return;
        }
        NetSpeedFragment f1 = new NetSpeedFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //fl为占位布局
        ft.replace(R.id.main_fragment, f1);
        ft.commit();
    }

    private void showFlow() {
        Fragment t = getFragmentManager().findFragmentById(R.id.main_fragment);
        if (t instanceof FlowFragment) {
            return;
        }
        FlowFragment f1 = new FlowFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //fl为占位布局
        ft.replace(R.id.main_fragment, f1);
        ft.commit();
    }


    private void showDns() {
        Fragment t = getFragmentManager().findFragmentById(R.id.main_fragment);
        if (t instanceof DnsFragment) {
            return;
        }
        DnsFragment f1 = new DnsFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //fl为占位布局
        ft.replace(R.id.main_fragment, f1);
        ft.commit();
    }


    @Deprecated
    private String android_command() {
        //要执行的命令行
        //String ret = "iptables --help";
        String ret = "ndc resolver";
        String con = "";
        String result = "";
        Process p;
        try {
            p = Runtime.getRuntime().exec(ret);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((result = br.readLine()) != null) {
                con += result;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //可以打出命令执行的结果
        System.out.println("==========================con:" + con);
        //ret = do_command(con);
        System.out.println("==========================ret:" + ret);
        return ret;
    }

    @Override
    public void success(UpdateData data) {

    }

    @Override
    public void start(String tag, String msg) {

    }

    @Override
    public void error(String tag, String msg) {

    }
}
