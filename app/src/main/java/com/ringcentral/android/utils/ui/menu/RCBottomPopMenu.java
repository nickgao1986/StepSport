package com.ringcentral.android.utils.ui.menu;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pic.optimize.R;
import com.pic.optimize.tab.RCTabItem;

import java.util.List;



public class RCBottomPopMenu extends RCBottomMenu {
    private static final String TAG = "RCBottomPopMenu";
    private final int DEFAULT_ANI_DURATION = 150;
    private final int SPLIT_INDEX = 6;
    private int mAnimationDuration = DEFAULT_ANI_DURATION;
    private int mSplitIndex = SPLIT_INDEX;
    private int mPadding;
    private LinearLayout mTopLayout;
    private LinearLayout mBottomLayout;
    private RCTabView mDocumentItem;
    private OnTabClickListener mOnTabClickListener;
    private IAnimation mAnimation;
    private boolean isShowing = false;

    public RCBottomPopMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        initAttributes(context);
        LayoutInflater.from(context).inflate(R.layout.ringcentral_bottom_pop, this);
        mAnimation = new MenuCollapseAnimation();
    }

    public void setOnTabClickListener(OnTabClickListener listener) {
        mOnTabClickListener = listener;
    }

    private void initAttributes(Context context) {
        this.setBackgroundColor(context.getResources().getColor(R.color.bgColorMain));
        this.mPadding = getResources().getDimensionPixelSize(R.dimen.menu_item_padding);
    }

    public void addTabs(Context context, List<RCTabItem> list) {
        mTopLayout = (LinearLayout) findViewById(R.id.top_layout);
        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mTopLayout.removeAllViews();
        mBottomLayout.removeAllViews();
        addMobileTabs(context, list);
    }

    private void addMobileTabs(Context context, List<RCTabItem> list) {
        int size = list.size();
        if (size <=7) {
            mTopLayout.setVisibility(View.GONE);
        } else {
            mTopLayout.setVisibility(View.VISIBLE);
        }
        if (size == 8) {
            mSplitIndex = SPLIT_INDEX;
        }
        for (int i = BOTTOM_MENU_TAB_SUM; i < size; i++) {
            RCTabItem tabItem = list.get(i);
            RCTabView view = new RCTabView(context, tabItem.getItemId());
            view.setOnClickListener(mTabClickListener);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.gravity = CENTER_IN_PARENT;
            view.setLayoutParams(params);
            TextView textView = (TextView)view.findViewById(R.id.tab_main_text);
            textView.setText(tabItem.getItemText());
            Drawable drawable = context.getResources().getDrawable(tabItem.getItemIcon());
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            }
            textView.setCompoundDrawables(null, drawable, null, null);
            if (i <= mSplitIndex) {
                mBottomLayout.addView(view);
            } else {
                mTopLayout.addView(view);
            }
        }
    }


    @Override
    public void onUpdateIndicator() {
    }

    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
			final int newSelected = ((RCTabView)view).getPosition();
            mOnTabClickListener.onTabClick(newSelected);
        }
    };

    public void setItemSelectedState(int selectedItem) {
        final int leftCount = mTopLayout.getChildCount();
        for (int i = 0; i < leftCount; i++) {
            final RCTabView tabItem = (RCTabView) mTopLayout.getChildAt(i);
            if (selectedItem != tabItem.getPosition()) {
                tabItem.changeSelectedState(false);
            } else {
                tabItem.changeSelectedState(true);
            }
        }
        final int rightCount = leftCount + mBottomLayout.getChildCount();
        for (int i = leftCount; i < rightCount; i++) {
            final RCTabView tabItem = (RCTabView) mBottomLayout.getChildAt(i - leftCount);
            if (selectedItem != tabItem.getPosition()) {
                tabItem.changeSelectedState(false);
            } else {
                tabItem.changeSelectedState(true);
            }
        }
    }

    public void closeWithAnimation(final int tab, final boolean isSwitch) {
        isShowing = false;
        mAnimation.close(this, mAnimationDuration, tab, isSwitch);
    }

    public void openWithAnimation() {
        isShowing = true;
        mAnimation.open(this, mAnimationDuration);
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setAnimationDelegate(IAnimationDelegate aniDelegate) {
        mAnimation.setAnimationDelegate(aniDelegate);
    }
}
