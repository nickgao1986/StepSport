package com.ringcentral.android.utils.ui.menu;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pic.optimize.R;

public class RCTabView extends RelativeLayout {
	private static final int MAX_MESSAGES_COUNT_TO_BE_DISPLAYED = 99;
	public int position;

	public RCTabView(Context context, int position) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.launch_bar_item, this);
		this.setBackgroundColor(getResources().getColor(
				R.color.transparentColor));
		this.setDuplicateParentStateEnabled(true);
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void changeSelectedState(boolean state) {
		this.findViewById(R.id.tab_main_text).setSelected(state);
	}

	public void updateIndicator(int count) {
		TextView textView = (TextView) this
				.findViewById(R.id.tab_indicator_counter);
		if (count > 0) {
			textView.setText(count <= MAX_MESSAGES_COUNT_TO_BE_DISPLAYED ? String
					.valueOf(count) : getContext().getString(
					R.string.menu_more_than_99));
			textView.setVisibility(View.VISIBLE);
		} else {
			textView.setVisibility(View.GONE);
		}
	}
}
