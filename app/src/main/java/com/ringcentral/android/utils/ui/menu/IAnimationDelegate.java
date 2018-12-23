package com.ringcentral.android.utils.ui.menu;

public interface IAnimationDelegate {
    public void onAnimationStart(boolean isToUp);

    public void onAnimationEnd(int tab, boolean isSwitch, boolean isToUp);
}
