package com.ringcentral.android.utils.ui.menu;


import android.content.Context;
import android.view.View;

import com.pic.optimize.rotatemenu.MenuItemInfo;

public interface IStyle {
    public View generateMenuItem(Context context, MenuItemInfo itemInfo);

    public void onLayout(boolean changed, int l, int t, int r, int b);

    public int onMeasure(int widthMeasureSpec, int heightMeasureSpec);

    public void onSizeChanged(int w, int h, int oldw, int oldh);
}
