package com.pic.optimize;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

public class PicTest extends Activity{

    private static final String TAG = PicTest.class.getSimpleName();
    private Bitmap mCurrentBitmp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_test);
        ImageView firstImg = (ImageView)findViewById(R.id.first_img);
        ImageView secondImg = (ImageView)findViewById(R.id.second_img);
        loadOriginalSize(firstImg);
        testInBitmap(secondImg);
    }

    /**
     * 直接加载load sdcard里的图片
     * @param img
     */
    private void loadOriginalSize(ImageView img) {
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = sdcard + "/11.jpg";

        mCurrentBitmp = BitmapFactory.decodeFile(filePath);
        img.setImageBitmap(mCurrentBitmp);
    }


    /**
     * 压缩图片
     * @param img
     */
    private void testPicOptimize(ImageView img) {
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = sdcard + "/11.jpg";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,options);

        int width = options.outWidth;
        options.inSampleSize = width / 200;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath,options);
        img.setImageBitmap(bitmap);
    }


    /**
     * inBitmap的使用
     * @param secondImg
     */
    private void testInBitmap(ImageView secondImg) {
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = sdcard + "/11.jpg";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inBitmap = mCurrentBitmp;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath,options);
        secondImg.setImageBitmap(bitmap);
    }
}
