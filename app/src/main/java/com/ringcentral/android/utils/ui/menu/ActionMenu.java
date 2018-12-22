package com.ringcentral.android.utils.ui.menu;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.pic.optimize.rotatemenu.MenuItemInfo;

import java.util.ArrayList;
import java.util.List;

public class ActionMenu extends ViewGroup implements IAction, View.OnClickListener {

    public ActionMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	private final int DEFAULT_ANI_DURATION = 200;
    private int mAnimationDuration = DEFAULT_ANI_DURATION;

    private MenuStyle mMenuStyle = MenuStyle.Cycle;
    private IStyle mStyle;
    private IAnimation mAnimation;

    private ArrayList<View> mArrayMenus;
    private boolean mIsInit = false;

    private OnMenuItemSelect mOnMenuItemSelect;
    private IAnimationDelegate mAniDelegate;

    public static enum MenuStyle {
        Cycle,
        Collapse
    }

    public interface OnMenuItemSelect {
        /**
         * event will be triggered when one of the item has been selected.
         *
         * @param itemId -1 dismiss menu
         */
        public void onMenuItemSelect(int itemId);
    }
    
    
    public ArrayList<View> getMenuItems() {
        return mArrayMenus;
    }
    
    
    public int getItemSize() {
        return mArrayMenus.size();
    }
    
    private void initData(Context context) {
        if (!mIsInit) {
            //this.setOnClickListener(this);
            MenuStyle mMenuStyle = MenuStyle.Cycle;
            setMenuStyle(mMenuStyle);
            mArrayMenus = new ArrayList<View>();
            mIsInit = true;
        }
    }
    
    
    public void setMenuItems(List<MenuItemInfo> itemList, MenuStyle menuStyle) {
        setMenuStyle(menuStyle);
        mArrayMenus.clear();
        removeAllViews();
        for (MenuItemInfo item : itemList) {
            addItem(mStyle.generateMenuItem(getContext(), item));
        }
    }
    
    public void setOnMenuItemSelectListener(OnMenuItemSelect listener) {
        mOnMenuItemSelect = listener;
    }
    
    
    private void addItem(View item) {
        item.setOnClickListener(this);
        mArrayMenus.add(item);
        this.addView(item);
    }
    
    
    public void setAnimationDelegate(IAnimationDelegate aniDelegate) {
        mAniDelegate = aniDelegate;
        mAnimation.setAnimationDelegate(aniDelegate);
    }
    
    
    
    private void setMenuStyle(MenuStyle menuStyle) {
        mMenuStyle = menuStyle;
        if (menuStyle == MenuStyle.Cycle) {
            mStyle = new CycleStyle(this);
            mAnimation = new MenuRotateAnimation();
            mAnimation.setAnimationDelegate(mAniDelegate);
        }
    }
    
	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void openWithAnimation() {
		 mAnimation.open(this, mAnimationDuration);
	}

	@Override
	public void closeWithAnimation() {
		  mAnimation.close(this, mAnimationDuration);
	}

	@Override
	public void closeWithoutAnimation() {
		 mAnimation.close(this, 0);
	}

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mStyle.onSizeChanged(w, h, oldw, oldh);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mStyle.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        for (int i = 0; i < mArrayMenus.size(); ++i) {
            View item = mArrayMenus.get(i);
            this.measureChild(item, widthMeasureSpec, heightMeasureSpec);
        }

        int height = mStyle.onMeasure(widthMeasureSpec, heightMeasureSpec);
        layoutParams.height = height;
    }
    
}
