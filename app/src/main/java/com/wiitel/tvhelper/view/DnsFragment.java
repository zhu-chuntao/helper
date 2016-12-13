package com.wiitel.tvhelper.view;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wiitel.tvhelper.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zhuchuntao on 16-12-8.
 */

public class DnsFragment extends Fragment {


    private static final String TAG = DnsFragment.class.getName();

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dns_layout, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Settings.System.putString(context.getContentResolver(), android.provider.Settings.System.WIFI_USE_STATIC_IP, "0");
        Settings.System.putString(context.getContentResolver(), android.provider.Settings.System.WIFI_STATIC_DNS1, "192.168.0.2");
        String statdns1 = android.provider.Settings.System.WIFI_STATIC_DNS1;
        String statdns2 = android.provider.Settings.System.WIFI_STATIC_DNS2;
        String sgateway = android.provider.Settings.System.WIFI_STATIC_GATEWAY;
        String staticip = android.provider.Settings.System.WIFI_STATIC_IP;
        String snetmask = android.provider.Settings.System.WIFI_STATIC_NETMASK;
        String staticus = android.provider.Settings.System.WIFI_USE_STATIC_IP;
        System.out.println("statdns1===" + statdns1 + ";statdns2==" + statdns2 + ";sgateway==" + sgateway + ";staticip=" + staticip + ";snetmask=" + snetmask + ";staticus=" + staticus);


    }

    private String getDns() {
        try {
            Process localProcess = Runtime.getRuntime().exec("getprop net.dns1");
            String con = "";
            String result = "";

            BufferedReader br = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
            while ((result = br.readLine()) != null) {
                con += result;
            }
            System.out.println("jsjsjsjs====" + localProcess.toString() + ";" + con.toString());

            return con;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "";
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
