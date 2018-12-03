package com.example.shoplistdownload;


import com.example.location.LocationObsever;
import com.example.location.LocationUtil;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.pic.optimize.R;


public class LocationTest extends Activity {

	private DiaryLocationObsever mObs = null;
	LocationUtil mLocationUtil;
	private TextView mLocationDes = null;
	private Button mAddLocationButton = null;
	private View mLocationLayout = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location_test);
		
		mObs = new DiaryLocationObsever();	
		mLocationUtil = new LocationUtil();
		mLocationUtil.initiLocationUtil(this, this.mObs);
		
		mLocationDes = (TextView)findViewById(R.id.write_diary_location_des);
		mAddLocationButton = (Button)findViewById(R.id.write_diary_add_gps);
		mLocationLayout = findViewById(R.id.write_diary_location_layout);
		mAddLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLocationUtil.RefreshGPS(true);
			}
		});
		
		Button Go_to_map = (Button)findViewById(R.id.go_to_map);
		Go_to_map.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
//				String text = mLocationDes.getText().toString();
//				String[] array = text.split(",");
//				String lat = array[0];
//				String lon = array[1];
//				intent.putExtra("lat", Double.valueOf(lat));
//				intent.putExtra("lon", Double.valueOf(lon));
			}
		});

	}
	
	private class DiaryLocationObsever extends LocationObsever{
		@Override
		public void notifyChange(int arg, String des){
			System.out.println("====notifyChange arg="+arg+"des="+des);
			switch (arg) {
			case LocationUtil.GPSTIMEOUT:
	  			setLocationDes(1, null);
			break;
  			case LocationUtil.STATUS_CHANGED:
    		    //setLocationDes(1, null);
				break;
			case LocationUtil.SELECT_LOCATION:	
				break;
			case LocationUtil.DEFAULT_LOCATION_COMPLETED:			
				setLocationDes(2, des);
				break;
			case LocationUtil.GET_LOCATIONBUILDINGLIST_FAILED:
				setLocationDes(1, null);
				break;				
			case LocationUtil.CANCELGPS_COMPLETED:
				System.out.println("====get location des="+des);
				setLocationDes(0,null);
				break;
			case LocationUtil.SELECT_LOCATION_COMPLETED:
				setLocationDes(2, des);
				mLocationLayout.setVisibility(View.VISIBLE);
				break;
			case LocationUtil.REFRESHGPS_COMPLETED:
				setLocationDes(0, null);
				break;
			case LocationUtil.REFRESHGPS_NOPROVIDER:
				setLocationDes(1, null);
				break;
			case LocationUtil.GETLOCATION_FAILED:
				setLocationDes(1, null);
				break;				
			default:
				break;
		}
	   }
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationUtil.destroy();
		mLocationUtil = null;
	}



	private void setLocationDes(int state, String des){
		switch(state){
		case 0:
			if (LocationUtil.getLastLocation().length() > 0) {
				mLocationDes.setText(LocationUtil.getLastLocation());
			}
			else {
				mLocationDes.setText(LocationUtil.NONE_AVAILABLE_LOCATION);
			}
			break;
		case 1:
			String str = this.getString(R.string.cannot_find_location);
			mLocationDes.setText(str);
			break;
		case 2:
			if(des != null){
				mLocationDes.setText(des);
			}else{
				mLocationDes.setText(des);
			}
			break;
		default:
			break;
		}
	}
	
	

}
