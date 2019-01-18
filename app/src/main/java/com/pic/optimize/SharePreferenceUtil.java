package com.pic.optimize;

import android.content.Context;
import android.content.SharedPreferences;

import com.pic.optimize.fresco.PicApplication;

public class SharePreferenceUtil {

    private static final String TAG = SharePreferenceUtil.class.getSimpleName();
    private static final String SHARED_NAME = "Book";

    public static SharedPreferences getSharedPreferences(Context context) {
        return (context == null ? PicApplication.getContext() : context).getSharedPreferences(SHARED_NAME,
                Context.MODE_PRIVATE);
    }

    public static void putString(Context context, String Key, String Value) {
        getSharedPreferences(context).edit().putString(Key, Value).apply();
    }

    public static String getString (Context context, String Key) {
        return getSharedPreferences(context).getString(Key, "");
    }


}
