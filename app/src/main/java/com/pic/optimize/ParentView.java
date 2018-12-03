package com.pic.optimize;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class ParentView extends RelativeLayout {


    public ParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 方法用户拦截被传递过来的事件，用于判断被传递过来的事件是否需要被当前的view进行处理
     *  return true : 拦截该事件，将该事件交给当前view的onTouchEvent方法进行处理
     *  return false和super.interceptTouchEvent(ev)，事件将不会被拦截，会被分发到子控件中）
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG","<<<<<ParentView ontouch down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG","<<<<<ParentView action move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("TAG","<<<<<ParentView action up");
                break;
        }
        return false;
    }
}
