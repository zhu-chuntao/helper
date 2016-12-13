package com.wiitel.tvhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiitel.tvhelper.R;
import com.wiitel.tvhelper.data.AppInfo;
import com.wiitel.tvhelper.util.FlowUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuchuntao on 16-12-8.
 */

public class FlowAdapter extends BaseAdapter {

    private List<AppInfo> appInfoList;
    private LayoutInflater layoutInflater;

    public FlowAdapter(Context context, List<AppInfo> appInfoList) {
        this.appInfoList = appInfoList;
        if (this.appInfoList == null) {
            this.appInfoList = new ArrayList<AppInfo>();
        }
        this.layoutInflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return appInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Zujian zujian=null;
        if(convertView==null){
            zujian=new Zujian();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.flow_list_item, null);
            zujian.icon=(ImageView)convertView.findViewById(R.id.flow_item_icon);
            zujian.name=(TextView)convertView.findViewById(R.id.flow_item_name);
            zujian.down=(TextView)convertView.findViewById(R.id.flow_item_down);
            convertView.setTag(zujian);
        }else{
            zujian=(Zujian)convertView.getTag();
        }
        //绑定数据
        zujian.icon.setBackgroundDrawable(appInfoList.get(position).getIcon());
        zujian.name.setText(appInfoList.get(position).getLabel());
        zujian.down.setText(FlowUtil.calculateFlow(appInfoList.get(position).getFlow().getRxAll()));
        return convertView;
    }


    public final class Zujian{
        public ImageView icon;
        public TextView name;
        public TextView down;
    }
}
