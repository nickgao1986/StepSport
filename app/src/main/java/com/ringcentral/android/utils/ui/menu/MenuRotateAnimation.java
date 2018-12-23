package com.ringcentral.android.utils.ui.menu;


import android.view.View;
import android.view.animation.Animation;

public class MenuRotateAnimation extends ViewStateManager implements IAnimation {
    private float mRotateDegrees = -180.0f;
    private IAnimationDelegate mAnimationDelegate;

    @Override
    public void setAnimationDelegate(IAnimationDelegate aniDelegate) {
        mAnimationDelegate = aniDelegate;
    }

    @Override
    public void open(final View view, int duration) {
        if (mAnimationDelegate != null) {
            mAnimationDelegate.onAnimationStart(true);
        }

        setViewState(view, View.VISIBLE, true);
        Animation animation;
        animation = new android.view.animation.RotateAnimation(mRotateDegrees, 0, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
        animation.setFillAfter(true);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    @Override
    public void close(final View view, int duration, int tab, boolean isSwitch) {
        Animation animation;
        animation = new android.view.animation.RotateAnimation(0, mRotateDegrees, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
        animation.setFillAfter(true);
        animation.setDuration(duration);
        animation.setStartOffset(0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                setViewState(view, View.GONE, false);
                if (mAnimationDelegate != null) {
                    mAnimationDelegate.onAnimationEnd(1,false,false);
                }
            }
        });
        view.startAnimation(animation);
    }


}
