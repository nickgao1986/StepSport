package com.example.tutorial;

import android.app.Activity;
import android.support.v4.app.Fragment;

public abstract class TipPresenter implements TipTouchEventInterface, TipViewEventTrace, PermissionChangedListener{

	
    protected TipManager mTipManager;
    private PermissionObserver mPermissionObserver;
    protected Fragment mTargetFragment;

    public TipPresenter(Activity target) {
        mTipManager = new TipManager(target, this);
        mPermissionObserver = new PermissionObserver(target, this);
        mPermissionObserver.register();
    }

    public TipManager getTipManager() {
        return mTipManager;
    }

    @Override
    public void onPermissionChanged() {
        mTipManager.onPermissionChanged();
    }

    /**
     * when tap the target view. Flurry log
     *
     * @param targetViewName
     */
    @Override
    public void onTipClickEvent(String targetViewName) {
        boolean isShowing = mTipManager.isShowing();
        //mark when ever is showing or not
        TipInfo tip = mTipManager.markTip(targetViewName);
        //flurry
        if (isShowing && tip != null && !tip.getEventName().isEmpty()) {
            //FlurryTypes.onEventWithOneParam(tip.getEventName(), FlurryTypes.PARAMETER_ACTION, FlurryTypes.PARAMETER_ACTION_VALUE_TAP_HOTSPOT);
        }
    }

    /**
     * when user tap close button. Flurry log
     */
    @Override
    public void onEventTapClose() {
        //mark
        TipInfo tip = mTipManager.mark();
        //remove the view
        mTipManager.remove();
        //flurry
        if (tip != null && !tip.getEventName().isEmpty()) {
          //  FlurryTypes.onEventWithOneParam(tip.getEventName(), FlurryTypes.PARAMETER_ACTION, FlurryTypes.PARAMETER_ACTION_VALUE_TAP_CLOSE);
        }
    }

    /**
     * when user tap tip, Flurry log.
     */
    @Override
    public void onEventTapTip() {
        //mark
        TipInfo tip = mTipManager.mark();
        //remove the view
        mTipManager.remove();
        //flurry
        if (tip != null && !tip.getEventName().isEmpty()) {
           // FlurryTypes.onEventWithOneParam(tip.getEventName(), FlurryTypes.PARAMETER_ACTION, FlurryTypes.PARAMETER_ACTION_VALUE_TAP_TOOLTIP);
        }
    }

    /**
     * when user tap to back. Flurry log
     *
     * @return
     */
    public boolean onHandleBackPressed() {
        boolean result = false;
        do {
            if (!mTipManager.hasTip()) {
                break;
            }

            if (!mTipManager.isShowing()) {
                break;
            }
            //mark
            TipInfo tip = mTipManager.mark();
            //remove the view
            mTipManager.remove();

            //flurry
            if (tip != null && !tip.getEventName().isEmpty()) {
           //     FlurryTypes.onEventWithOneParam(tip.getEventName(), FlurryTypes.PARAMETER_ACTION, FlurryTypes.PARAMETER_ACTION_VALUE_TAP_HARDWARE_BACK);
            }

            result = true;
        } while (false);

        return result;
    }

    public final void onDestroy() {
        if (mPermissionObserver != null) {
            mPermissionObserver.unregister();
        }
    }

    public boolean isShowing() {
        return mTipManager.isShowing();
    }
    

	@Override
	public void onEventHide() {
		
	}

	@Override
	public void onEventShow() {
		
	}

	
    /**
     * when activity called finish() method.
     */
    public abstract void onHandleFinish();

    /**
     * when activity onResume.
     */
    public abstract void onHandleResume();

    /**
     * when activity onPause.
     */
    public abstract void onHandlePause();

    /**
     * when any touch event happens.
     */
    public abstract void onHandleTouch();

    public abstract void setTargetFragment(Fragment targetFragment);
}
