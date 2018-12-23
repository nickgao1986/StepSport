package com.ringcentral.android.utils.ui.menu;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;

public class PlusMenuAnimation implements IAnimation {
    @Override
    public void setAnimationDelegate(IAnimationDelegate aniDelegate) {
    }

    @Override
    public void open(View view, int duration) {
        Animation animation = getAnimation(true, duration);
        view.startAnimation(animation);
    }


    @Override
    public void close(View view, int duration, int tab, boolean isSwitch) {
        Animation animation = getAnimation(false, duration);
        view.startAnimation(animation);
    }

    private Animation getAnimation(boolean expanded, int duration) {
        Animation animation = new RotateAnimation(expanded ? 0.0f : 45.0f, expanded ? 45.0f : 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(0);
        animation.setDuration(duration);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);
        return animation;
    }

}
