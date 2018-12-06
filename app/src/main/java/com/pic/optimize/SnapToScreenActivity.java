package com.pic.optimize;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.view.MyGroup;

public class SnapToScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.snap_to_screen);
		int[] images = new int[]{R.drawable.help_01,R.drawable.help_02,R.drawable.help_03};
		MyGroup myGroup = (MyGroup) findViewById(R.id.workspace);
		for (int i = 0; i < 3; i++) {
			final View item = getLayoutInflater().inflate(
					R.layout.introduction_item_test, null);

			myGroup.addView(item);
			final ImageView imageView = (ImageView) item
					.findViewById(R.id.introduction_image_view);
			try {
				imageView.setImageResource(images[i]);
			} catch (OutOfMemoryError e) {
			}

		}
	}

}
