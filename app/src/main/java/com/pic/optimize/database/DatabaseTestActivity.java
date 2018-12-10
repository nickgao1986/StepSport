package com.pic.optimize.database;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;

import com.pic.optimize.UploadPhotoActivity;

public class DatabaseTestActivity extends Activity {

	public static void startActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context,DatabaseTestActivity.class);
		context.startActivity(intent);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		queryDataBase1();
	}
	
	
	private void addDataToDataBase() {
		try {
			ContentResolver resolver = this.getContentResolver();
			ContentValues values = new ContentValues();
			values.put(DataBaseClass.MyTest1Table.TEST1, "cc");
			values.put(DataBaseClass.MyTest1Table.account_ID, 1002);
			values.put(DataBaseClass.MyTest1Table.TEST2, "dd");
			resolver.insert(UriHelper.getUri(MyProvider.MyTest1Table),
					values);
		} catch (Throwable error) {
		}
	}
	
	
	
	private void queryDataBase1() {
		ContentResolver resolver = this.getContentResolver();

		Cursor cursor = resolver.query(UriHelper.getUri(MyProvider.MyTest1Table), null, null, null, null);
		cursor.moveToPosition(-1);
		while(cursor.moveToNext()) {
			int account = cursor.getInt(1);
			String test1 = cursor.getString(2);
			System.out.println("====account="+account+"test1="+test1);
		}
		cursor.close();
	}


}
