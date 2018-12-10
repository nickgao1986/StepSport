package com.pic.optimize;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout.LayoutParams;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class UploadPhotoActivity extends Activity{

	private Context mContext = null;
	private String mFilePathName;
	private String mUploadFilePathName = "";
	Button cancelButton = null;
	private boolean mIsUploading = false;
	private UploadPhotoTask mUploadPhotoTask = null;
	private String filePath;
	private String mFileName;
	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,UploadPhotoActivity.class);
		context.startActivity(intent);
	}


	/** Handler to get notify from upload photo engine*/
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HttpUploadedFile.POST_PROGRESS_NOTIFY:
				int completePercent = msg.arg1;
				HandleUploadProgress(completePercent);
				break;
			default:
				break;
			}
		}
	};
	
	/**
     * handle the uploading progress notification
     */
	private void HandleUploadProgress(int completePercent){
		View containerView = findViewById(R.id.photo_upload_progress_bar_container);
		int maxLen = containerView.getWidth();
		int barLen = (completePercent * maxLen) / 100;
		View barView = findViewById(R.id.photo_upload_progress_bar);
		LayoutParams params = new LayoutParams();
		params.height = LayoutParams.FILL_PARENT;
		params.width = barLen;
		barView.setLayoutParams(params);
	}
	
	/**
     * show or hide progress bar
     */
	private void showProgressBar(boolean show){
		View view = findViewById(R.id.photo_upload_progress_layout);
		if(show){
			view.setVisibility(View.VISIBLE);
			View bar = view.findViewById(R.id.photo_upload_progress_bar);
			LayoutParams params = new LayoutParams();
			params.height = LayoutParams.FILL_PARENT;
			params.width = 3;
			bar.setLayoutParams(params);
		}else{
			view.setVisibility(View.GONE);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo_upload_activity);
		filePath = android.os.Environment.getExternalStorageDirectory()+"";
		mFileName = "11.jpeg";
		WriteToSD();
		mContext= this;
		mFilePathName = getStoredPicPath();
		LoadPhotoTask task = new LoadPhotoTask();
		task.execute();
		
		// set button action
		cancelButton = (Button) findViewById(R.id.photo_upload_button_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				doCancelUploadPhoto();

			}

		});

		Button uploadButton = (Button) findViewById(R.id.photo_upload_button_upload);
		uploadButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				hideInputMethod();
				mUploadPhotoTask = new UploadPhotoTask();
				String[] params = new String[3];
				mUploadPhotoTask.execute();
				mIsUploading = true;
				showProgressBar(true);

			}

		});
	}

	public void WriteToSD(){
		if(!isExist()){
			write();
		}
	}
	private void write(){
		InputStream inputStream;
		try {
			inputStream = this.getResources().getAssets().open(mFileName);
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(filePath + "/"+mFileName);
			byte[] buffer = new byte[512];
			int count = 0;
			while((count = inputStream.read(buffer)) > 0){
				fileOutputStream.write(buffer, 0 ,count);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
			inputStream.close();
			System.out.println("success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private boolean isExist(){
		File file = new File(filePath + "/"+mFileName);
		if(file.exists()){
			return true;
		}else{
			return false;
		}
	}


	private void doCancelUploadPhoto(){
		if(mIsUploading){
			if(mUploadPhotoTask != null){
				HttpUploadedFile.getInstance().cancel();
				mUploadPhotoTask.cancel(true);
				mIsUploading = false;
				showProgressBar(false);
			}
		}else{
			finish();
		}
	}
	
	private void hideInputMethod(){
		View view = getCurrentFocus();
		if(view != null){
		    ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	private String getStoredPicPath() {
		String fileName = "11.jpeg";
		return Environment.getExternalStorageDirectory()+"/"+fileName;
	}
	
	
	private String SaveBitmapToFile(Bitmap bmp){
		if (null == bmp) {
			return null;
		}
		String fileName = "upload_tmp.jpg";
		File f = this.getFileStreamPath(fileName);//data/data/com.example.tianqitongtest/files/upload_tmp.jpg
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream ostream;
		try {
			int targetWidth = 780;
			int w = bmp.getWidth();
			if (w > targetWidth) {
				int h = bmp.getHeight();
				int targetHeight = (targetWidth * h) / w;
				bmp = Bitmap.createScaledBitmap(bmp, targetWidth, targetHeight,
						true);
			}
			ostream = this.openFileOutput(fileName, MODE_PRIVATE);
			bmp.compress(Bitmap.CompressFormat.JPEG, 70, ostream);
			ostream.flush();
			ostream.close();
			ostream = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f.getAbsolutePath();
	}
	
	/**
     * this class is the task class for upload photo
     */
	private class UploadPhotoTask extends AsyncTask<String, Void, Boolean>{
		
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Boolean doInBackground(String... params) {
    		return HttpUploadedFile.getInstance().doUploadPhoto(getApplicationContext(), mUploadFilePathName, mHandler);
    	}  
    	
    	protected void onPostExecute(Boolean result){
    		mIsUploading = false;
    		showProgressBar(false);
			if(result){
				Toast.makeText(UploadPhotoActivity.this, R.string.upload_photo_fail, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(UploadPhotoActivity.this, R.string.upload_photo_fail, Toast.LENGTH_SHORT).show();

			}
    	}
	}
	
	private class LoadPhotoTask extends AsyncTask<Void, Void, Boolean>{
		private Bitmap mLoadedBitmap = null;
		
    	protected Boolean doInBackground(Void... params) {
    		try {
				if(mFilePathName.length() > 0){
					File file = new File(mFilePathName);
					if (!file.exists()) {
						return false;
					}
					BitmapFactory.Options opt = new BitmapFactory.Options();
					long fileSize = file.length();
					int maxSize = 2*1024 * 1024;
					if(fileSize <= maxSize){
						opt.inSampleSize = 1;
					}else if(fileSize <= maxSize * 4){
						opt.inSampleSize = 2;
					}else{
						long times = fileSize / maxSize;
						opt.inSampleSize = (int)(Math.log(times) / Math.log(2.0)) + 1;
					}
					try{
						mLoadedBitmap = BitmapFactory.decodeFile(mFilePathName,opt);
						mUploadFilePathName = SaveBitmapToFile(mLoadedBitmap);
					}catch(OutOfMemoryError e){
						Toast.makeText(UploadPhotoActivity.this, 
								   getResources().getString(R.string.no_memory_to_view_photo), 
								   Toast.LENGTH_SHORT).show();
						UploadPhotoActivity.this.finish();
						
					}
				}
				return true;
			} catch (Exception e) {
				Log.e("UploadPhotoActivity", "doInBackground", e);
				return false;
			}
    	}  
    	
    	
    	protected void onPostExecute(Boolean result){
    		try {
				showLoadPreviewProgressBar(false);
				if(mLoadedBitmap != null){
					ImageView IamgePreView = (ImageView)findViewById(R.id.photo_upload_preview_image);
					IamgePreView.setImageBitmap(mLoadedBitmap);
				}else{
					
				}
				mLoadedBitmap = null;
			} catch (Exception e) {
				Log.e("UploadPhotoActivity", "onPostExecute", e);
			}
    	}
    }

	
	private void showLoadPreviewProgressBar(boolean show){
		View view = findViewById(R.id.photo_upload_progress_item);
		if(show){
			view.setVisibility(View.VISIBLE);
		}else{
			view.setVisibility(View.GONE);
		}
		
		Button  UploadButton = (Button)findViewById(R.id.photo_upload_button_upload);
		if(!show){
			UploadButton.setEnabled(true);
			UploadButton.setTextColor(getResources().getColor(R.drawable.black));
		}else{
			UploadButton.setTextColor(getResources().getColor(R.drawable.gray2));
			UploadButton.setEnabled(false);
		}
		
	}

}
