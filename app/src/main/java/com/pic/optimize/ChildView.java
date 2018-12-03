package com.pic.optimize;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ChildView extends View {


    public ChildView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     *  当前的view把事件进行了拦截，则事件则会被传递到该方法中
     *  return false：表明没有消费该事件，事件将会以冒泡的方式一直被传递到上层的view或Activity中的
     *  return true: 表明消费了该事件，事件到此结束。
     *  return super.onTouchEvent(event)：默认情况，和return false一样。
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG","<<<<<childview ontouch down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG","<<<<<ChildView action move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("TAG","<<<<<ChildView action up");
                break;

        }
        return false;
    }
}
