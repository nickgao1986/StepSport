package com.pic.optimize.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class MyDragLayer extends FrameLayout {

	/**
	 * The bitmap that is currently being dragged
	 */
	private Bitmap mDragBitmap = null;

	private float mLastMotionX;
	private float mLastMotionY;

	private float mOffsetX;
	private float mOffsetY;

	private static final int TRANSITION_DURATION = 250;

	public View mTrashBin;

	public View mDropTarget;

	private final Paint mTrashPaint = new Paint();
	private Paint mDragPaint;

	private TransitionDrawable mTransition;
	    
	public MyDragLayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyDragLayer(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public MyDragLayer(Context context) {
		super(context);
	}


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mLastMotionX = ev.getX();
        mLastMotionY = ev.getY();
        
        int dx = 0;
        int dy = 0;
        
        if (mDragBitmap != null) {
            dx = (int)(mLastMotionX - mOffsetX + mDragBitmap.getWidth() / 2);
            dy = (int)(mLastMotionY - mOffsetY + mDragBitmap.getHeight() / 2);
        }
        
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            boolean preIsTrash = mDropTarget == mTrashBin;
            mDropTarget = findDropTarget(dx, dy);
            boolean currentIsTrash = mDropTarget == mTrashBin;
            
            if (!preIsTrash && currentIsTrash) {
                mTransition.reverseTransition(TRANSITION_DURATION);
            } else if (preIsTrash && !currentIsTrash) {
                mTransition.reverseTransition(TRANSITION_DURATION);
            }
            
            if (currentIsTrash) {
                mDragPaint = mTrashPaint;
            } else {
                mDragPaint = null;
            }
            
        } else if (action == MotionEvent.ACTION_UP) {
            mDropTarget = findDropTarget(dx, dy);
            if (mDropTarget == mTrashBin) {
                
                invalidate();
                
                return true; //QuickNavGridView will receive Action_cancel 
            }
        }
        
        invalidate();
        
        
        boolean result = super.onInterceptTouchEvent(ev);
        

        
        return result;
    }
    
    private View findDropTarget(int x, int y) {
        if (mTrashBin != null && mTrashBin.getVisibility() == View.VISIBLE) {
            Rect r = new Rect();
            mTrashBin.getHitRect(r);
            
            if (r.contains(x, y)) {
                return mTrashBin;
            }
        }

        
        return null;
    }

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if (mDragBitmap != null && !mDragBitmap.isRecycled()) {
			// Draw actual icon being dragged
			canvas.drawBitmap(mDragBitmap, getScrollX() + mLastMotionX
					- mOffsetX, getScrollY() + mLastMotionY - mOffsetY,
					mDragPaint);
		}

	}

	public void startDrag(Bitmap bitmap, int offsetx, int offsety) {
		mDragBitmap = bitmap;

		mOffsetX = offsetx;
		mOffsetY = offsety;

		mDragPaint = null;

		invalidate();
	}
	
	public void setTrashBin(View view) {
		mTrashBin = view;
		mTransition = (TransitionDrawable) view.getBackground();
	}
}
