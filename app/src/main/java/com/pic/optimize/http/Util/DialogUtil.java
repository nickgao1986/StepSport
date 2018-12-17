package com.pic.optimize.http.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.pic.optimize.R;
import com.pic.optimize.fresco.PicApplication;

public class DialogUtil {

    /**
     * 加载中对话框
     */
    public static ProgressDialog mLoadingDialog;


    /**
     * 显示加载中对话框
     */
    public static void showLoadingDialog(Context context) {
        if (context == null) {
            context = PicApplication.getContext();
        }
        showLoadingDialog(context, context.getString(R.string.loading));
    }

    /**
     * 关闭加载中的对话框
     */
    public static void dismissLoadingDialog(Context context) {
        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
                mLoadingDialog = null;
            }
            // }
        } catch (Throwable e) {
        }
    }


    /**
     * 显示加载中的对话框
     *
     * @param message 提示信息
     */
    public static void showLoadingDialog(Context context, String message) {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new ProgressDialog(context);
            }
            mLoadingDialog.setMessage(message);
            mLoadingDialog.setCancelable(true);
            if (!mLoadingDialog.isShowing() && !((Activity) context).isFinishing()) {
                try {
                    mLoadingDialog.show();
                } catch (Throwable e) {

                }
            }
        } catch (Exception e) {
        }
    }

}
