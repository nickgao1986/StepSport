package com.ringcentral.android.utils.ui.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RCBottomMenu extends RelativeLayout {
    protected static final int BOTTOM_MENU_TAB_SUM = 4;

    public RCBottomMenu(Context context) {
        super(context);
    }

    public RCBottomMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onUpdateIndicator() {}

    public interface OnTabClickListener {
        void onTabClick(int tab);
        void onPlusClick(boolean isFromMenu);
    }
}
