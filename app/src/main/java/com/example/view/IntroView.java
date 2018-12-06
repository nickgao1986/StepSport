package com.example.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.item.LinkInfo;
import com.example.model.StateFaceModel;
import com.pic.optimize.R;

import java.util.ArrayList;

public class IntroView extends TextView {

    private ArrayList<LinkInfo> titleList;
    private int displayWidth = 0;
    private float displayHeight = 0;
    private float curLen = 0;
    private Bitmap starBmp;
    private Bitmap selectedBmp;
    private float posX = 0;
    private float posY = 0;
    private LinkInfo curInfo;
    private OnClickLinkListener Listener;

    public void setOnClickLinkListener(OnClickLinkListener listener) {
        this.Listener = listener;
    }

    public interface OnClickLinkListener {
        public abstract void onClick(LinkInfo linkInfo);
    }

    private String mFaceType = MSG_FACE_TYPE;
    public static final String MSG_FACE_TYPE = "msgtype";
    public static final String STATUS_FACE_TYPE = "statustype";

    public IntroView(Context context) {
        super(context);
    }

    public IntroView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntroView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setTitleList(ArrayList<LinkInfo> titleList) {
        this.titleList = titleList;
        displayHeight = 0;
        requestLayout();
    }


    public void setDisplayWidth(int width) {
    }

    public void setSelectedBmp(Bitmap selectedBmp) {
        this.selectedBmp = selectedBmp;
    }

    public void setStarBmp(Bitmap starBmp) {
        this.starBmp = starBmp;
    }


