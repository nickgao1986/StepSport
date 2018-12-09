package com.pic.optimize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;
	private Paint mPaint;
	private Paint mOppoPaint;
	private Paint mOppositePaint;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private boolean isMirrorDraw = true;
	private Path mOppositePath;

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(8);
		
		mOppoPaint = new Paint();
		mOppoPaint.setAntiAlias(true);
		mOppoPaint.setDither(true);
		mOppoPaint.setColor(0xFF000000);
		mOppoPaint.setStyle(Paint.Style.STROKE);
		mOppoPaint.setStrokeJoin(Paint.Join.ROUND);
		mOppoPaint.setStrokeCap(Paint.Cap.ROUND);
		mOppoPaint.setStrokeWidth(8);
		mPath = new Path();
		mOppositePath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	}
	
//	public DrawView(Context c) {
//		super(c);
//		mPaint = new Paint();
//		mPaint.setAntiAlias(true);
//		mPaint.setDither(true);
//		mPaint.setColor(0xFFFF0000);
//		mPaint.setStyle(Paint.Style.STROKE);
//		mPaint.setStrokeJoin(Paint.Join.ROUND);
//		mPaint.setStrokeCap(Paint.Cap.ROUND);
//		mPaint.setStrokeWidth(8);
//		
//		mPath = new Path();
//		mOppositePath = new Path();
//		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
//	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	public void clear() {
		if (mCanvas != null) {
			mPath.reset();
			mOppositePath.reset();
			mCanvas.drawColor(0xFFAAAAAA);
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(0xFFAAAAAA);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath(mPath, mPaint);
		if(isMirrorDraw) {
			canvas.drawPath(mOppositePath, mOppoPaint);
		}
	}

	private float mX, mY;
	private float mOppositeX, mOppositeY;
	private static final float TOUCH_TOLERANCE = 4;
	
	public void turnOnOrOffMirrorDraw(boolean flag) {
		isMirrorDraw = flag;
	}

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		mCanvas.drawPath(mPath, mPaint);
		mPath.reset();
	}

	private void touch_opposite_up() {
		mOppositePath.lineTo(mOppositeX, mY);
		mCanvas.drawPath(mOppositePath, mOppoPaint);
		mOppositePath.reset();
	}

	private void touch_opposite_move(float x, float y) {
		float oppositeX = OppositeDrawActivity.screenWidth - x;
		float dx = Math.abs(oppositeX - mOppositeX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mOppositePath.quadTo(mOppositeX, mY, (mOppositeX + oppositeX) / 2, (y + mY) / 2);
			mOppositeX = oppositeX;
			mY = y;
		}
	}

	private void touch_opposite_start(float x, float y) {
		mOppositePath.reset();
		float oppositeX = OppositeDrawActivity.screenWidth - x;
		mOppositePath.moveTo(oppositeX, y);
		mOppositeX = oppositeX;

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			if(isMirrorDraw) {
				touch_opposite_start(x, y);
			}
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			if(isMirrorDraw) {
				touch_opposite_move(x, y);
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			if(isMirrorDraw) {
				touch_opposite_up();
			}
			invalidate();
			break;
		}
		return true;
	}
}