package com.pic.optimize.fresco;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class PicApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
