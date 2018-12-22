package com.ringcentral.android.utils.ui.menu;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.pic.optimize.R;
import com.pic.optimize.rotatemenu.MenuItemInfo;


public class CycleStyle implements IStyle{

	 private ActionMenu mActionMenu;
	    private int mItemPadding = 20;
	    private ArrayList<Position> mPositions = new ArrayList<Position>();

	    private float mRadian = 3.14f / 180.0f;
	    private float mCenterX = 0.0f;
	    private float mCenterY = 0.0f;
	    private float mRadius = 0.0f;

	    public CycleStyle(ActionMenu menu) {
	        mActionMenu = menu;
	        mItemPadding = mActionMenu.getResources().getDimensionPixelSize(R.dimen.tab_item_padding);
	    }

	    @Override
	    public View generateMenuItem(Context context, MenuItemInfo itemInfo) {
	        TextView item = new TextView(context);
	        item.setId(itemInfo.getItemId());
	        item.setText(itemInfo.getItemText());
	        item.setGravity(Gravity.CENTER);
	        item.setCompoundDrawablesWithIntrinsicBounds(0, itemInfo.getItemIcon(), 0, 0);
	        return item;
	    }

	    @Override
	    public void onLayout(boolean changed, int l, int t, int r, int b) {
	        ArrayList<View> menus = mActionMenu.getMenuItems();
	        int width = mActionMenu.getMeasuredWidth();
	        int height = mActionMenu.getMeasuredHeight();
	        if (menus.size() != mPositions.size()) {
	            reCalculate(width, height);
	        }
	        for (int i = 0; i < menus.size(); ++i) {
	            View item = menus.get(i);
	            Position position = mPositions.get(i);
	            int x = (int) position.getX();
	            int y = (int) position.getY();
	            int halfItemWidth = item.getMeasuredWidth() / 2;
	            int halfItemHeight = item.getMeasuredHeight() / 2;
	            item.layout(x - halfItemWidth, height - (y + halfItemHeight), x + halfItemWidth, height - (y - halfItemHeight));
	        }

	    }

	    @Override
	    public int onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        return mActionMenu.getMeasuredWidth();
	    }

	    @Override
	    public void onSizeChanged(int w, int h, int oldw, int oldh) {
	        reCalculate(w, h);
	    }

	    protected void reCalculate(int w, int h) {
	        mPositions.clear();
	        mPositions = getPositions(w, h, mActionMenu.getItemSize());
	    }


	    public ArrayList<Position> getPositions(int width, int height, int itemCount) {
	        mCenterX = width / 2;
	        mRadius = mCenterX - mItemPadding;
	        mCenterY = 0.0f;

	        ArrayList<Position> arrayPositions = new ArrayList<Position>();

	        float averageAngle = 180.0f / (itemCount + 1);
	        float currentAngle = averageAngle;
	        for (int i = 0; i < itemCount; ++i) {
	            arrayPositions.add(toPosition(currentAngle));
	            currentAngle += averageAngle;
	        }

	        return arrayPositions;
	    }

	    private float angleToRadian(float angle) {
	        return angle * mRadian;
	    }

	    private Position toPosition(float angle) {
	        float radian = angleToRadian(angle);
	        float x = mCenterX + (float) (Math.cos(radian) * mRadius);
	        float y = mCenterY + (float) (Math.sin(radian) * mRadius);
	        return new Position(x, y);
	    }

}
