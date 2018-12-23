package com.example.tutorial;


import android.app.Activity;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.view.View;

public class TargetView {

	private View mView;

    public TargetView(String targetView, Activity targetAct, Fragment targetFragment, boolean isFragment) {
        int targetViewId = targetAct.getResources().getIdentifier(targetView, "id", targetAct.getPackageName());
        if (targetFragment != null && isFragment) {
            View view = targetFragment.getView();
            if (view == null) {
                mView = targetAct.findViewById(targetViewId);
            } else {
                mView = view.findViewById(targetViewId);
            }
        } else {
            mView = targetAct.findViewById(targetViewId);
        }
    }

    public View getView() {
        return mView;
    }

    public boolean isVisible() {
        return (mView == null) ? false : (mView.getVisibility() == View.VISIBLE);
    }

    public boolean isTotallyVisible() {
        if (mView == null) {
            return false;
        }

        Rect visibleRect = new Rect();
        mView.getLocalVisibleRect(visibleRect);
        return isVisible() && (visibleRect.left == 0 && visibleRect.top == 0
                && visibleRect.width() == mView.getWidth() && visibleRect.height() == mView.getHeight());
    }
}
