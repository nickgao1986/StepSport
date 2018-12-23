package com.example.tutorial;

import android.view.View;

/**
 * Created by jerry.cai on 4/14/15.
 */
interface AnimationInterface {
    void fadeIn(View target, long duration, AnimationStartListener listener);

    void fadeOut(View target, long duration, AnimationEndListener listener);

    interface AnimationStartListener {
        void onAnimationStart();
    }

    interface AnimationEndListener {
        void onAnimationEnd();
    }
}
