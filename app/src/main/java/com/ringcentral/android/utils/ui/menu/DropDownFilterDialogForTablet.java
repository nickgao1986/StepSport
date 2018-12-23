package com.ringcentral.android.utils.ui.menu;


import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pic.optimize.R;

/**
 * Created by nick.gao on 2015/5/8.
 */
public class DropDownFilterDialogForTablet extends DropDownFilterDialog {

    public DropDownFilterDialogForTablet(Context context) {
        super(context);
        mContext = context;
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drop_down_filter_dialog);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    public void hideDropDownFilter(boolean showAnimation) {
        dismiss();
        mDropdownClickListener.onDropdownHide();
    }


    public void init() {
        super.init();
        mDropDownMenuLayout.setBackgroundResource(R.drawable.dropdown_filters_background);
    }


    public void showDialog(boolean showAnimation, View banner) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int height = mContext.getResources().getDimensionPixelSize(R.dimen.drop_down_menu_item_margin_top_for_tablet);
        if (banner == null) {
            lp.y = height;
        } else {
            lp.y = height + banner.getMeasuredHeight();
        }
        show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hideDropDownFilter(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            hideDropDownFilter(true);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}