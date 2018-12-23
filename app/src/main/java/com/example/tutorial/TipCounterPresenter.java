package com.example.tutorial;


import android.app.Activity;
import android.support.v4.app.Fragment;

import java.util.HashMap;

public class TipCounterPresenter extends TipPresenter {
    private static final int CHECK_RESUME_MAX_COUNTER = 3;
    private static HashMap<String, Integer> sCounterMap = new HashMap<String, Integer>();
    private String mTargetName;

    public TipCounterPresenter(Activity target) {
        super(target);
        mTargetName = target.getClass().getName();
    }

    @Override
    public void onHandleFinish() {
        //try to find current tip, whether is marked, if not, that is, next time we still needing to shown the tip directly.
        TipInfo tip = getTipManager().getTip();
        if (tip != null && !tip.isMarked()) {
            removeCounter();
        }
    }

    @Override
    public void onHandleResume() {
        TipManager tipManager = getTipManager();
        if (!tipManager.hasTip()) {
            return;
        }

        tipManager.resetCursor();

        //check whether the view is hide
        if (tipManager.isHidden()) {
            tipManager.remove();
            tipManager.showTip();
        } else {
            increaseCounter();
            tipManager.hide();
            if (checkToShowTip()) {
                tipManager.showTip();
            }
        }
    }

    @Override
    public void onHandlePause() {
        if (!getTipManager().hasTip()) {
            return;
        }

        getTipManager().hide();
    }

    @Override
    public boolean onHandleBackPressed() {
        return super.onHandleBackPressed();
    }

    @Override
    public void onHandleTouch() {
    }

    @Override
    public void setTargetFragment(Fragment targetFragment) {
        mTargetFragment = targetFragment;
        mTipManager.setTargetFragment(targetFragment);
    }

    //private methods
    private boolean checkToShowTip() {
        boolean result = false;

        if (!sCounterMap.containsKey(mTargetName)) {
            result = true;
        } else {
            if (sCounterMap.get(mTargetName) >= CHECK_RESUME_MAX_COUNTER) {
                result = true;
            }
        }

        if (result) {
            resetCounter();
        }

        return result;
    }

    private void increaseCounter() {
        if (sCounterMap.containsKey(mTargetName)) {
            int value = sCounterMap.get(mTargetName);
            sCounterMap.put(mTargetName, value + 1);
        }
    }

    private void removeCounter() {
        if (sCounterMap.containsKey(mTargetName)) {
            sCounterMap.remove(mTargetName);
        }
    }

    private void resetCounter() {
        sCounterMap.put(mTargetName, 0);
    }


}
