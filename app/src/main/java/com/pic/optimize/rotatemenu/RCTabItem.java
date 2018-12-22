package com.pic.optimize.rotatemenu;

public class RCTabItem {
    private String mItemName;
    private int mItemId;
    private int mItemIcon;
    private int mItemText;

    public RCTabItem(String tabName, int tabId, int tabIcon, int tabTitle) {
        this.mItemName = tabName;
        this.mItemId = tabId;
        this.mItemIcon = tabIcon;
        this.mItemText = tabTitle;
    }

    public String getItemName() {
        return mItemName;
    }

    public int getItemId() {
        return mItemId;
    }

    public int getItemText() {
        return mItemText;
    }

    public int getItemIcon() {
        return mItemIcon;
    }
}