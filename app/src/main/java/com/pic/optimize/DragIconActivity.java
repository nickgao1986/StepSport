package com.pic.optimize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pic.optimize.view.MyDragLayer;
import com.pic.optimize.view.MyLinearlayout;

public class DragIconActivity extends Activity {


    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,DragIconActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_drag_animation);
        MyLinearlayout layout = (MyLinearlayout)findViewById(R.id.linear);
        layout.mDragLayer =  (MyDragLayer) findViewById(R.id.rootView);
        layout.mDragLayer.setTrashBin(findViewById(R.id.trashbin));
    }
}
