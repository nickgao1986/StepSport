package com.example.tutorial;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;


public class TutorialActivity extends Activity implements
		TipTouchEventInterface, TutorialActivityInterface {
	private TipPresenter mTipPresenter;

	protected boolean isFragmentContainer() {
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTipPresenter = new TipCounterPresenter(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isFragmentContainer()) {
			mTipPresenter.onHandleResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mTipPresenter.onHandlePause();
	}

	@Override
	public void finish() {
		super.finish();
		mTipPresenter.onHandleFinish();
	}

	@Override
	protected void onDestroy() {
		mTipPresenter.onDestroy();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mTipPresenter.onHandleBackPressed()) {
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mTipPresenter.isShowing()) {
				return true;
			} else {
				return onKeyDownOpenMenu();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyDownOpenMenu() {
		return false;
	}

	@Override
	public void onTipClickEvent(String targetViewName) {
		mTipPresenter.onTipClickEvent(targetViewName);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mTipPresenter.onHandleTouch();
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public ViewGroup getViewRootForTip() {
		return ((ViewGroup) getWindow().getDecorView());
	}

	@Override
	public void onViewRootResume() {
		mTipPresenter.onHandleResume();
	}

	@Override
	public void onViewRootPause() {
		mTipPresenter.onHandlePause();
	}

	@Override
	public boolean isTipShowing() {
		return mTipPresenter.isShowing();
	}
}
