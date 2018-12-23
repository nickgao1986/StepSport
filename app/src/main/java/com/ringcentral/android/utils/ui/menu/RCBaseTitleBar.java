package com.ringcentral.android.utils.ui.menu;


import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pic.optimize.R;

public class RCBaseTitleBar extends LinearLayout {

    protected TextView mTitleView = null;
    protected HeaderClickListener mHeaderClickListener;
    protected HeaderPhotoView mHeaderPhotoView;
    protected Context mContext;
    protected String mCurrentTabName;

    public interface HeaderClickListener {
        void onRightButtonClicked();
        void onLeftButtonClicked();
        void onDropDownFilterClicked();
    }

    public RCBaseTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
//        init(context, attrs);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), (int) getDefaultBottomPadding());
    }

    public RCBaseTitleBar(Context context) {
        super(context);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), (int) getDefaultBottomPadding());
    }

    protected void init(Context context, AttributeSet attrs) {
    }

    public float getDefaultBottomPadding() {
        return getResources().getDimensionPixelSize(R.dimen.actoin_bar_border_width);
    }

    public void setNameText(String name) {
        mTitleView.setText(name);
    }

    public void setText(int stringId) {
        mTitleView.setText(stringId);
    }

    public void setButtonsClickCallback(HeaderClickListener _headerClickListener) {
        mHeaderClickListener = _headerClickListener;
    }

    public void setCurrentScreenName(String name) {
        mCurrentTabName = name;
    }

    public void setRightImageRes(int resId) {
    }

    public void setRightImageVisibility(int visibility) {
    }

    public void setRightVisibility(int visibility) {
    }

    public void setRightBtnEnabled(boolean flag) {
    }

    public void setRightImageBtnEnabled(boolean flag) {
    }

    public void setRightText(int stringId) {
    }

    public void setLeftText(int stringId) {
    }

    public void showProfile() {
    }

    public void setTitleVisibility(int visibility) {
    }

    protected class ProfileOnClickListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            mContext.startActivity(intent);
        }
    }
}
