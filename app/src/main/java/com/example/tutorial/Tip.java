package com.example.tutorial;

public class Tip {
	int id;
    String screen;
    String viewId;
    String text;
    String brand[];
    String permission[];
    String hotPadding;
    boolean hotZoomIn;
    String eventName;
    boolean isFragment;

    public int getId() {
        return id;
    }

    public String getScreen() {
        return screen;
    }

    public String getViewId() {
        return viewId;
    }

    public String getText() {
        return text;
    }

    public String[] getBrand() {
        return brand;
    }

    public String[] getPermission() {
        return permission;
    }

    public String getHotPadding() {
        return hotPadding;
    }

    public boolean isHotZoomIn() {
        return hotZoomIn;
    }

    public String getEventName() {
        return eventName;
    }

    public boolean isFragment() {
        return isFragment;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setBrand(String brand[]) {
        this.brand = brand;
    }

    public void setPermission(String permission[]) {
        this.permission = permission;
    }

    public void setHotPadding(String hotPadding) {
        this.hotPadding = hotPadding;
    }

    public void setHotZoomIn(boolean hotZoomIn) {
        this.hotZoomIn = hotZoomIn;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setFragment(boolean isFragment) {
        this.isFragment = isFragment;
    }
}
