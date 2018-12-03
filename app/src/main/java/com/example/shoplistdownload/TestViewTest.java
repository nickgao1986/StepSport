package com.example.shoplistdownload;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.pic.optimize.R;
public class TestViewTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		TextView test = (TextView)findViewById(android.R.id.text1);
		test.setText(getString(R.string.branded_settings_item_privacy_policy_content));
		//test.setText(getString(R.string.name1,"android",20f));
	}

	
}
