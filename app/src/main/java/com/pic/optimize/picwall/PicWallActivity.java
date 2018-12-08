package com.pic.optimize.picwall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pic.optimize.NewsSwitchActivity;
import com.pic.optimize.R;

public class PicWallActivity extends AppCompatActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,PicWallActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_wall_main);

    }
}
