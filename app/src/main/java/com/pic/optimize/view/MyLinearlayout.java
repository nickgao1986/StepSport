package com.pic.optimize.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pic.optimize.R;

public class MyLinearlayout extends RelativeLayout {

	private View mDragView;
	private Bitmap mDragBitmap = null;
	private int mBitmapOffsetX;
	private int mBitmapOffsetY;
	private boolean mDragging = false;
	public MyDragLayer mDragLayer;
    /**
     * X offset from where we touched on the cell to its upper-left corner
     */
    private float mTouchOffsetX;

    /**
     * Y offset from where we touched on the cell to its upper-left corner
     */
    private float mTouchOffsetY;
    private static final float DRAG_SCALE = 18.0f;

	private float mLastMotionX;
	private float mLastMotionY;
	private ImageView mView;
	
	public MyLinearlayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(context, R.layout.my_linear_layout, this);
		init(context);
	}

	private void init(Context context) {
		final int top = this.getResources().getDimensionPixelSize(R.dimen.top);
		final ImageView image1 = (ImageView) findViewById(R.id.item1);
		final ImageView image2 = (ImageView) findViewById(R.id.item2);
		final int x = 50;
		final int y = top;
		mView = image2;
		final int switch_to_left = 50;
		Button btn1 = (Button) findViewById(R.id.btn1);
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				TranslateAnimation trans = new TranslateAnimation(0,-switch_to_left,0,0);
				trans.setFillAfter(true);
				trans.setDuration(300);
				image1.startAnimation(trans);
			}
		});

		mView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				startDrag();
				return false;
			}
		});
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// System.out.println("onInterceptTouchEvent:" + ev);

		int action = ev.getAction();

		final float x = ev.getX();
		final float y = ev.getY();

		if (action == MotionEvent.ACTION_DOWN) {

			mLastMotionX = x;
			mLastMotionY = y;
			requestFocus();
		}
		if (mDragging) {
			return true;
		}

		return super.onInterceptTouchEvent(ev);
	}

	class MyTranslateAnimation extends TranslateAnimation {
		public int dstX;
		public int dstY;
		public View view;

		public MyTranslateAnimation(float fromXDelta, float toXDelta,
				float fromYDelta, float toYDelta) {
			super(fromXDelta, toXDelta, fromYDelta, toYDelta);
		}

	}

	public void startDrag() {
		View view = mView;
		if (view != null) {
			mDragView = view;
			mDragging = true;
			view.setVisibility(View.INVISIBLE);

			Rect r = new Rect();
			r.set(view.getScrollX(), view.getScrollY(), 0, 0);

			offsetDescendantRectToMyCoords(view, r);

			mTouchOffsetX = mLastMotionX - r.left;
			mTouchOffsetY = mLastMotionY - r.top;
			view.clearFocus();
			view.setPressed(false);

			boolean willNotCache = view.willNotCacheDrawing();
			view.setWillNotCacheDrawing(false);

			// Reset the drawing cache background color to fully transparent
			// for the duration of this operation
			int color = view.getDrawingCacheBackgroundColor();
			view.setDrawingCacheBackgroundColor(0);

			if (color != 0) {
				view.destroyDrawingCache();
			}
			
			view.buildDrawingCache();
			Bitmap viewBitmap = view.getDrawingCache();
			int width = viewBitmap.getWidth();
			int height = viewBitmap.getHeight();

			Matrix scale = new Matrix();
			float scaleFactor = view.getWidth();
			scaleFactor = (scaleFactor + DRAG_SCALE) / scaleFactor;
			scale.setScale(scaleFactor, scaleFactor);

			mDragBitmap = Bitmap.createBitmap(viewBitmap, 0, 0, width, height,
					scale, true);
			
			view.destroyDrawingCache();
			
			
			view.setWillNotCacheDrawing(willNotCache);
			view.setDrawingCacheBackgroundColor(color);

			final Bitmap dragBitmap = mDragBitmap;
			mBitmapOffsetX = (dragBitmap.getWidth() - width) / 2;
			mBitmapOffsetY = (dragBitmap.getHeight() - height) / 2;

			invalidate();

			mDragLayer.startDrag(mDragBitmap,
					(int) (mTouchOffsetX + mBitmapOffsetX),
					(int) (mTouchOffsetY + mBitmapOffsetY));

		}
	}
}
