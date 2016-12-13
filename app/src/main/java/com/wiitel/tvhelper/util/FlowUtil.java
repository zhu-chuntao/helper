package com.wiitel.tvhelper.util;

/**
 * Created by zhuchuntao on 16-12-13.
 */

public class FlowUtil {

    public static final String calculateFlow(long dataSize) {
        if (dataSize / 1024 / 1024 > 1) {
            return dataSize / 1024 / 1024 + " M ";
        } else {
            return dataSize / 1024 + " kb ";
        }
    }
}
