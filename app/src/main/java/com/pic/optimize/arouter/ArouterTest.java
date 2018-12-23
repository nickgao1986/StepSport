package com.pic.optimize.arouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pic.optimize.DragIconActivity;
import com.pic.optimize.R;
import com.weather.nick.mylibrary.TestService;

public class ArouterTest extends Activity {

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,ArouterTest.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arouter_activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //页面
                ARouter.getInstance().build("/test3/activity3").navigation();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //服务
//                HelloService service = (HelloService) ARouter.getInstance().build("/service/hello").navigation();
//                service.sayHello("hello sdsdf");

//                Response call = BAFRouter.call("bbtrp://com.babytree.pregnancy/bb_userinfo_service/getuserinfo", new Bundle());
//
//                String message = call.message;
//                Log.d(TAG, "onClick: " + message);


                TestService service = (TestService) ARouter.getInstance().build("/testservice/hello").navigation();
                service.sayHello("hello sdsdf");

            }
        });

    }
}
