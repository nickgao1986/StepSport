package com.ringcentral.android.utils.ui.menu;

/**
 * Created by nick.gao on 2015/5/4.
 */
public class DropDownItem {
    String name;
    int couter;
    int index;

    public DropDownItem(String name,int index,int couter) {
        this.name = name;
        this.index = index;
        this.couter = couter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCouter() {
        return couter;
    }

    public void setCouter(int couter) {
        this.couter = couter;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
