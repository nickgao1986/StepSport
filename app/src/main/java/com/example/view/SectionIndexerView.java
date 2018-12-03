package com.example.view;

import com.example.shoplistdownload.ContactsList.SectionTitle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.pic.optimize.R;

public class SectionIndexerView extends View{

    public static final String TAG = SectionIndexerView.class.getSimpleName();
    private static final boolean DEBUG = true;
    private Paint mTextPaint;
    private int mViewWidth = 0;
    private int mViewHeight = 0;
    private float mPerTextHeight;
    private boolean mIsPressed = false;
    private ListView mListview = null;
    private SectionIndexer mIndexer;
    private TextView mView = null;
    private String[] mSectionIndexerText = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","#" };
    private static final float TEXTSIZE_RATIO = 0.84f;
    private static final int CHAR_WIDTH = 12;
    private static final int BG_ALPHA = 153;
    private static final float BG_RADIAN = 20f;
    private int mCharwidth = CHAR_WIDTH;
    private Context mContext = null;
    

    public SectionIndexerView(Context context) {
        super(context);
        init(context);
    }


    public SectionIndexerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        mCharwidth = (int) (mCharwidth * mContext.getResources().getDisplayMetrics().density);
        this.mTextPaint = new Paint();
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setColor(-1);
    }
    
    

    public void init(ListView listview, SectionIndexer indexer, TextView view) {
        this.mListview = listview;
        this.mIndexer = indexer;
        this.mView = view;
    }
    
    
    @Override
    protected void onDraw(Canvas paramCanvas) {
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        this.mTextPaint.setColor(Color.parseColor("#75797d"));
        if (this.mIsPressed) {
            localPaint.setColor(Color.parseColor("#838a98"));
            localPaint.setAlpha(BG_ALPHA);
        } else {
            localPaint.setAlpha(0);
        }
       
        this.mTextPaint.setTextSize(this.mViewHeight * TEXTSIZE_RATIO / mSectionIndexerText.length);
        System.out.println("==textSize="+(this.mViewHeight * TEXTSIZE_RATIO / mSectionIndexerText.length));
        System.out.println("====mViewWidth="+mViewWidth+"mCharWidth="+mCharwidth);
        //paramCanvas.drawRoundRect�ǻ�����,BG_RADIANΪ�����Ļ���
        paramCanvas.drawRoundRect(new RectF(50, 0.0F, this.mViewWidth,
                this.mViewHeight), BG_RADIAN, BG_RADIAN, 
                localPaint);
        
        int textPointX = 140;
        
        float textPointY = (this.mPerTextHeight - this.mTextPaint.ascent()) / 2.0F;
        
        int sectionslength = this.mSectionIndexerText.length;
        int currentSection = 0;
        int currentHeight = 0;
        while (true) {
            if (currentSection >= sectionslength) {
                break;
            }

            paramCanvas.drawText(
                    this.mSectionIndexerText[currentSection],
                    textPointX
                            + (mCharwidth - (int) this.mTextPaint
                                    .measureText(this.mSectionIndexerText[currentSection])) / 2,
                    textPointY + (3.0F + currentHeight * this.mPerTextHeight), this.mTextPaint);// SUPPRESS CHECKSTYLE : magic number
            ++currentHeight;
            ++currentSection;
        }
        
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        this.mViewWidth = width;

        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        this.mViewHeight = height;
        setMeasuredDimension(this.mViewWidth, this.mViewHeight);
        this.mPerTextHeight = (this.mViewHeight / this.mSectionIndexerText.length);
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean returnvalue = false;
        switch (motionEvent.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (DEBUG) {
                Log.d(TAG, "action down!");
            }
            mListview.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
            returnvalue = processTouchEvent(motionEvent);
            break;
        case MotionEvent.ACTION_MOVE:
            if (DEBUG) {
                Log.d(TAG, "action move!");
            }
            returnvalue = processTouchEvent(motionEvent);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if (DEBUG) {
                Log.d(TAG, "action up!");
            }
            setPressed(false);
            mView.setText("");
            mView.setVisibility(View.GONE);
            returnvalue = true;
            
            break;
        default:
            break;
        }
        invalidate();
        return returnvalue;
    }


    private boolean processTouchEvent(MotionEvent motionEvent) {
        String selcected = "";

        int backgroundPointX = this.mViewWidth - getPaddingRight() - mCharwidth * 3// SUPPRESS CHECKSTYLE 
                - getPaddingLeft();
        if (motionEvent.getX() < backgroundPointX) {
            mView.setVisibility(View.GONE);
            setPressed(false);
            return false;
        }
        setPressed(true);
        int curruntSection = (int) (motionEvent.getY() / this.mPerTextHeight);
        
        if (curruntSection >= 0 && curruntSection <= mSectionIndexerText.length - 1) {
            selcected = mSectionIndexerText[curruntSection];
            mView.setVisibility(View.VISIBLE);
        } else {
            if (curruntSection < 0) {
                selcected = mSectionIndexerText[0];
            } else if (curruntSection > mSectionIndexerText.length) {
                selcected = mSectionIndexerText[mSectionIndexerText.length - 1];
            }
            mView.setVisibility(View.GONE);
        }
        boolean foundedPosition = false;
        for (int i = 0; i < mIndexer.getSections().length; i++) {
        	SectionTitle title = (SectionTitle)mIndexer.getSections()[i];
            if (selcected.equals(title.title)) {
                mListview.setSelection(mIndexer.getPositionForSection(i));
                foundedPosition = true;
                break;
            }
        }

        if (foundedPosition) {
            mView.setBackgroundResource(R.drawable.section_text_bg);
            mView.setText(selcected);
        } else {
            mView.setBackgroundResource(R.drawable.section_text_gray_bg);
            mView.setText(selcected);
        }
        return true;
    }
    @Override
    public void setPressed(boolean pressed) {
        this.mIsPressed = pressed;
    }

}
