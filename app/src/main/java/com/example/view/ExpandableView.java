package com.example.view;

import java.util.List;

import com.pic.optimize.R;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ExpandableView extends LinearLayout {

	private int mScreenWidth;
	private int mScreenHeight;
	private int mButtonWidth;
	private int mButtonHeight;
	private LinearLayout mLayoutShow;
	private LinearLayout mLayoutHide;
	private ExpandItemClickListener onExpandItemClickListener;
	private int mMaxNum;
	private boolean mIsInAnimation = false;
	private boolean mCanMoveToBottom = false;
	private View mMoreView;
	private int mOldBottom;
	private Context mContext;
	private int mHeaderHeight;
	private double mAverageSize;
	private boolean mIsFirstLayout= true;
	private LinearLayout.LayoutParams mParams;
	private boolean mRestart = false;
	private LayoutInflater mInflater;
	public static final int LAUNCHER_BAR_LINE_ONE = 0;
	public static final int LAUNCHER_BAR_LINE_TWO = 1;
	
	private static final boolean LAUNCHER_HIDE_LAYOUT_TAG = true;

	
	public ExpandableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	public void setScreenSize(int height, int width) {
		mScreenHeight = height;
		mScreenWidth = width;
		initParameter();
		initSubView();
	}
	
	private void initParameter(){
		mButtonWidth = mContext.getResources().getDimensionPixelSize(R.dimen.expand_item_minwidth);
		mButtonHeight = mContext.getResources().getDimensionPixelSize(R.dimen.expand_item_minheight);
		mHeaderHeight = mContext.getResources().getDimensionPixelSize(R.dimen.header_bar_height);
		mMaxNum = mScreenWidth / mButtonWidth;
		mAverageSize = ((double) mScreenWidth) / mMaxNum;
		this.setOrientation(LinearLayout.VERTICAL);
	}

	public void addListView(List<View> views) {
		setPortraitView(views);
		addView(mLayoutShow);
		addView(mLayoutHide);

	}
	
	private void initSubView() {
		mMoreView = mInflater.inflate(R.layout.launch_bar_item12, null).findViewById(R.id.launcher_content);
		mMoreView.setBackgroundResource(R.drawable.btn_launcher_more);
		mMoreView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRestart = false;
				onExpandItemClickListener.onExpandItemClick(mLayoutShow, v, mMaxNum, mMaxNum, LAUNCHER_BAR_LINE_ONE);
			}
		});
		
		mLayoutShow = new LinearLayout(mContext);
		mLayoutHide = new LinearLayout(mContext);
		mLayoutHide.setTag(LAUNCHER_HIDE_LAYOUT_TAG);
		mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, mButtonHeight);
		mLayoutShow.setOrientation(LinearLayout.HORIZONTAL);
		mLayoutHide.setOrientation(LinearLayout.HORIZONTAL);
		mLayoutShow.setBackgroundResource(R.drawable.bg_launcher_port_nor);
		mLayoutHide.setBackgroundResource(R.drawable.bg_launcher_port_nor);
		mLayoutShow.setLayoutParams(mParams);
		mLayoutHide.setLayoutParams(mParams);
	}
	
	
	
	private void setPortraitView(List<View> views) {
		if (views.size() > mMaxNum) {
			mLayoutShow = adjustPortraitView(mLayoutShow, 0, mMaxNum - 1, views, true);
			mLayoutHide = adjustPortraitView(mLayoutHide, mMaxNum - 1, views.size(), views, false);
		} else {
			mLayoutHide.setVisibility(View.GONE);
			mLayoutShow = adjustPortraitView(mLayoutShow, 0, views.size(), views, false);
		}
	}
	
	private LinearLayout adjustPortraitView(LinearLayout layout, int start, int end, List<View> views, boolean isAddMore) {
		for (int i = start; i < end; i++) {
			final View view = views.get(i);
			view.setLayoutParams(new LayoutParams((int) mAverageSize, LayoutParams.FILL_PARENT));
			view.setTag(i);
			setClick(layout, view, LAUNCHER_BAR_LINE_ONE);
			layout.addView(view);
		}
		if (isAddMore) {
			mMoreView.setLayoutParams(new LayoutParams((int)mAverageSize, LayoutParams.FILL_PARENT));
			mLayoutShow.addView(mMoreView);
		}
		return layout;
	}
	
	private void setClick(final LinearLayout layout, final View view, final int line) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRestart = false;
				if (onExpandItemClickListener != null) {
					onExpandItemClickListener.onExpandItemClick(layout, view, (Integer) view.getTag(), mMaxNum, line);
				}
			}
		});
	}
	
	
	public boolean isInAnimation() {
		return mIsInAnimation;
	}
	
	public boolean isCanMoveToBottom(){
		if (mLayoutHide != null && mLayoutHide.getVisibility() == View.VISIBLE) {
			int bottom = this.getBottom();
			if (bottom > mOldBottom) {
				mCanMoveToBottom = false;
			} else if(bottom <= mOldBottom) {
				mCanMoveToBottom = true;
			}
		}
		return mCanMoveToBottom;
	}
	
	public void excuteAnimation(boolean isClickMore) {
		if (mLayoutHide != null && mLayoutHide.getVisibility() == View.VISIBLE) {
			if (!mIsInAnimation) {
				if (isCanMoveToBottom()) {
					mIsInAnimation = true;
					mMoreView.setSelected(false);

					this.startAnimation(getAnimation(true, 0, 0, 0, mButtonHeight));
					mCanMoveToBottom = false;
				} else {
					if (isClickMore) {
						mIsInAnimation = true;
						mMoreView.setSelected(true);

						this.startAnimation(getAnimation(false, 0, 0, 0, -mButtonHeight));
						mCanMoveToBottom = true;
					}
				}
			}
		}
	}
	
	private Animation getAnimation(final boolean moToBottom, int fromXDelta, final int toXDelta,
			int fromYDelta, final int toYDelta) {
		TranslateAnimation animation = new TranslateAnimation(fromXDelta,
				toXDelta, fromYDelta, toYDelta);
		animation.setDuration(200);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				ExpandableView.this.clearAnimation();
				ExpandableView.this.layout(ExpandableView.this.getLeft(),
						(ExpandableView.this.getTop() + toYDelta),
						ExpandableView.this.getRight(),
						(ExpandableView.this.getBottom() + toYDelta));
				mIsInAnimation = false;
			}
		});
		return animation;
	}
	
	public void setOnExpandItemClickListener(ExpandItemClickListener listener) {
		onExpandItemClickListener = listener;
	}

	public interface ExpandItemClickListener {
		void onExpandItemClick(final View parentView, View view, int position, int maxNum, int line);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mLayoutHide != null && mLayoutHide.getVisibility() == View.VISIBLE) {
			if(mIsFirstLayout && this.getWidth() > 0){
				mIsFirstLayout = false;
				mOldBottom = this.getBottom();
				this.layout(this.getLeft(), (this.getTop() + mHeaderHeight), this.getRight(), (this.getBottom() + mHeaderHeight));
			}

			if (mRestart && this.getBottom() <= mOldBottom && !mIsFirstLayout) {
				this.layout(this.getLeft(), (this.getTop() + mHeaderHeight), this.getRight(), (this.getBottom() + mHeaderHeight));
			}
		}
	}
	
	public void setRestart(boolean restart){
		mRestart = restart;
	}
	
}
