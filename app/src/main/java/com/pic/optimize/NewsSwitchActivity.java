package com.pic.optimize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsSwitchActivity extends Activity implements OnClickListener{

	private LinearLayout columnTitleLayout;
	private ArrayList<String> array = new ArrayList<String>();
	private ImageView animImage;
	private ImageButton scrollToRight;
	private ImageButton scrollToLeft;
	private int currTabIndex;
	private int lastTabIndex;

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,NewsSwitchActivity.class);
		context.startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.news_switch_main);

		animImage = (ImageView)findViewById(R.id.column_slide_bar);
		scrollToLeft = (ImageButton)findViewById(R.id.column_to_left);
	    scrollToRight = (ImageButton)findViewById(R.id.column_to_right);
		columnTitleLayout = (LinearLayout) findViewById(R.id.column_title_layout);
		
		updateResource();

	}

	private void updateResource() {
		array.clear();
		String[] resource = this.getResources().getStringArray(R.array.all_choice);
		for (int j = 0; j < resource.length; j++) {
			String name = resource[j];
			array.add(name);
		}
		initTab();

	}

	private void initTab() {
		this.columnTitleLayout.removeAllViews();
		int j = this.array.size();
		if (j <= 5) {
			this.scrollToRight.setVisibility(View.INVISIBLE);
			this.scrollToLeft.setVisibility(View.INVISIBLE);

		}
		currTabIndex = 0;
		int i = 0;
		animImage.setBackgroundResource(R.drawable.slidebar);
		for (i = 0; i < array.size(); i++) {
			String str = array.get(i);
			TextView ColumnTextView = new TextView(this);
			ColumnTextView.setText(str);
			ColumnTextView.setTag(i);
			ColumnTextView.setPadding(18, 2, 15, 4);
			ColumnTextView.setOnClickListener(this);
			ColumnTextView.setTextAppearance(this, R.style.column_tx_style);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			columnTitleLayout.addView(ColumnTextView, params);
		}

		TextView MoreColumnTextView = new TextView(this);
		MoreColumnTextView.setTag(i);
		CharSequence localCharSequence = getResources().getText(R.string.more_column);
		MoreColumnTextView.setText(localCharSequence);
		MoreColumnTextView.setPadding(18, 2, 15, 4);
		MoreColumnTextView.setTextAppearance(this, R.style.column_tx_style);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		columnTitleLayout.addView(MoreColumnTextView, params);
		
	}
	
	private void showAnimation() {

		if (lastTabIndex == currTabIndex) {
			return;
		}
		((TextView) columnTitleLayout.getChildAt(lastTabIndex)).setTextColor(R.drawable.white);
		int widgetItemWidth = ((TextView) columnTitleLayout.getChildAt(lastTabIndex)).getWidth();
		int fromX = lastTabIndex * widgetItemWidth;
		int toX = currTabIndex * widgetItemWidth;
		Log.v("test", "widgetItemWidth" + widgetItemWidth + "fromX:" + fromX + " toX:" + toX);
		TranslateAnimation animation = new TranslateAnimation(fromX, toX, 0, 0);
		animation.setDuration(500);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				((TextView) columnTitleLayout.getChildAt(lastTabIndex)).setTextColor(NewsSwitchActivity.this.getResources().getColor(R.drawable.gray2));
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				((TextView) columnTitleLayout.getChildAt(currTabIndex)).setTextColor(NewsSwitchActivity.this.getResources().getColor(R.drawable.white));
				lastTabIndex = currTabIndex;
			}
		});
		animImage.startAnimation(animation);
	}

    

	@Override
	public void onClick(View v) {
		int k = (Integer)v.getTag();
		lastTabIndex = currTabIndex;
		currTabIndex = k;

		String text = ((TextView) v).getText().toString();

		if (lastTabIndex != currTabIndex) {

			if (currTabIndex == array.size()) {
				return;
			}
			showAnimation();

		}
	}

}
