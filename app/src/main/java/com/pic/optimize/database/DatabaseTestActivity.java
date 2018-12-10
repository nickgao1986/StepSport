package com.pic.optimize.database;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


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
		copyDatabase();
		DbHelper.getInstance(this);
		addDataToDataBase();
		queryDataBase1();
	}

	private void copyDatabase() {
		File file = new File("/data/data/com.pic.optimize/databases");
		String[] array = file.list();
		for(int i=0;i<array.length;i++) {
			Log.d("TAG","=====array[i]="+array[i]);
		}
		File f = new File("/data/data/com.pic.optimize/databases/my.db");
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		File o = new File(sdcardPath+"/my.db");
		if(f.exists()) {
			FileChannel outF;
			Log.d("TAG","=====file exsit");

			try {
				outF = new FileOutputStream(o).getChannel();
				new FileInputStream(f).getChannel().transferTo(0, f.length(),outF);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(this, "完成", Toast.LENGTH_SHORT).show();
		}
	}



	private void addDataToDataBase() {
		try {
			ContentResolver resolver = this.getContentResolver();
			ContentValues values = new ContentValues();
			values.put(DataBaseClass.MyTest1Table.TEST1, "cc");
			values.put(DataBaseClass.MyTest1Table.account_ID, 1002);
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
		}
		cursor.close();
	}


}
