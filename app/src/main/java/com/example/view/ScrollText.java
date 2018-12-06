package com.example.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.item.LinkInfo;
import com.example.model.StateFaceModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ScrollText extends TextView {

    public static final int TEXT_TIMER = 100;
    private float textLength = 0f;
    private float viewWidth = 0f;
    private float step = 0f;
    private float y = 0f;
    private float temp_view_plus_text_length = 0.0f;
    private float temp_view_plus_two_text_length = 0.0f;
    private boolean isStarting = false;
    private int left = 0;
    private int right = 0;
    private Paint paint = null;
    private String text = "";
    private Bitmap txtBmp;
    private Canvas txtCanvas;
    private FontMetrics fontMetrics;
    private Timer timer = new Timer();
    private ArrayList<LinkInfo> stateList;

    Handler handler;

    TimerTask task = new TimerTask() {
        public void run() {
            if (handler != null && isStarting) {
                Message msg = Message.obtain();
                msg.what = TEXT_TIMER;
                handler.sendMessage(msg);
            }
        }
    };

    public ScrollText(Context context) {
        super(context);
    }

    public ScrollText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void init(WindowManager windowManager, Handler handler) {
        try {
            this.handler = handler;
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Style.STROKE);
            paint.setTextSize(getTextSize());
            paint.setColor(getCurrentTextColor());
            text = getText().toString();
            textLength = 0;

            int len = stateList.size();
            for (int i = 0; i < len; i++) {
                LinkInfo info = stateList.get(i);
                if (info.isFace()) {
                    Bitmap faceBmp = StateFaceModel.getInstance()
                            .getSmallFaceIcon(info.getContent());
                    int xLen = faceBmp.getWidth();
                    textLength += xLen + 4;
                    continue;
                }
                String strContent = info.getContent();
                float xLen = paint.measureText(strContent);
                textLength += xLen;
            }
            left = this.getPaddingLeft();
            right = this.getPaddingRight();
            step = textLength;
            fontMetrics = paint.getFontMetrics();

            y = getPaddingTop();
            txtBmp = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void scrollText() {
        if (!isStarting) {
            return;
        }
        invalidate();
        if (viewWidth < textLength) {
            step += 0.5;
            if (step > temp_view_plus_two_text_length) {
                step = textLength;
            }
        }
    }

    public void setStateList(ArrayList<LinkInfo> stateList) {
        this.stateList = stateList;
    }

    private void setTxtBmp() {
        if (txtBmp == null && fontMetrics != null) {
            y = -paint.ascent();
            viewWidth = getWidth() - left - right;
            temp_view_plus_text_length = viewWidth + textLength;
            temp_view_plus_two_text_length = viewWidth + textLength * 2;
            txtCanvas = new Canvas();

            int width = (int) viewWidth;
            float height = getHeight();
            txtBmp = Bitmap.createBitmap(width, (int) height, Config.ARGB_8888);
            txtCanvas.setBitmap(txtBmp);

        }
    }


    public void startScroll() {
        isStarting = true;
    }


    public void stopScroll() {
        isStarting = false;
    }

    public void start() {
        timer.schedule(task, 10, 20);
    }

    public void stop() {
        timer.cancel();
    }

    @Override
    public void onDraw(Canvas canvas) {
        try {
            //初始化一个txtCanvas，里面放txtBmp的bitmap
            setTxtBmp();
            if (txtBmp == null) {
                return;
            }
            Paint txtPaint = new Paint();
            txtPaint.setColor(Color.WHITE);
            txtPaint.setStyle(Style.FILL);
            txtCanvas.drawRect(0, 0, txtBmp.getWidth(), txtBmp.getHeight(),
                    txtPaint);
            txtPaint.setAntiAlias(true);
            txtPaint.setStyle(Style.STROKE);
            txtPaint.setTextSize(getTextSize());
            txtPaint.setColor(getCurrentTextColor());
            float x = 0;
            if (viewWidth < textLength) {
                x = temp_view_plus_text_length - step;
            }
            int len = stateList.size();
            float curLen = x;
            for (int i = 0; i < len; i++) {
                LinkInfo info = stateList.get(i);
                if (info.isFace()) {
                    Bitmap faceBmp = StateFaceModel.getInstance()
                            .getSmallFaceIcon(info.getContent());
                    int xLen = faceBmp.getWidth();
                    txtCanvas.drawBitmap(faceBmp, curLen + 2, 0, txtPaint);
                    curLen += xLen + 4;
                    continue;
                }
                String strContent = info.getContent();
                strContent = strContent.replaceAll("\n", " ");
                float xLen = txtPaint.measureText(strContent);
                txtCanvas.drawText(strContent, curLen, y, txtPaint);
                curLen += xLen;
            }
            canvas.drawBitmap(txtBmp, left, 0, paint);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        txtBmp = null;
        setTxtBmp();
    }
}
