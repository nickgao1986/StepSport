package com.pic.optimize.tab;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.example.tutorial.TutorialActivity;
import com.pic.optimize.DragIconActivity;
import com.pic.optimize.R;
import com.ringcentral.android.utils.ui.menu.DropDownItem;
import com.ringcentral.android.utils.ui.menu.DropDownMenuClicked;
import com.ringcentral.android.utils.ui.menu.IAnimationDelegate;
import com.ringcentral.android.utils.ui.menu.RCBaseTitleBar;
import com.ringcentral.android.utils.ui.menu.RCBottomPopMenu;
import com.ringcentral.android.utils.ui.menu.RCBottomTabMenu;
import com.ringcentral.android.utils.ui.menu.RCTitleBarWithDropDownFilter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;

public class TabActivity extends TutorialActivity implements
		RCBottomTabMenu.OnTabClickListener, IAnimationDelegate,
		RCBaseTitleBar.HeaderClickListener {

	private int mCurrentFragment = 0;
	RCBottomTabMenu mRcBottomTabMenu;
	RCBottomPopMenu mRCBottomPopMenu;
	private List<RCTabItem> mRCTabArray;
	private View mCoverView;

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,TabActivity.class);
		context.startActivity(intent);
	}




	public static enum MainActivities {
		Ringout {
			@Override
			public String toString() {
				return "Ringout";
			}
		},
		Messages {
			@Override
			public String toString() {
				return "Messages";
			}
		},
		Calllog {
			@Override
			public String toString() {
				return "CallLog";
			}
		},
		Contacts {
			@Override
			public String toString() {
				return "Contacts";
			}
		},
		Text {
			@Override
			public String toString() {
				return "Text";
			}
		},
		Favorites {
			@Override
			public String toString() {
				return "Favorites";
			}
		},
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
		RCDocuments {
			@Override
			public String toString() {
				return "RCDocuments";
			}
		}
	}

	private ArrayList<DropDownItem> getTopMenuListData() {
		ArrayList<DropDownItem> mTopMenuList = new ArrayList<DropDownItem>();
		mTopMenuList.add(new DropDownItem(this.getResources().getString(
				R.string.message_all), RCTitleBarWithDropDownFilter.STATE_ALL,
				0));

		mTopMenuList.add(new DropDownItem(this.getResources().getString(
				R.string.message_voice),
				RCTitleBarWithDropDownFilter.STATE_VOICE, 0));

		mTopMenuList.add(new DropDownItem(this.getResources().getString(
				R.string.message_fax), RCTitleBarWithDropDownFilter.STATE_FAX,
				0));

		mTopMenuList.add(new DropDownItem(this.getResources().getString(
				R.string.message_text),
				RCTitleBarWithDropDownFilter.STATE_TEXT, 0));

		return mTopMenuList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_activity_main);
		final RCTitleBarWithDropDownFilter mainTitleBar = (RCTitleBarWithDropDownFilter) findViewById(R.id.header);
		mainTitleBar.setDropDownItemList(getTopMenuListData());
		mainTitleBar.setOnDropDownMenuClick(new DropDownMenuClicked() {
			@Override
			public void onDropDownMenuClicked(int index) {
				mainTitleBar.initMessageFilterWithState(index);
			}
		});
		mainTitleBar.setText(R.string.message_all);
		mainTitleBar.setButtonsClickCallback(this);
		mRCTabArray = updateMenuDataList();
		mRcBottomTabMenu = (RCBottomTabMenu) findViewById(R.id.rcBottomTabBar);

		mRcBottomTabMenu.setOnTabClickListener(this);
		mRcBottomTabMenu.addTabs(this, mRCTabArray);
		mRCBottomPopMenu = (RCBottomPopMenu) findViewById(R.id.rcBottomPopWidget);
		mRCBottomPopMenu.setOnTabClickListener(this);
		mRCBottomPopMenu.setAnimationDelegate(this);
		mRCBottomPopMenu.addTabs(this, mRCTabArray);
		mCoverView = findViewById(R.id.cover_view);
		mCoverView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					popDownWidget(0, false);
				}
				return true;
			}
		});
		
	}

	private void popDownWidget(final int tab, final boolean isSwitch) {
		mRCBottomPopMenu.closeWithAnimation(tab, isSwitch);
	}

	private List<RCTabItem> updateMenuDataList() {
		List<RCTabItem> tabItems = new ArrayList<RCTabItem>();

		tabItems.add(new RCTabItem(MainActivities.Ringout.toString(),
				MainActivities.Ringout.ordinal(),
				R.drawable.launch_bar_button_foreground_call,
				R.string.menu_list_dialpad));

		tabItems.add(new RCTabItem(MainActivities.Messages.toString(),
				MainActivities.Messages.ordinal(),
				R.drawable.launch_bar_button_foreground_message,
				R.string.menu_list_messages));

		tabItems.add(new RCTabItem(MainActivities.Calllog.toString(),
				MainActivities.Calllog.ordinal(),
				R.drawable.launch_bar_button_foreground_call_log,
				R.string.menu_list_recents));

		tabItems.add(new RCTabItem(MainActivities.Contacts.toString(),
				MainActivities.Contacts.ordinal(),
				R.drawable.launch_bar_button_foreground_contact,
				R.string.menu_list_contact));

		tabItems.add(new RCTabItem(
				MainActivities.Text.toString(),
				MainActivities.Text.ordinal(),
				tabItems.size() >= 4 ? R.drawable.widget_bar_button_foreground_sms
						: R.drawable.launch_bar_button_foreground_sms,
				R.string.menu_list_sms));

		tabItems.add(new RCTabItem(
				MainActivities.Favorites.toString(),
				MainActivities.Favorites.ordinal(),
				tabItems.size() >= 4 ? R.drawable.widget_bar_button_foreground_favorite
						: R.drawable.launch_bar_button_foreground_favorite,
				R.string.menu_list_favorites));

		tabItems.add(new RCTabItem(MainActivities.Fax.toString(),
				MainActivities.Fax.ordinal(),
				R.drawable.widget_bar_button_foreground_fax,
				R.string.menu_list_fax));

		tabItems.add(new RCTabItem(MainActivities.Conferencing.toString(),
				MainActivities.Conferencing.ordinal(),
				R.drawable.widget_bar_button_foreground_conf,
				R.string.menu_list_conference));

		tabItems.add(new RCTabItem(MainActivities.Meetings.toString(),
				MainActivities.Meetings.ordinal(),
				R.drawable.widget_bar_button_foreground_meeting,
				R.string.tab_name_zoom_video));

		tabItems.add(new RCTabItem(MainActivities.RCDocuments.toString(),
				MainActivities.RCDocuments.ordinal(),
				R.drawable.widget_bar_button_foreground_document,
				R.string.menu_list_documents));
		return tabItems;
	}

	@Override
	public void onTabClick(int tab) {
		if (mRCBottomPopMenu.isShowing()) {
			popDownWidget(tab, mCurrentFragment != tab);
		} else if (mCurrentFragment != tab) {
			setCurrentTab(tab);
		}
	}

	private void setCurrentTab(int tab) {
		boolean previous = isSelectItemInPopWidget(mCurrentFragment);
		boolean current = isSelectItemInPopWidget(tab);
		if (previous && current) {
			mRCBottomPopMenu.setItemSelectedState(tab);
		} else if (!previous && !current) {
			mRcBottomTabMenu.setItemSelectedState(tab);
		} else {
			mRcBottomTabMenu.setItemSelectedState(tab);
			mRCBottomPopMenu.setItemSelectedState(tab);
		}
		mCurrentFragment = tab;
		// setContainer(tab);
	}

	@Override
	public void onPlusClick(boolean isFromMenu) {
		if (mRCBottomPopMenu.isShowing()) {
			popDownWidget(0, false);
		} else {
			if (isSelectItemInPopWidget(0)) {
				mRCBottomPopMenu.setItemSelectedState(0);
			}
			popUpWidget();
		}
	}

	private boolean isSelectItemInPopWidget(int tab) {
		return tab > mRCTabArray.get(3).getItemId();
	}

	private void popUpWidget() {
		mRCBottomPopMenu.openWithAnimation();
	}

	@Override
	public void onAnimationStart(boolean isToUp) {
		if (isToUp) {
			mRcBottomTabMenu.rotatePlusButton(this, true);
			mCoverView.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.cover_show));
		} else {
			mRcBottomTabMenu.rotatePlusButton(this, false);
			mCoverView.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.cover_dismiss));
		}
	}

	@Override
	public void onAnimationEnd(int tab, boolean isSwitch, boolean isToUp) {
		if (isToUp) {
			mCoverView.setVisibility(View.VISIBLE);
		} else {
			if (isSwitch) {
				// setCurrentTab(tab);
			}
			mCoverView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onRightButtonClicked() {

	}

	@Override
	public void onLeftButtonClicked() {

	}

	@Override
	public void onDropDownFilterClicked() {

	}

	private void saveFile(String str) {
		String fileName = Environment.getExternalStorageDirectory() + "/"
				+ "log.txt";
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(str);
			writer.flush();
		} catch (IOException ex) {
			System.out.println("=====ex="+ex);
		} finally {
			try {
				writer.close();
			} catch (IOException ex) {

			}
		}
	}

}
