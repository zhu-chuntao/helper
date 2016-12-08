package com.wiitel.tvhelper.data;

import android.graphics.drawable.Drawable;

/**
 * Created by zhuchuntao on 16-12-8.
 */

public class AppInfo {

    private AppFlow flow;
    private String label;
    private Drawable icon;

    public AppFlow getFlow() {
        return flow;
    }

    public void setFlow(AppFlow flow) {
        this.flow = flow;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
