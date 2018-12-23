package com.example.tutorial;


import android.app.Activity;
import android.support.v4.app.Fragment;

import java.util.List;


public class TipManager implements TipViewInterface, PermissionChangedListener {
    private Activity mTarget;
    private Fragment mTargetFragment;
    private List<TipInfo> mTips = null;
    private TipInfo mTip = null;
    private int mTipCursor = 0;
    private TipViewInterface mTipView = null;
    private boolean mHasTip = false;
    private TipViewEventTrace mTipViewEventTrace = null;

    public TipManager(Activity target, TipViewEventTrace listener) {
        mTarget = target;
        mTipViewEventTrace = listener;
        mTips = TipHelper.getUnmarkedTips(target);
        mHasTip = (mTips != null && mTips.size() > 0);
        mTipCursor = 0;
        mTipView = null;
    }

    @Override
    public void onPermissionChanged() {
        if (!mHasTip) {
            return;
        }

        mTipCursor = 0;

        if (mTip != null) {
            TargetView targetView = new TargetView(mTip.getViewId(), mTarget, mTargetFragment, mTip.isFragment());
            if (!targetView.isTotallyVisible()) {
                remove();
            }
        }

    }

    public void setTargetFragment(Fragment targetFragment) {
        mTargetFragment = targetFragment;
    }

    public TipInfo markTip(String targetView) {
        TipInfo result = null;
        do {
            if (!mHasTip) {
                break;
            }

            for (TipInfo t : mTips) {
                if (t.getViewId().equals(targetView)) {
                    this.markTip(t);
                    result = t;
                    break;
                }
            }

            if (result == null) {
                break;
            }

            if (isShowing()) {
                this.remove();
            }

        } while (false);

        return result;
    }

    public boolean showTip() {
        boolean result = false;
        do {
            TipInfo oldTip = mTip;
            mTip = this.nextAvailableTip();

            if (mTip == null) {
                //make sure next time to show missed tips;
                mTipCursor = 0;
                break;
            }

            if (!mTip.equals(oldTip)) {
                this.remove();
            }

            if (mTipView == null) {
                mTipView = new TipView.Builder(mTarget, mTargetFragment, mTip)
                        .setTipViewEventTrace(mTipViewEventTrace)
                        .build();
            }

            this.show();

            result = true;
        } while (false);

        return result;
    }

    @Override
    public void remove() {
        if (mTipView != null) {
            mTipView.remove();
            mTipView = null;
        }
    }

    @Override
    public boolean isShowing() {
        if (mTipView != null) {
            return mTipView.isShowing();
        }
        return false;
    }

    @Override
    public void show() {
        if (mTipView != null) {
            mTipView.show();
        }
    }

    @Override
    public void hide() {
        if (mTipView != null) {
            mTipView.hide();
        }
    }

    public boolean isHidden() {
        return (mTipView != null);
    }


    public boolean hasTip() {
        return mHasTip;
    }

    public void resetCursor() {
        mTipCursor = 0;
    }

    public TipInfo nextAvailableTip() {
        TipInfo tip = nextUnmarkedTip();
        if (tip != null) {
            TargetView targetView = new TargetView(tip.getViewId(), mTarget, mTargetFragment, tip.isFragment());
            if (!targetView.isTotallyVisible()) {
                tip = nextAvailableTip();
            }
        }
        return tip;
    }


    /**
     * find an unmarked tip from tip list
     *
     * @return null when there is not any matched
     */
    public TipInfo nextUnmarkedTip() {
        TipInfo tip = null;
        do {
            if (!mHasTip) {
                break;
            }

            if (mTipCursor >= mTips.size()) {
                break;
            }

            tip = mTips.get(mTipCursor);
            mTipCursor++;

            if (tip.isMarked()) {
                tip = this.nextUnmarkedTip();
                break;
            }

        } while (false);

        return tip;
    }

    public TipInfo mark() {
        this.markTip(mTip);
        return mTip;
    }

    public TipInfo getTip() {
        return mTip;
    }
    

    private void markTip(TipInfo tipInfo) {
        if (!tipInfo.isMarked()) {
            //marked
            tipInfo.setMarked(true);
            //marked in file
            TipHelper.mark(mTarget, tipInfo);
        }
    }
}