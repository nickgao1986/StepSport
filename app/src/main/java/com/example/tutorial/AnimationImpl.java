package com.example.tutorial;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class AnimationImpl implements AnimationInterface {
    private static final String ALPHA = "alpha";
    private static final float INVISIBLE = 0f;
    private static final float VISIBLE = 1f;

    private final AccelerateDecelerateInterpolator mInterpolator;

    public AnimationImpl() {
        mInterpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    public void fadeIn(View target, long duration, final AnimationStartListener listener) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(target, ALPHA, INVISIBLE, VISIBLE);
        objectAnimator.setDuration(duration).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                listener.onAnimationStart();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        objectAnimator.start();
    }

    @Override
    public void fadeOut(View target, long duration, final AnimationEndListener listener) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(target, ALPHA, INVISIBLE);
        objectAnimator.setDuration(duration).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                listener.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        objectAnimator.start();
    }

}