    public void setFaceType(String faceType) {
        this.mFaceType = faceType;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (titleList == null || titleList.size() == 0) {
                return;
            }
            curLen = 0;
            Paint paint = new Paint();
            paint.setTextSize(getTextSize());
            paint.setAntiAlias(true);
            paint.setStyle(Style.FILL);
            float y = -paint.ascent();
            y = drawList(canvas, paint, titleList, y);
        } catch (Exception ex) {

        }
    }

    private float drawList(Canvas canvas, Paint paint, ArrayList<LinkInfo> infoList, float StartY) {
        if (infoList == null || infoList.size() == 0) {
            return StartY;
        }
        float y = StartY;
        float fontHeight = -paint.ascent() + paint.descent();//fontMetrics.bottom - fontMetrics.ascent;
        int len = infoList.size();
        int color = getCurrentTextColor();
        for (int i = 0; i < len; i++) {
            LinkInfo info = infoList.get(i);
            if (info.isFace()) {
                Bitmap faceBmp = null;
                if (mFaceType == MSG_FACE_TYPE) {
                    faceBmp = StateFaceModel.getInstance()
                            .getSmallFaceIcon(info.getContent());
                }
                if (faceBmp != null) {
                    int xLen = faceBmp.getWidth() + 4;
                    if (curLen + xLen >= displayWidth) {
                        y += fontHeight;
                        curLen = 0;
                    }
                    canvas.drawBitmap(faceBmp, curLen + 2, y - fontHeight + 4, paint);
                    curLen += xLen;
                }
                continue;
            }
            String strContent = info.getContent();

            if (mFaceType == MSG_FACE_TYPE && strContent.startsWith("\n")) {
                y += fontHeight;
                curLen = 0;
            }

            strContent = strContent.replaceAll("\n", " ");
            if ((info.isEmail() || info.isPhoneNumber() || info.isWebUrl()) && info.isSelected()) {
                paintSelectRectF(canvas, paint, info);
            }
            float xLen = paint.measureText(strContent);
            int starLen = 0;
            if (info.isCommonString()) {
                paint.setColor(color);
            } else {
                paint.setColor(getResources().getColor(R.drawable.blue1));
            }

            if (curLen + xLen + starLen >= displayWidth) {
                int lenStr = strContent.length();
                for (int j = 0; j < lenStr; j++) {
                    float width = paint.measureText(strContent, j, j + 1);
                    if (curLen + width >= displayWidth) {
                        y += fontHeight;
                        curLen = 0;
                    }
                    canvas.drawText(strContent, j, j + 1, curLen, y, paint);
                    curLen += width;
                }
            } else {
                canvas.drawText(strContent, curLen, y, paint);
                curLen += xLen;
            }
        }
        return y;
    }


    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        Paint paint = new Paint();
        paint.setTextSize(getTextSize());
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL);
        displayHeight = -paint.ascent() + paint.descent() + 2;//fontMetrics.bottom - fontMetrics.top + 4;
        measureList(paint, titleList, 0);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min((int) displayHeight, specSize);
            } else {
                result = (int) displayHeight;
            }
        }
        return result;
    }

    private float measureList(Paint paint, ArrayList<LinkInfo> infoList, float StartX) {
        if (infoList == null || infoList.size() == 0) {
            displayHeight = StartX;
            return StartX;
        }
        float x = StartX;
        int len = infoList.size();
        float fontHeight = -paint.ascent() + paint.descent();
        for (int i = 0; i < len; i++) {
            LinkInfo info = infoList.get(i);

            info.getRectFList().clear();

            if (info.isFace()) {
                Bitmap faceBmp = null;
                if (mFaceType == MSG_FACE_TYPE) {
                    faceBmp = StateFaceModel.getInstance()
                            .getSmallFaceIcon(info.getContent());                }
                if (faceBmp != null) {
                    int xLen = faceBmp.getWidth() + 4;
                    if (x + xLen >= displayWidth) {
                        displayHeight += fontHeight;
                        x = 0;
                    }
                    x += xLen;
                }
                continue;
            }
            String strContent = info.getContent();
            strContent = strContent.replaceAll("\n", " ");

            float xLen = paint.measureText(strContent);
            if (x + xLen >= displayWidth) {
                float startX = x;
                int lenStr = strContent.length();
                for (int j = 0; j < lenStr; j++) {
                    float width = paint.measureText(strContent, j, j + 1);
                    if (x + width >= displayWidth) {
                        RectF rectF = new RectF();
                        rectF.set(startX, displayHeight - fontHeight, x, displayHeight);
                        info.addRectF(rectF);
                        displayHeight += fontHeight;
                        x = width;
                        startX = 0;
                    } else {
                        x += width;
                    }
                }
                RectF rectF = new RectF();
                rectF.set(startX, displayHeight - fontHeight, x - startX, displayHeight);
                info.addRectF(rectF);
            } else {
                RectF rectF = new RectF();
                rectF.set(x, displayHeight - fontHeight, x + xLen, displayHeight);
                info.addRectF(rectF);
                x += xLen;
            }
        }
        return x;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (!this.isEnabled() || Listener == null) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();
            boolean flag = cancelCurInfo(x, y);
            if (flag) {
                return false;
            }
            if (clickLink(titleList, x, y, MotionEvent.ACTION_UP)) {
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            boolean flag = clickLink(titleList, x, y, MotionEvent.ACTION_DOWN);
            if (flag) {
                return true;
            }
        } else {
            float x = event.getX();
            float y = event.getY();
            cancelCurInfo(x, y);
        }
        return false;
    }

    private void paintSelectRectF(Canvas canvas, Paint paint, LinkInfo info) {
        ArrayList<RectF> rectList = info.getRectFList();
        int len = rectList.size();
        for (int i = 0; i < len; i++) {
            RectF rectF = rectList.get(i);
            paint.setColor(this.getContext().getResources().getColor(R.drawable.gray3));
            canvas.drawRect(rectF.left, rectF.top - 2, rectF.right, rectF.bottom - 2, paint);
        }
    }

    private boolean cancelCurInfo(float x, float y) {
        if (curInfo == null) {
            return true;
        }
        if (!curInfo.contains(x, y)) {
            curInfo.setSelected(false);
            this.invalidate();
            curInfo = null;
            return true;
        }
        return false;
    }

    private boolean clickLink(ArrayList<LinkInfo> infoList, float x, float y, int action) {
        if (infoList == null) {
            return false;
        }
        int len = infoList.size();

        for (int i = 0; i < len; i++) {
            LinkInfo info = infoList.get(i);
            if (info.isCommonString()) {
                continue;
            }
            if (info.contains(x, y)) {
                if (action == MotionEvent.ACTION_DOWN) {
                    info.setSelected(true);
                    this.invalidate();
                    this.curInfo = info;
                } else if (action == MotionEvent.ACTION_UP) {
                    this.curInfo = null;
                    info.setSelected(false);
                    this.invalidate();
                    Listener.onClick(info);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            int width = measureWidth(widthMeasureSpec);
            int height = measureHeight(heightMeasureSpec);
            setMeasuredDimension(width, height);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int initialWidth = getPaddingLeft() + getPaddingRight();
        int width = initialWidth;
        int maxWidth = 0;

        TextPaint tempPaint = null;

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (tempPaint == null) {
                tempPaint = new TextPaint();
                tempPaint.setStyle(Style.FILL);
                tempPaint.setAntiAlias(true);
                tempPaint.setTextSize(getTextSize());
            }

            if (titleList != null && titleList.size() > 0) {
                maxWidth = specSize;

                int size = titleList.size();
                forLable:
                for (int i = 0; i < size; i++) {
                    LinkInfo info = titleList.get(i);

                    if (info.isFace()) {
                        Bitmap faceBmp = null;
                        if (mFaceType == MSG_FACE_TYPE) {
                            faceBmp = StateFaceModel.getInstance()
                                    .getSmallFaceIcon(info.getContent());                        }
                        if (faceBmp != null) {
                            int wSize = faceBmp.getWidth() + 4;
                            if (width + wSize >= maxWidth) {
                                width = maxWidth;
                                break forLable;
                            }
                            width += wSize;
                        }
                        continue;
                    }

                    String text = info.getContent();
                    if (!TextUtils.isEmpty(text)) {
                        float wSize = tempPaint.measureText(text);
                        if (width + wSize >= maxWidth) {
                            width = maxWidth;
                            break forLable;
                        }
                        width += wSize;
                    }

                }
            }

            result = width;
        }

        displayWidth = result;
        return result;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


}
