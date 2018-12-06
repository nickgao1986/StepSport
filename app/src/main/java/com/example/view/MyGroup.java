package com.example.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyGroup extends ViewGroup{

	private Scroller mScroller;
	private float mOriMotionX;
	private float mLastMotionX;
	private VelocityTracker mVelocityTracker;
	private int mTouchState = TOUCH_STATE_REST;
	private static final int TOUCH_STATE_REST = 0;
	private int mTouchSlop;
	private int mMaximumVelocity;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private float mLastDownX;
	private static final int DEFAULT_VALUE = 1000;
	private int mNextScreen = -1;
	private static final int SNAP_VELOCITY = 700;

	
	private int mCurrentScreen;
	public MyGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWorkspace();
	}
	
	private void initWorkspace() {
		mScroller = new Scroller(getContext());
		setCurrentScreen(0);

		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}
	

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int paddingleft = 0;
		int paddingTop = 0;
		int childLeft = paddingleft;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				final int childHeight = child.getMeasuredHeight() ;

				child.layout(childLeft, paddingTop, childLeft + childWidth,
						childHeight + paddingTop);
				childLeft += child.getMeasuredWidth();
			}
		}
		
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		int childCount = getChildCount();
		if (childCount == 0) {
			return;
		}


		boolean restore = false;
		int restoreCount = 0;

		final long drawingTime = getDrawingTime();
		final float scrollPos = (float) getScrollX() / getWidth();
		final int leftScreen = (int) scrollPos;
		final int rightScreen = leftScreen + 1;
		if (leftScreen >= 0 && leftScreen < childCount) {
			drawChild(canvas, getChildAt(leftScreen), drawingTime);
		}
		if  (rightScreen < getChildCount()) {
			drawChild(canvas, getChildAt(rightScreen), drawingTime);
		}

		if (restore) {
			canvas.restoreToCount(restoreCount);
		}

	}
	
	

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mNextScreen != -1) {
			setCurrentScreen(Math.max(0,
					Math.min(mNextScreen, getChildCount() - 1)));
			mNextScreen = -1;

//			if (mListener != null) {
//				mListener.onViewChanged(mCurrentScreen);
//			}

		}
	}
	void setCurrentScreen(int index) {
		mCurrentScreen = index;
		resetVisibilityForChildren();
	}
	
	private void resetVisibilityForChildren() {
	    int count = getChildCount();
	    for (int i = 0; i < count; i++) {
	        View child = getChildAt(i);
	        if (Math.abs(mCurrentScreen - i) <= 0) {
	            child.setVisibility(View.VISIBLE);
	        } else {
	            child.setVisibility(View.INVISIBLE);
	        }
	    }
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();

		switch (action) {
			case MotionEvent.ACTION_MOVE:
				final int xDiff = (int) Math.abs(x - mLastMotionX);
				final int touchSlop = mTouchSlop;
				boolean xMoved = xDiff > touchSlop;
				if (xMoved) {
					mTouchState = TOUCH_STATE_SCROLLING;
				}
				break;
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = x;

				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
						: TOUCH_STATE_SCROLLING;
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				mTouchState = TOUCH_STATE_REST;
				break;
			default:
				break;
			}
		
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		int mScrollX = this.getScrollX();

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mOriMotionX = x;
				mLastMotionX = x;
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
				mOriMotionX = x;
				mLastMotionX = x;
				mLastDownX = x;
				return true;
			case MotionEvent.ACTION_MOVE:
				System.out.println("====action move mScrollX="+mScrollX);
				final int buffer = getWidth() / 2;
				int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;
				System.out.println("=====deltaX="+deltaX);
				if (deltaX < 0) {
					scrollBy(Math.max(-mScrollX - buffer, deltaX), 0);
				}else{
					int availableToScroll = 0;
					if (getChildCount() > 0) {
						System.out.println("====rihgt="+(getChildAt(
			                            getChildCount() - 1).getRight())+"avail="+(getChildAt(
			                            getChildCount() - 1).getRight()- mScrollX - getWidth()
			                            ));
					    availableToScroll = getChildAt(
			                            getChildCount() - 1).getRight()
			                            - mScrollX - getWidth();
					    scrollBy(Math.min(availableToScroll + buffer, deltaX), 0);
					}
				}
				
			return true;
			case MotionEvent.ACTION_UP:
				final VelocityTracker velocityTracker = mVelocityTracker;

				velocityTracker.computeCurrentVelocity(DEFAULT_VALUE,
						mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();
				if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
					snapToScreen(mCurrentScreen - 1);
				} else if (velocityX < -SNAP_VELOCITY
						&& mCurrentScreen < getChildCount() - 1) {
					//  move right
					snapToScreen(mCurrentScreen + 1);
				} else {
					snapToDestination(mLastMotionX < mOriMotionX);
				}
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				mTouchState = TOUCH_STATE_REST;
				if (Math.abs(mLastDownX - x) > 10) {
					return true;
				}
				return false;
			case MotionEvent.ACTION_CANCEL:
				mTouchState = TOUCH_STATE_REST;
				return false;
			default:
				break;
			}

		return true;
	}

	public void snapToDestination(boolean forward) {
		final int screenWidth = getWidth();
		int scrollX = getScrollX();

		if (forward) {
		    scrollX += screenWidth - screenWidth / 3;
		} else { 
		    scrollX += screenWidth / 3;
		}
		System.out.println("======screenWidth="+screenWidth+"scrollX / screenWidth="+(scrollX / screenWidth));
		snapToScreen(scrollX / screenWidth);
	}
	

	public void snapToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		boolean changingScreens = whichScreen != mCurrentScreen;

		mNextScreen = whichScreen;
		int mScrollX = this.getScrollX();
		final int newX = whichScreen * getWidth();
		final int delta = newX - mScrollX;
		System.out.println("====snapToScreen delta="+delta);
		mScroller.startScroll(mScrollX, 0, delta, 0, Math.abs(delta) * 2);
		invalidate();
	}
}
