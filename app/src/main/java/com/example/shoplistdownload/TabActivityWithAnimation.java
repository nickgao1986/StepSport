package com.example.shoplistdownload;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.textviewwithurl.NewsActivity;
import com.pic.optimize.MainTest;
import com.pic.optimize.PicTest;
import com.pic.optimize.R;
import com.pic.optimize.fresco.TestListActivity;
import com.pic.optimize.helper.PaperHelpActivity;
import com.pic.optimize.picwall.PicWallActivity;


public class TabActivityWithAnimation extends TabActivity {
	private TabHost mTabHost;
	private TabWidget mTabWidget;
	private static String HOME_TAB = "home tab";
	private static String CHAT_TAB = "chat tab";
	private static String MESSAGE_TAB = "message tab";
	private static String PROFILE_TAB = "profile tab";
	private static String MORE_TAB = "more tab";
	private LayoutInflater mLayoutInflater;
	private ImageView animImage = null;
	private int lastTabIndex = 0;
	private int currTabIndex = 0;
	private int lastImageChangeOrientation = -1;
	
	private int widgetItemWidth = 0;
	private int focusWidgetItemWidth = 0;
	private int focusWidgetItemHeight = 0;
	
	private static final int[] NORMAL_IMAGE = {
	    R.drawable.home,
	    R.drawable.bar,
	    R.drawable.msg_center,
	    R.drawable.profile,
	    R.drawable.more
	};
	private static final int[] SELECTED_IMAGE = {
        R.drawable.home_pressed,
        R.drawable.bar_pressed,
        R.drawable.msg_center_pressed,
        R.drawable.profile_pressed,
        R.drawable.more_pressed
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maintab_activity);
		mLayoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		initTabHost();
	}
	
	/**
	 *  initial tab host 
	 */
	private void initTabHost() {
		mTabHost = getTabHost();
		mTabWidget = mTabHost.getTabWidget();
		
		mTabHost.setCurrentTab(0);

		View homeWidgetView = mLayoutInflater.inflate(R.layout.tab_indicator, mTabWidget, false);
		Intent intent = new Intent();
		intent.setClass(this, PicWallActivity.class);
		TabHost.TabSpec tabSpec = mTabHost.newTabSpec(HOME_TAB);
		TextView homeTitle = (TextView) homeWidgetView.findViewById(R.id.title);
		homeTitle.setText(R.string.main_tab_home);
		ImageView homeIcon = (ImageView) homeWidgetView.findViewById(R.id.icon);
		homeIcon.setBackgroundResource(NORMAL_IMAGE[0]);
		tabSpec = tabSpec.setIndicator(homeWidgetView).setContent(intent);
		
		mTabHost.addTab(tabSpec);

		View barWidgetView = mLayoutInflater.inflate(R.layout.tab_indicator, mTabWidget, false);
		tabSpec = this.mTabHost.newTabSpec(CHAT_TAB);
		intent = new Intent(this, NewsActivity.class);
		TextView barTitle = (TextView) barWidgetView.findViewById(R.id.title);
		barTitle.setText(R.string.main_tab_bar);
		ImageView barIcon = (ImageView) barWidgetView.findViewById(R.id.icon);
		barIcon.setBackgroundResource(NORMAL_IMAGE[1]);
		
		tabSpec = tabSpec.setIndicator(barWidgetView).setContent(intent);
		mTabHost.addTab(tabSpec);

		View msgWidgetView = mLayoutInflater.inflate(R.layout.tab_indicator, mTabWidget, false);
		tabSpec = this.mTabHost.newTabSpec(MESSAGE_TAB);
		intent = new Intent(this, TestListActivity.class);
		TextView msgTitle = (TextView) msgWidgetView.findViewById(R.id.title);
		msgTitle.setText(R.string.main_tab_message);
		ImageView msgIcon = (ImageView) msgWidgetView.findViewById(R.id.icon);
		msgIcon.setBackgroundResource(NORMAL_IMAGE[2]);
		tabSpec = tabSpec.setIndicator(msgWidgetView).setContent(intent);
		mTabHost.addTab(tabSpec);

		View myinfoWidgetView = mLayoutInflater.inflate(R.layout.tab_indicator, mTabWidget, false);
		tabSpec = this.mTabHost.newTabSpec(PROFILE_TAB);
		intent = new Intent(this, PaperHelpActivity.class);
		TextView myinfoTitle = (TextView) myinfoWidgetView.findViewById(R.id.title);
		myinfoTitle.setText(R.string.main_tab_myinfo);
		ImageView myinfoIcon = (ImageView) myinfoWidgetView.findViewById(R.id.icon);
		myinfoIcon.setBackgroundResource(NORMAL_IMAGE[3]);
		tabSpec = tabSpec.setIndicator(myinfoWidgetView).setContent(intent);
		mTabHost.addTab(tabSpec);

		View moreWidgetView = mLayoutInflater.inflate(R.layout.tab_indicator, mTabWidget, false);
		tabSpec = this.mTabHost.newTabSpec(MORE_TAB);
		intent = new Intent(this, PicWallActivity.class);
		TextView moreTitle = (TextView) moreWidgetView.findViewById(R.id.title);
		moreTitle.setText(R.string.main_tab_more);
		ImageView moreIcon = (ImageView) moreWidgetView.findViewById(R.id.icon);
		moreIcon.setBackgroundResource(NORMAL_IMAGE[4]);
		tabSpec = tabSpec.setIndicator(moreWidgetView).setContent(intent);
		mTabHost.addTab(tabSpec);
		mTabHost.setOnTabChangedListener(new TabHostListener(this));

		((ImageView)mTabWidget.getChildAt(0).findViewById(R.id.icon))
            .setImageResource(SELECTED_IMAGE[0]);
		((TextView)mTabWidget.getChildAt(0).findViewById(R.id.title))
    		.setTextColor(TabActivityWithAnimation.this.getResources().getColor(R.color.white));
		mTabWidget.getChildAt(0).setBackgroundResource(R.drawable.tab_bottom_selected);
		
		animImage = (ImageView) findViewById(R.id.tab_widget_image);
		animImage.setVisibility(View.INVISIBLE);
	}
	
	 private void showAnimation() {
	     Log.v("test", "showAnimation");
	     if (lastTabIndex == currTabIndex) {
	         return;
	     }
	     if (mTabWidget.getChildCount() < 2) {
	         return;
	     }
	     
	     if (getResources().getConfiguration().orientation != lastImageChangeOrientation) {
	         lastImageChangeOrientation = getResources().getConfiguration().orientation;
	         
	         widgetItemWidth = mTabWidget.getWidth() / mTabWidget.getChildCount();
	         
	         View currView = mTabWidget.getChildAt(currTabIndex);
	         focusWidgetItemWidth = currView.getWidth();
	         focusWidgetItemHeight = currView.getHeight();
	         
	         LayoutParams lp = animImage.getLayoutParams();
	         lp.width = focusWidgetItemWidth;
	         lp.height = focusWidgetItemHeight;
	         animImage.setLayoutParams(lp);
	     }
	        
	     int fromX = lastTabIndex * widgetItemWidth;
	     int toX = currTabIndex * widgetItemWidth;
	     Log.v("test", "fromX:" + fromX + " toX:" + toX);
         TranslateAnimation animation = new TranslateAnimation(fromX, toX, 0, 0);
         animation.setDuration(600);
         animation.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
                mTabWidget.getChildAt(lastTabIndex).setBackgroundResource(R.drawable.tab_bottom);
                ((ImageView)mTabWidget.getChildAt(lastTabIndex).findViewById(R.id.icon))
                    .setImageResource(NORMAL_IMAGE[lastTabIndex]);
                ((TextView)mTabWidget.getChildAt(lastTabIndex).findViewById(R.id.title))
            		.setTextColor(TabActivityWithAnimation.this.getResources().getColor(R.drawable.gray2));
                animImage.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                animImage.setVisibility(View.INVISIBLE);
                ((ImageView)mTabWidget.getChildAt(currTabIndex).findViewById(R.id.icon))
                    .setImageResource(SELECTED_IMAGE[currTabIndex]);
                ((TextView)mTabWidget.getChildAt(currTabIndex).findViewById(R.id.title))
                	.setTextColor(TabActivityWithAnimation.this.getResources().getColor(R.color.white));
                mTabWidget.getChildAt(currTabIndex).setBackgroundResource(R.drawable.tab_bottom_selected);
                lastTabIndex = currTabIndex;
            }
        });
         animImage.startAnimation(animation);
     }
	 
	
	
	 private class TabHostListener implements TabHost.OnTabChangeListener {
		 Context context;
		 public TabHostListener(Context context){
			 this.context = context;
		 }
		 public void onTabChanged(String paramString) {
		     lastTabIndex = currTabIndex;
		     currTabIndex = mTabHost.getCurrentTab();
		     if (lastTabIndex != currTabIndex) {
		         showAnimation();
		     }
		     
		 }
	 }
	 
}
