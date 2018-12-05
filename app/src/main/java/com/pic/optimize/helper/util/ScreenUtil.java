package com.pic.optimize.helper.util;

import android.content.Context;

public class ScreenUtil {

    public static int dip2px(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dip * scale + 0.5F);
    }

    public static int getScreenWidth(Context context) {
        return context != null ? context.getResources().getDisplayMetrics().widthPixels : 1;
    }
}
