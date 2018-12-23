package com.ringcentral.android.utils.ui.menu;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class MenuCollapseAnimation extends ViewStateManager implements
		IAnimation {
	private IAnimationDelegate mAnimationDelegate;
	private boolean isInUpAnimation = false;
	private boolean isInDownAnimation = false;

	@Override
	public void setAnimationDelegate(IAnimationDelegate aniDelegate) {
		mAnimationDelegate = aniDelegate;
	}

	@Override
	public void open(final View view, int duration) {
		if (isInUpAnimation) {
			return;
		}

		if (mAnimationDelegate != null) {
			mAnimationDelegate.onAnimationStart(true);
		}

		view.setVisibility(View.VISIBLE);
		int height = view.getMeasuredHeight();
		if (height <= 0) {
			height = 500;
		}

		Animation animation;
		animation = new TranslateAnimation(0, 0, height, 0);
		animation.setFillAfter(true);
		animation.setDuration(duration);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				isInUpAnimation = true;
				isInDownAnimation = false;
				if (mAnimationDelegate != null) {
					mAnimationDelegate.onAnimationStart(true);
				}
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				if (mAnimationDelegate != null) {
					mAnimationDelegate.onAnimationEnd(0, false, true);
				}
				isInUpAnimation = false;
			}
		});
		view.startAnimation(animation);
	}


	@Override
	public void close(final View view, int duration, final int tab,
			final boolean isSwitch) {
		if (isInDownAnimation) {
			return;
		}
		Animation animation;
		animation = new TranslateAnimation(0, 0, 0, view.getMeasuredHeight());
		animation.setFillAfter(true);
		animation.setDuration(duration);
		animation.setStartOffset(0);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				isInDownAnimation = true;
				isInUpAnimation = false;
				if (mAnimationDelegate != null) {
					mAnimationDelegate.onAnimationStart(false);
				}
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				view.clearAnimation();
				view.setVisibility(View.GONE);
				if (mAnimationDelegate != null) {
					mAnimationDelegate.onAnimationEnd(tab, isSwitch, false);
				}
				isInDownAnimation = false;
			}
		});
		view.startAnimation(animation);
	}
}
