package com.example.location;

import java.util.Timer;
import java.util.TimerTask;
import com.pic.optimize.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.provider.Settings;

public class  LocationUtil  {
	
	public final static int GPSTIMEOUT = 0;
	public final static int STATUS_CHANGED = 1;
	public final static int SELECT_LOCATION = 2;
	public final static int DEFAULT_LOCATION_COMPLETED = 3;
	public final static int GET_LOCATIONBUILDINGLIST_FAILED = 4;
	public final static int CANCELGPS_COMPLETED = 5;
	public final static int SELECT_LOCATION_COMPLETED = 6;
	public final static int REFRESHGPS_COMPLETED = 7;
	public final static int REFRESHGPS_NOPROVIDER = 8;
	public final static int GETLOCATION_FAILED = 9;
	
	protected static String sLastLocation = "";
	public static final String NONE_AVAILABLE_LOCATION = "...";
	
	
	private  LocationObsever mLocationObsever = null;
	
	/** location services */
	private  LocationManager mLocationManager = null;

	private  Context mContext = null;
	
	/** adapter for building list*/
	private  BuildingListAdapter mBuildingListAdapter = new BuildingListAdapter();


	/** nearby building list*/
	public  JSONArray mLocationBuildingList = null;
	
	/** location services */
	private  MyLocationListener mLocationListener = null;
	
	/** latitude and longitude of current location*/
	public static String mLat = "";
	public static String mLon = "";

	/** time out for GPS location update */
	private  Timer mGpsTimer = new Timer();	
	/** TimerTask for time out of GPS location update */
	private  GpsTimeOutTask mGpsTimeOutTask = new GpsTimeOutTask();
	/** GPS location update time out in milliseconds*/
	private  long mGpsTimeOut = 180000;//3 minutes
	/** handler for time out of GPS location update*/
	private Handler mGpsTimerHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (mLocationManager == null) {
				return;
			}
			if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				System.out.println("=====use network to get location");
				mLocationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						mLocationListener);
			} else {
				mLocationManager.removeUpdates(mLocationListener);

				// mLocationObsever.notifyChange(SETADDLOCATIONBUTTONSTATE_1_SETLOCATIONDES_1,null);
				if (mLocationObsever != null) {
					mLocationObsever.notifyChange(GPSTIMEOUT, null);
				}
			}
		}
	};
	
	
	public void initiLocationUtil (Context context, LocationObsever locationobsever){
		mLocationObsever = locationobsever;	
		mContext = context;
		mLocationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);	
		mLocationListener = new MyLocationListener();	
	}
	
	
	
	private  class MyLocationListener implements LocationListener{
		private boolean mLocationReceived = false;
		@Override
		public void onLocationChanged(Location location) {
			if(location != null && !mLocationReceived){
				mLocationReceived = true;
				String lon = String.valueOf(location.getLongitude());
				String lat = String.valueOf(location.getLatitude());	
				if(mLocationObsever != null){
					mLocationObsever.notifyChange(DEFAULT_LOCATION_COMPLETED, "经纬度"+lat+","+lon);
				}
			}else  if(location == null){
				if(mLocationObsever != null){
					mLocationObsever.notifyChange(GETLOCATION_FAILED, null);	
				}
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
			//if GPS provider is not accessible, try network provider
			if(provider.equals(LocationManager.GPS_PROVIDER) && status != LocationProvider.AVAILABLE){
				if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
					mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
							0, 
							0, 
							mLocationListener);
				}else{
					mLocationManager.removeUpdates(mLocationListener);

					if(mLocationObsever != null){
						mLocationObsever.notifyChange(STATUS_CHANGED, null);
					}
				}
			}
		}
	}
	

	public void RefreshGPS(boolean calledByCreate){	
		
		mLocationManager.removeUpdates(mLocationListener);
		boolean providerEnable = true;
		boolean showLocationServiceDisableNotice = true;
		if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					0, 
					0, 
					mLocationListener);
			showLocationServiceDisableNotice = false;
			
			//start time out timer
			mGpsTimer = new Timer(); 
			mGpsTimeOutTask = new GpsTimeOutTask();
			mGpsTimer.schedule(mGpsTimeOutTask, mGpsTimeOut);
			
		}
		
		if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
					0, 
					0, 
					mLocationListener);
			showLocationServiceDisableNotice = false;
			providerEnable = true;
		}
				
		if(providerEnable){
			if(mLocationObsever != null){
				mLocationObsever.notifyChange(REFRESHGPS_COMPLETED, null);
			}

		}else{
			if(mLocationObsever != null){
				mLocationObsever.notifyChange(REFRESHGPS_NOPROVIDER, null);
			}

		}
		
		if(showLocationServiceDisableNotice){
			showLocationServiceDisabledDialog();
		}
		
	}
	
	
	/**
	 * cancel operations of refreshing GPS
     */
	public  void cancelRefreshGPS(){
		if(mLocationManager != null){
			mLocationManager.removeUpdates(mLocationListener);
		}
		
		if(mLocationObsever != null){
			mLocationObsever.notifyChange(CANCELGPS_COMPLETED, null);	
		}
	}	
	
	
	
	public  void destroy (){
		if(mLocationManager != null){
		     mLocationManager.removeUpdates(mLocationListener);
		}
		
		if(mGpsTimer != null){
			mGpsTimer.cancel();
		} 
		
		cancelRefreshGPS();
		
		mContext = null;
		mLocationObsever = null;
		
		mLocationBuildingList = null;
		
		System.gc();
	}
	
	
	/**
	 * this class is TimerTask for time out of GPS location update
	 */
	private  class GpsTimeOutTask extends TimerTask{
		public void run() {
			Message message = new Message();       
			message.what = 1;       
			mGpsTimerHandler.sendMessage(message);     
		}  
	}
	
	
	public AlertDialog showLocationServiceDisabledDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.location_service_disabled_notice);
		builder.setNeutralButton("Cancel", null).setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				try {
					mContext.startActivity(intent);
				} catch (ActivityNotFoundException e) {
					intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
					mContext.startActivity(intent);
				}
			}
		});
		return builder.create();
	}
	
	public static String getLastLocation() {
		return sLastLocation;
	}
	
	public static void setLastLocation(String strLocation) {
		sLastLocation = strLocation;
	}
	
	
	/**
     * this class is the adapter class for building list dialog
     */
	private  class BuildingListAdapter extends BaseAdapter{

		/** current selected index*/
		private int mSelectIndex = 0;
		
		public void setSelectIndex(int index){
			mSelectIndex = index;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(mLocationBuildingList != null){
				return mLocationBuildingList.length();
			}else{
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;   
			
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);    
			view = inflater.inflate(R.layout.location_building_list_item,null);
			
			try {
				if(mLocationBuildingList != null) {
					JSONObject item = mLocationBuildingList.getJSONObject(position);
					String name = item.getString("name");
					String address = item.getString("address");
					TextView nameView = (TextView)view.findViewById(R.id.building_name);
					nameView.setText(name);
					TextView addressView = (TextView)view.findViewById(R.id.building_address);
					addressView.setText(address);
					
					if(mSelectIndex == position){
						view.findViewById(R.id.building_location_mark).setVisibility(View.VISIBLE);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return view;
		}
		
	}	

}