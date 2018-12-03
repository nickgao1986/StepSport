package com.pic.optimize;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;

public class MainTest extends Activity{

    private LargeImageView mLargeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_main);

        mLargeImageView = (LargeImageView) findViewById(R.id.img);
        OkHttpClient client;
    }

}
