package com.ringcentral.android.utils.ui.menu;


import java.util.ArrayList;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pic.optimize.R;

public class RCTitleBarWithDropDownFilter extends RCMainTitleBar implements View.OnClickListener {
    private ImageView mBtnDropDownImageview;
    private boolean mIsShowDialog = false;
    private DropDownFilterDialog mDropDownFilterDialog;
    private int mCurrentIndex = 0;
    private boolean mHasDropDown = false;
    public DropDownMenuClicked mDropDownMenuClicked;
    private Context mContext;

    public static final int MESSAGE_ALL = 0;
    public static final int MESSAGE_VOICE = 1;
    public static final int MESSAGE_FAX = 2;
    public static final int MESSAGE_TEXT = 3;


    public final static int STATE_ALL = 0;
    public final static int STATE_VOICE = 1;
    public final static int STATE_FAX = 2;
    public final static int STATE_TEXT = 3;


    private ArrayList<DropDownItem> mDropDownItemList;

    private View mBanner;
    private LinearLayout mTitleLayout;
    public int getState() {
        return mCurrentIndex;
    }

    public void setDropDownItemList(ArrayList<DropDownItem> mDropDownItemList) {
        this.mDropDownItemList = mDropDownItemList;
    }



    public RCTitleBarWithDropDownFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RCTitleBarWithDropDownFilter(Context context) {
        super(context, null);
        init(context, null);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        mContext = context;
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);
        mHasDropDown = a.getBoolean(R.styleable.TitleBar_title_has_dropdown,false);
        a.recycle();


        mBtnDropDownImageview = (ImageView) findViewById(R.id.btn_drop_down_imageview);
        if(mHasDropDown) {
            mBtnDropDownImageview.setVisibility(View.VISIBLE);
        }else{
            mBtnDropDownImageview.setVisibility(View.GONE);
        }
        mTitleLayout = (LinearLayout)findViewById(R.id.title_layout);
        mTitleLayout.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if (null == mHeaderClickListener) {
            return;
        }
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_layout:
                if(mBtnDropDownImageview.getVisibility() == View.GONE) {
                    return;
                }
                if (!mIsShowDialog) {
                    // will show dialog
                    showDropDownFilter(true);
                    mIsShowDialog = true;
                } else {
                    mDropDownFilterDialog.hideDropDownFilter(true);
                    mIsShowDialog = false;
                }
                break;
        }

    }

    public void setOnDropDownMenuClick(DropDownMenuClicked mDropDownMenuClicked) {
        this.mDropDownMenuClicked = mDropDownMenuClicked;
    }


    public void setDropDownMenuVisible(int visible) {
        if(visible == View.VISIBLE) {
            mBtnDropDownImageview.setVisibility(View.VISIBLE);
        }else{
            mBtnDropDownImageview.setVisibility(View.GONE);
        }
    }


    public void rotatePlusButton(Context context, boolean isUp) {
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(
                context, isUp ? R.anim.flip_up : R.anim.flip_down);
        animatorSet.setTarget(mBtnDropDownImageview);
        animatorSet.start();
    }

    public void setLeftImage() {

    }

    private void showDropDownFilter(boolean showAnimation) {
        if (mDropDownFilterDialog == null) {
            mDropDownFilterDialog = new DropDownFilterDialogForPhone(getContext());
        }

        rotatePlusButton(mContext, false);
        mDropDownFilterDialog.setCurrentIndex(mCurrentIndex);
        mDropDownFilterDialog.setTopMenuItemList(mDropDownItemList);
        mDropDownFilterDialog.showDialog(showAnimation, mBanner);
        mDropDownFilterDialog.setDropDownClickListener(new DropDownFilterDialog.OnDropdownClickListener() {
            @Override
            public void onDropdownHide() {
                rotatePlusButton(mContext, true);
                mIsShowDialog = false;
            }

            @Override
            public void onClickItem(int index) {
                if (index == mCurrentIndex) {
                    return;
                }
                mCurrentIndex = index;
                mDropDownMenuClicked.onDropDownMenuClicked(index);
            }
        });
    }



    /*****************************************init filter*************************************************/
    public int getCurrentIndex() {
        return mCurrentIndex;
    }



    public void initMessageFilterWithState(int state) {
        mCurrentIndex = state;
        switch (state) {
            case STATE_ALL:
                setText(R.string.messages_bar_item_all);
                break;
            case STATE_VOICE:
                setText(R.string.messages_bar_item_voice);
                break;
            case STATE_FAX:
                setText(R.string.messages_bar_item_fax);
                break;
            case STATE_TEXT:
                setText(R.string.messages_bar_item_text);
                break;
        }
    }


}
