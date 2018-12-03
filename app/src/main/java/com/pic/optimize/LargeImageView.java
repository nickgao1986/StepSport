package com.pic.optimize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.io.IOException;
import java.io.InputStream;

public class LargeImageView extends View{
    private static final String TAG = "LargeImageView";

    private BitmapRegionDecoder mDecoder;

    //绘制的区域
    private volatile Rect mRect = new Rect();

    // 分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    //图片的宽度和高度
    private int mImageWidth, mImageHeight;
    private BitmapFactory.Options options;
    private Context mContext;
    private int mTouchSlopSquare;


    public LargeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        int touchSlop = ViewConfiguration.getTouchSlop();
        mTouchSlopSquare = touchSlop * touchSlop;

        //设置显示图片的参数，如果对图片质量有要求，就选择ARGB_8888模式
        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    /**
     * 把inputStream传入控件，并初始化bitmapReginDecoder
     * @param is
     */
    public void setInputStream(InputStream is) throws IOException{

        //获取图片的宽高
        BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
        // 设置为true则只获取图片的宽高等信息，不加载进内存
        tmpOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, tmpOptions);
        mImageWidth = tmpOptions.outWidth;
        mImageHeight = tmpOptions.outHeight;

        //初始化BitmapRegionDecode，并用它来显示图片
        mDecoder = BitmapRegionDecoder.newInstance(is,false);

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int)event.getRawY();
                mLastX = (int)event.getRawX();
                break;
             case MotionEvent.ACTION_MOVE:
                 int x = (int)event.getRawX();
                 int y = (int)event.getRawY();
                 final int deltaX = (x-mLastX);
                 final int deltaY = (y - mLastY);
                 int distance = deltaX*deltaX + deltaY*deltaY;
                 if(distance > mTouchSlopSquare) {
                     move(x,y);
                 }
                break;

        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //显示图片
        Bitmap bitmap = mDecoder.decodeRegion(mRect,options);
        canvas.drawBitmap(bitmap,0,0,null);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int imageWidth = mImageWidth;
        int imageHeight = mImageHeight;

        //默认显示图片的中心区域
        mRect.left = imageWidth/2 - width/2;
        mRect.right = mRect.left+width;
        mRect.top = imageHeight/2 - height/2;
        mRect.bottom = mRect.top + height;
    }

    /**
     * 移动的时候更新图片显示的区域
     *
     * @param x
     * @param y
     */
    private void move(int x, int y) {
        int deltaX = x - mLastX;
        int deltaY = y - mLastY;
        Log.d(TAG, "move, deltaX:" + deltaX + " deltaY:" + deltaY);
        //如果图片宽度大于屏幕宽度
        if (mImageWidth > getWidth()) {
            //移动rect区域
            mRect.offset(-deltaX, 0);
            //检查是否到达图片最右端
            if (mRect.right > mImageWidth) {
                mRect.right = mImageWidth;
                mRect.left = mImageWidth - getWidth();
            }

            //检查左端
            if (mRect.left < 0) {
                mRect.left = 0;
                mRect.right = getWidth();
            }

            invalidate();
        }
        //如果图片高度大于屏幕高度
        if (mImageHeight > getHeight()) {
            mRect.offset(0, -deltaY);

            //是否到达最底部
            if (mRect.bottom > mImageHeight) {
                mRect.bottom = mImageHeight;
                mRect.top = mImageHeight - getHeight();
            }

            if (mRect.top < 0) {
                mRect.top = 0;
                mRect.bottom = getHeight();
            }
            //重绘
            invalidate();
        }


        mLastX = x;
        mLastY = y;
    }


}
