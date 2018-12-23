package com.ringcentral.android.utils.ui.menu;

import java.util.List;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pic.optimize.R;
import com.pic.optimize.tab.RCTabItem;

public class RCBottomTabMenu extends RCBottomMenu {

	private static final String TAG = "RCBottomTabMenu";
	private static final int SPLIT_INDEX = 1;
	private LinearLayout mLeftLayout;
	private LinearLayout mRightLayout;
	private ImageView mPlusBtn;
	private int mPadding = 0;
	private RCTabView mMessageItem;
	private OnTabClickListener mOnTabClickListener;

	public RCBottomTabMenu(Context context) {
		super(context);
		init(context);
	}

	public RCBottomTabMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setOnTabClickListener(OnTabClickListener listener) {
		mOnTabClickListener = listener;
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.ringcentral_bottom, this);
		this.setBackgroundColor(getResources().getColor(
				R.color.transparentColor));
		mPadding = context.getResources().getDimensionPixelSize(
				R.dimen.tab_item_padding);
		mPlusBtn = (ImageView) findViewById(R.id.btn_plus);
		View plusLayout = findViewById(R.id.layout_plus);
		plusLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnTabClickListener.onPlusClick(false);
			}
		});
	}

	public void rotatePlusButton(Context context, boolean isUp) {
		AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(
				context, isUp ? R.anim.flip_down : R.anim.flip_up);
		animatorSet.setTarget(mPlusBtn);
		animatorSet.start();
	}

	public void addTabs(Context context, List<RCTabItem> list) {
		mLeftLayout = (LinearLayout) findViewById(R.id.left_layout);
		mRightLayout = (LinearLayout) findViewById(R.id.right_layout);
		mLeftLayout.removeAllViews();
		mRightLayout.removeAllViews();
		addMobileTabs(context, list);

	}

	private final OnClickListener mTabClickListener = new OnClickListener() {
		public void onClick(View view) {
			final int newSelected = ((RCTabView)view).getPosition();
			mOnTabClickListener.onTabClick(newSelected);
		}
	};

	private void addMobileTabs(Context context, List<RCTabItem> list) {
		for (int i = 0; i < BOTTOM_MENU_TAB_SUM; i++) {
			RCTabItem tabItem = list.get(i);
			RCTabView view = new RCTabView(context, tabItem.getItemId());
			view.setOnClickListener(mTabClickListener);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
					ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
			params.gravity = CENTER_IN_PARENT;
			view.setLayoutParams(params);
			TextView textView = (TextView) view
					.findViewById(R.id.tab_main_text);
			textView.setText(tabItem.getItemText());
			Drawable drawable = context.getResources().getDrawable(
					tabItem.getItemIcon());
			if (drawable != null) {
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
			}
			textView.setCompoundDrawables(null, drawable, null, null);
			if (i <= SPLIT_INDEX) {
				mLeftLayout.addView(view);
			} else {
				mRightLayout.addView(view);
			}
		}
	}

	public void setItemSelectedState(int selectedItem) {
		final int leftCount = mLeftLayout.getChildCount();
		for (int i = 0; i < leftCount; i++) {
			final RCTabView tabItem = (RCTabView) mLeftLayout.getChildAt(i);
			if (selectedItem != tabItem.getPosition()) {
				tabItem.changeSelectedState(false);
			} else {
				tabItem.changeSelectedState(true);
			}
		}
		final int rightCount = leftCount + mRightLayout.getChildCount();
		for (int i = leftCount; i < rightCount; i++) {
			final RCTabView tabItem = (RCTabView) mRightLayout.getChildAt(i
					- leftCount);
			if (selectedItem != tabItem.getPosition()) {
				tabItem.changeSelectedState(false);
			} else {
				tabItem.changeSelectedState(true);
			}
		}
	}

}
