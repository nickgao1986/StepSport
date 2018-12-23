package com.ringcentral.android.utils.ui.menu;

import java.util.ArrayList;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.pic.optimize.R;

public class RCMainTitleBar extends RCBaseTitleBar implements
		View.OnClickListener {
	private Button mBtnRight = null;
	private ImageButton mBtnRightImg = null;
	private Button mBtnTopLeft = null;
	private ImageView mBtnDropDownImageview;

	private boolean mIsShowDialog = false;
    private boolean mIsInAnimation;
    private boolean mFinishShowDialog = false;

	public RCMainTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public RCMainTitleBar(Context context) {
		super(context, null);
		init(context, null);
	}

	@Override
	protected void init(Context context, AttributeSet attrs) {
		View mainView = inflate(context,
				R.layout.profile_titlebar_with_rightbutton, this);
		mTitleView = (TextView) mainView.findViewById(R.id.title);
		mBtnTopLeft = (Button) mainView.findViewById(R.id.btnTopLeft);
		mHeaderPhotoView = (HeaderPhotoView) mainView.findViewById(R.id.photo);
		ProfileOnClickListener profileOnClickListener = new ProfileOnClickListener();
		mBtnRightImg = (ImageButton) mainView
				.findViewById(R.id.btnTopRightImage);
		mBtnRight = (Button) mainView.findViewById(R.id.btnTopRight);
		mBtnRightImg.setOnClickListener(this);
		mBtnTopLeft.setOnClickListener(this);
		mBtnRight.setOnClickListener(this);
		mBtnTopLeft.setVisibility(View.GONE);
		// setBackgroundResource(R.color.bgTitleBar);

		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.TitleBar);
		String titleText = a.getString(R.styleable.TitleBar_title_text);
		String rightButtonText = a
				.getString(R.styleable.TitleBar_title_right_button_label);
		Drawable rightButtonDrawable = a
				.getDrawable(R.styleable.TitleBar_title_right_button_drawable);
		a.recycle();

		if (null != rightButtonText) {
			mBtnRight.setText(rightButtonText);
			mBtnRight.setVisibility(View.VISIBLE);
			mBtnRight.setOnClickListener(this);
		}

		if (null != titleText) {
			mTitleView.setText(titleText);
		}

		if (null != rightButtonDrawable) {
			mBtnRight.setVisibility(GONE);
			mBtnRightImg.setVisibility(VISIBLE);
			mBtnRightImg.setImageDrawable(rightButtonDrawable);
			mBtnRightImg.setOnClickListener(this);
		}

		mTitleView.setText("All Messages");
		mBtnDropDownImageview = (ImageView) findViewById(R.id.btn_drop_down_imageview);
		
		LinearLayout title_layout = (LinearLayout)findViewById(R.id.title_layout);
		title_layout.setOnClickListener(this);
	}

	@Override
	public void setLeftText(int stringId) {
		mHeaderPhotoView.setVisibility(View.GONE);
		mBtnTopLeft.setVisibility(View.VISIBLE);
		mBtnTopLeft.setText(stringId);
	}

	@Override
	public void showProfile() {
		mBtnTopLeft.setVisibility(View.GONE);
		mHeaderPhotoView.setVisibility(View.VISIBLE);
	}

	@Override
	public void setRightText(int stringId) {
		mBtnRightImg.setVisibility(View.GONE);
		mBtnRight.setVisibility(View.VISIBLE);
		mBtnRight.setText(stringId);
	}

	@Override
	public void setRightImageRes(int resId) {
		mBtnRightImg.setVisibility(View.VISIBLE);
		mBtnRight.setVisibility(View.GONE);
		mBtnRightImg.setImageDrawable(getContext().getResources().getDrawable(
				resId));
	}

	@Override
	public void setRightImageVisibility(int visibility) {
		mBtnRightImg.setVisibility(visibility);
		if (VISIBLE == mBtnRightImg.getVisibility()) {
			mBtnRightImg.setOnClickListener(this);
		} else {
			mBtnRightImg.setOnClickListener(null);
		}
	}

	@Override
	public void setRightVisibility(int visibility) {
		mBtnRight.setVisibility(visibility);
		if (VISIBLE == mBtnRight.getVisibility()) {
			mBtnRight.setOnClickListener(this);
		} else {
			mBtnRight.setOnClickListener(null);
		}
	}

	@Override
	public void setRightBtnEnabled(boolean flag) {
		mBtnRight.setEnabled(flag);
	}

	@Override
	public void setRightImageBtnEnabled(boolean flag) {
		mBtnRightImg.setEnabled(flag);
	}

	@Override
	public void onClick(View v) {
		if (null == mHeaderClickListener) {
			return;
		}
		switch (v.getId()) {
		case R.id.btnTopLeft:
			mHeaderClickListener.onLeftButtonClicked();
			break;
		case R.id.photo:
			break;
		case R.id.btnTopRight:
		case R.id.btnTopRightImage:
			mHeaderClickListener.onRightButtonClicked();
			break;
		}

	}
    
    
    private void finishShowGridView() {
        mFinishShowDialog = true;
    }


}
