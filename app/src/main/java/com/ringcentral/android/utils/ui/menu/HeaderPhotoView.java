package com.ringcentral.android.utils.ui.menu;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pic.optimize.R;

public class HeaderPhotoView extends RelativeLayout {

    private static final String TAG = "[RC]HeaderPhotoView";

    public Context mContext;

    public ImageView mRedDotView;
    public ContactPhotoView mMenuButton;


    public HeaderPhotoView(Context context) {
        super(context, null);
    }

    public HeaderPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public void initView() {
        View mainView = inflate(mContext, R.layout.header_profile_view, this);
        mainView.setFocusable(true);
        mainView.setFocusableInTouchMode(true);
        mRedDotView = (ImageView) mainView.findViewById(R.id.red_dot_imageview);
        mMenuButton = (ContactPhotoView) mainView.findViewById(R.id.btn_main_menu_action_menu);
    }








}
