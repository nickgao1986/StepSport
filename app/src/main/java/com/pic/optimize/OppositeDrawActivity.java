package com.pic.optimize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class OppositeDrawActivity extends Activity {

	public static int screenWidth;
	private DrawView myView;
	private static final int CLEAR_MENU_ID = Menu.FIRST;
	private Paint mPaint;
	ToggleButton settings_toggle_button;

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,OppositeDrawActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_opposite_draw);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		myView = (DrawView) findViewById(R.id.drawview);
		Button btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				myView.clear();
			}
		});

		settings_toggle_button = (ToggleButton) findViewById(R.id.settings_toggle_button);
		settings_toggle_button.setChecked(true);

		settings_toggle_button.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				settings_toggle_button.setChecked(isChecked);
				myView.turnOnOrOffMirrorDraw(isChecked);
			}
		});

	}

}
