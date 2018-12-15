package com.pic.optimize.fresco;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

public class PicApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Fresco.initialize(this);
    }

    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }

}
