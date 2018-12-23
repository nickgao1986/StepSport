package com.pic.optimize.rotatemenu;

import java.util.ArrayList;

import com.pic.optimize.R;
import com.pic.optimize.recycleview.TestRecycleViewActivity;
import com.ringcentral.android.utils.ui.menu.ActionMenu;
import com.ringcentral.android.utils.ui.menu.IAction;
import com.ringcentral.android.utils.ui.menu.IAnimationDelegate;
import com.ringcentral.android.utils.ui.menu.PlusButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class RoateMenuActivity extends Activity implements  IAction, IAnimationDelegate,ActionMenu.OnMenuItemSelect{

	private boolean mMenuStatus = false;
	PlusButton mPlusButton;
	private ActionMenu mActionMenu;
	private View mActionMenuBg;

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,RoateMenuActivity.class);
		context.startActivity(intent);
	}

	public static enum MenuItems {
		Fax {
			@Override
			public String toString() {
				return "Fax";
			}
		},
		Conferencing {
			@Override
			public String toString() {
				return "Conferencing";
			}
		},
		Meetings {
			@Override
			public String toString() {
				return "Meetings";
			}
		},
		Text {
			@Override
			public String toString() {
				return "Text";
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rotate_activity_main);
		mPlusButton = (PlusButton) findViewById(R.id.rcMenuButton);
		mPlusButton.setOnClickListener(mOnAnimationClickListener);
		mPlusButton.setActionListener(this);
		//mActionMenu = (ActionMenu)findViewById(R.id.rcActionMenu);
		mActionMenu = (ActionMenu)findViewById(R.id.rcActionMenu);
		mActionMenu.setAnimationDelegate(this);
		mActionMenu.setOnMenuItemSelectListener(this);
		mActionMenuBg = this.findViewById(R.id.rcActionMenuBg);
		mActionMenuBg.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getAction();
				if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				}
				return true;
			}
		});

		mActionMenu.setMenuItems(getPermissionListForMenu(this), ActionMenu.MenuStyle.Cycle);
	}

	private ArrayList<MenuItemInfo> getPermissionListForMenu(Context context) {
		ArrayList<MenuItemInfo> menuArray = new ArrayList<MenuItemInfo>();
		menuArray.add(new MenuItemInfo(MenuItems.Fax.toString(), MenuItems.Fax
				.ordinal(), R.drawable.menu_action_fax,
				R.string.messages_bar_item_fax));

		menuArray
				.add(new MenuItemInfo(MenuItems.Conferencing.toString(),
						MenuItems.Conferencing.ordinal(),
						R.drawable.menu_action_conference,
						R.string.tab_name_conference));
		menuArray.add(new MenuItemInfo(MenuItems.Meetings.toString(),
				MenuItems.Meetings.ordinal(), R.drawable.menu_action_meetings,
				R.string.tab_name_zoom_video));
		menuArray.add(new MenuItemInfo(MenuItems.Text.toString(),
				MenuItems.Text.ordinal(), R.drawable.menu_action_sms,
				R.string.messages_bar_item_text));

		return menuArray;
	}

	private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener();

	private class OnAnimationClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (!mMenuStatus) {
				mMenuStatus = true;
				mPlusButton.openWithAnimation();
			} else {
				mMenuStatus = false;
				mPlusButton.closeWithAnimation();
			}
		}
	}

	@Override
	public void openWithAnimation() {
	    mActionMenu.openWithAnimation();
	}

	@Override
	public void closeWithAnimation() {
		 mActionMenu.closeWithAnimation();
	}

	@Override
	public void closeWithoutAnimation() {
		 mActionMenu.closeWithoutAnimation();
	}

	@Override
	public void onAnimationStart(boolean isToUp) {
		mActionMenuBg.setVisibility(View.VISIBLE);
	}

	@Override
	public void onAnimationEnd(int tab, boolean isSwitch, boolean isToUp) {
		mActionMenuBg.setVisibility(View.GONE);
	}

	@Override
//	public void onAnimationStart() {
//		 mActionMenuBg.setVisibility(View.VISIBLE);
//	}
//
//	@Override
//	public void onAnimationEnd() {
//		 mActionMenuBg.setVisibility(View.GONE);
//	}



//	@Override
//	public void onMenuItemSelect(int itemId) {
//
//	}



	public void onMenuItemSelect(int itemId) {

	}
}
