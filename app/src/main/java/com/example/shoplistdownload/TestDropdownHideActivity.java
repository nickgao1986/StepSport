package com.example.shoplistdownload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pic.optimize.R;

public class TestDropdownHideActivity extends Activity {

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,TestDropdownHideActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
	}

	
}
