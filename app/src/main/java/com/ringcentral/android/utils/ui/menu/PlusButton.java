package com.ringcentral.android.utils.ui.menu;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class PlusButton extends ImageView implements IAction {
    private final int DEFAULT_ANI_DURATION = 200;
    private IAnimation mAnimation;
    private boolean mIsInit = false;
    private IAction mActionListener;

    public PlusButton(Context context) {
        super(context);
        init();
    }

    public PlusButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlusButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!mIsInit) {
            mAnimation = new PlusMenuAnimation();
            mIsInit = true;
        }
    }

    public void setActionListener(IAction listener) {
        mActionListener = listener;
    }


    private void close(int duration) {
        mAnimation.close(this, duration);
        if (mActionListener != null) {
            if (duration != 0) {
                mActionListener.closeWithAnimation();
            } else {
                mActionListener.closeWithoutAnimation();
            }
        }
    }

    @Override
    public void openWithAnimation() {
        mAnimation.open(this, DEFAULT_ANI_DURATION);
        if (mActionListener != null) {
            mActionListener.openWithAnimation();
        }
    }

    @Override
    public void closeWithAnimation() {
        close(DEFAULT_ANI_DURATION);
    }

    @Override
    public void closeWithoutAnimation() {
        close(0);
    }

}
