package com.pic.optimize;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;

public class EventTest extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_test);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG","<<<<<activity ontouch down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG","<<<<<activity action move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("TAG","<<<<<activity action up");
                break;

        }
        return false;
    }

}
