package com.wiitel.tvhelper.view;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wiitel.tvhelper.R;

/**
 * Created by zhuchuntao on 16-12-8.
 */

public class DnsFragment extends Fragment {


    private static final String TAG = DnsFragment.class.getName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println(TAG+"onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(TAG+"onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println(TAG+"onCreateView");
        return inflater.inflate(R.layout.dns_layout, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println(TAG+"onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println(TAG+"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println(TAG+"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println(TAG+"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println(TAG+"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println(TAG+"onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println(TAG+"onDetach");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println(TAG+"onDestroy");
    }}
