package com.example.shoplistdownload;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.pic.optimize.R;
import com.example.view.ExpandableView;
import com.example.view.ExpandableView.ExpandItemClickListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetailShopActivity extends Activity  implements ExpandItemClickListener{

	private ExpandableView mExpandableView = null;
	private List<View> mItemViews = null;
	private TypedArray mLauncherDrawables = null;
	private View mRootView;
	boolean hasMeasured = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mRootView = LayoutInflater.from(this).inflate(R.layout.detail_activity, null);
		setContentView(mRootView);
		prepareLauncherView();
		Intent intent = getIntent();
		ShopData data = intent.getParcelableExtra("detail");
		TextView detail_id = (TextView) findViewById(R.id.detail_id);
		detail_id.setText(data.name);
		Button back = (Button) findViewById(R.id.back);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		Button btn_settings = (Button) findViewById(R.id.btn_settings);
		btn_settings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(DetailShopActivity.this, AnimationTest.class);
				DetailShopActivity.this.startActivity(intent);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

	}
	
	private void prepareLauncherView() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int height = dm.heightPixels;
		
		mLauncherDrawables = getResources().obtainTypedArray(R.array.launcher_item_drawables);
		mExpandableView = (ExpandableView) findViewById(R.id.launcher_expand_view);
		mItemViews = new ArrayList<View>();
		for (int i = 0; i < mLauncherDrawables.length(); i++) {
			addLauncherItem(i);
		}
		mExpandableView.setOnExpandItemClickListener(this);
		
		int contentHeight = height - getStatusBarHeight();
		mExpandableView.setScreenSize(contentHeight, screenWidth);
		mExpandableView.addListView(mItemViews);
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		mExpandableView.setRestart(true);
	}

	private int getStatusBarHeight() {

		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {

			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}

	private void addLauncherItem(int i) {
		View view = LayoutInflater.from(this).inflate(R.layout.launch_bar_item12, null).findViewById(R.id.launcher_content);
		view.setBackgroundDrawable(mLauncherDrawables.getDrawable(i));
		mItemViews.add(view);
	}

	@Override
	public void onExpandItemClick(View parentView, View view, int position, int maxNum, int line) {
		if (!mExpandableView.isInAnimation()) {
			if (position != maxNum) {
				mExpandableView.excuteAnimation(false);
			} else {
				mExpandableView.excuteAnimation(true);
			}
		}
	}

}
