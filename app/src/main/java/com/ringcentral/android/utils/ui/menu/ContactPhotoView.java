package com.ringcentral.android.utils.ui.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.pic.optimize.R;

public class ContactPhotoView extends ImageView {

    private static final String TAG = "[RC]ContactPhotoView";

    private static final int DEFAULT_PHOTO_SIZE = 130;
    private static final int DEFAULT_CORNER_RADIUS = 15; // for 130x130 image..
    private static final int DEFAULT_CONTACT_PHOTO = R.drawable.image_contact_info_photo_default;
    private static final int PHOTO_BORDER_WIDTH = 2;

    private int mCornerRadius = DEFAULT_CORNER_RADIUS;
    private int mViewSize = DEFAULT_PHOTO_SIZE;
    private int mDefaultContactPhoto = DEFAULT_CONTACT_PHOTO;
    private int mPhotoBorderWidth = PHOTO_BORDER_WIDTH;

    public ContactPhotoView(Context context) {
        super(context);
        init(context, null);
    }

    public ContactPhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ContactPhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedCornerImageViewStyle);
            mCornerRadius = a.getInt(R.styleable.RoundedCornerImageViewStyle_corner_radius, DEFAULT_CORNER_RADIUS);
            mViewSize = a.getInt(R.styleable.RoundedCornerImageViewStyle_image_size, DEFAULT_PHOTO_SIZE);
            mDefaultContactPhoto = a.getResourceId(R.styleable.RoundedCornerImageViewStyle_default_image, DEFAULT_CONTACT_PHOTO);
            final TypedValue tvBorderWidth = a.peekValue(R.styleable.RoundedCornerImageViewStyle_border_width);

            if (tvBorderWidth != null) {
                switch (tvBorderWidth.type) {
                    case TypedValue.TYPE_DIMENSION:
                        mPhotoBorderWidth = a.getDimensionPixelSize(R.styleable.RoundedCornerImageViewStyle_border_width, PHOTO_BORDER_WIDTH);
                        break;
                    case TypedValue.TYPE_INT_DEC:
                        mPhotoBorderWidth = a.getInt(R.styleable.RoundedCornerImageViewStyle_border_width, PHOTO_BORDER_WIDTH);
                        break;
                    default:
                        mPhotoBorderWidth = PHOTO_BORDER_WIDTH;
                        break;
                }
            } else {
                mPhotoBorderWidth = PHOTO_BORDER_WIDTH;
            }

            a.recycle();


        }
        setContactPhoto(null);
    }

    public void setContactPhoto(Bitmap photo) {
        if (photo == null) {
            super.setImageDrawable(getContext().getResources().getDrawable(mDefaultContactPhoto));
        } else {
            setImageDrawable(new BitmapDrawable(photo));
        }
    }

    /**
     * All other methods for settings images will pass through this one
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        if (drawable != null) {
            BitmapDrawable roundedDrawable = null;
            try {
                Bitmap srcBitmap = ((BitmapDrawable) drawable).getBitmap();
                roundedDrawable = new BitmapDrawable(getRoundedCornerBitmap(getContext(), srcBitmap, mCornerRadius, getMeasuredWidth(), getMeasuredHeight(), mPhotoBorderWidth));
            } catch (Throwable e) {
            }
            if (roundedDrawable != null) {
                drawable = roundedDrawable;
            }

        }
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setContactPhoto(bm);
    }

    @Override
    public void setImageURI(Uri uri) {
        throw new RuntimeException("Not supported");
    }

    /**
     * Create a bitmap with rounded corners
     *
     * @param bitmap       original bitmap
     * @param cornerRadius corner radius in pixels
     * @return bitmap with rounded corners
     */
    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, int cornerRadius, int viewWidth, int viewHeight, int photoBorderWidth) {
        Bitmap result = null;
        try {
            final int width = bitmap.getWidth();
            final int height = bitmap.getHeight();
            final int newBmpWidth = viewWidth > DEFAULT_PHOTO_SIZE ? viewWidth : DEFAULT_PHOTO_SIZE;
            final int newBmpHeight = viewHeight > DEFAULT_PHOTO_SIZE ? viewHeight : DEFAULT_PHOTO_SIZE;
            final float scaleFactor = Math.min(((float) newBmpWidth) / (width), ((float) newBmpHeight) / (height));
            final Rect rect = new Rect(photoBorderWidth, photoBorderWidth, width - photoBorderWidth, height - photoBorderWidth);
            final RectF rectF = new RectF(photoBorderWidth, photoBorderWidth, width * scaleFactor - photoBorderWidth, height * scaleFactor - photoBorderWidth);

            result = Bitmap.createBitmap(newBmpWidth, newBmpHeight, Config.ARGB_8888);

            final Canvas canvas = new Canvas(result);
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            canvas.drawARGB(0, 0, 0, 0); // filling with transparent background
            canvas.drawRoundRect(rectF, cornerRadius + 30, cornerRadius + 30, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)); // applying alpha composing
            canvas.drawBitmap(bitmap, rect, rectF, paint); // finally paint to bitmap

        } catch (Throwable t) {
        }
        return result;
    }

}
