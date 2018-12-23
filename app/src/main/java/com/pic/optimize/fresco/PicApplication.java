package com.pic.optimize.fresco;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.pic.optimize.http.OkHttpUtil;
import com.alibaba.android.arouter.launcher.ARouter;

public class PicApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        ARouter.openLog();
        ARouter.openDebug();
        ARouter.init(this);

        Fresco.initialize(this);
        OkHttpUtil.init(this);
    }

    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    public static String getResString(int resid) {
        return mContext.getString(resid);
    }

}
