package com.pic.optimize.database;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MyProvider extends ContentProvider {

    static final boolean DEBUG_ENBL = false; 	

    static final String TAG = "[RC]MyProvider";

    /* URI authority string */
    public static final String AUTHORITY = "com.pic.optimize.provider.myprovider";

    /* URI paths names */
    public static final String MyTest1Table = "MyTest1Table";
    public static final String MyTest2Table = "MyTest2Table";
    
    
	private DbHelper dbHelper;
	
	@Override
	public boolean onCreate() {
		dbHelper = DbHelper.getInstance(getContext());
		return true;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int match = sUriMatcher.match(uri);
		SQLiteDatabase db;
		long rowId;

		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException e) {

			throw e;
		}

		synchronized (dbHelper) {
			try {
				rowId = db.insert(tableName(match), null, values);
			} catch (SQLException e) {

				throw e;
			}
		}

		uri = ContentUris.withAppendedId(UriHelper.removeQuery(uri), rowId);
		getContext().getContentResolver().notifyChange(uri, null);
		return uri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = sUriMatcher.match(uri);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(tableName(match));
		SQLiteDatabase db;
		try {
			db = dbHelper.getReadableDatabase();
		} catch (SQLiteException e) {
			// TODO Implement proper error handling

			throw e;
		}

		Cursor cursor;
		synchronized (dbHelper) {
			try {
				cursor = qb.query(db, projection, selection, selectionArgs,
						null, null, sortOrder);
			} catch (Throwable e) {

				throw new RuntimeException("Exception at db query: "
						+ e.getMessage());
			}
		}

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	
	private String tableName(int uri_match) {
		switch (uri_match) {
		case MyTest1Table_MATCH:
			return DataBaseClass.MyTest1Table.getInstance().getName();

		case MyTest2Table_MATCH:
			return DataBaseClass.MyTest2Table.getInstance().getName();
		}
		return null;
	}

	 /* UriMatcher codes */
    private static final int MyTest1Table_MATCH = 10;
    private static final int MyTest2Table_MATCH = 11;
    
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, MyTest1Table, MyTest1Table_MATCH);
        sUriMatcher.addURI(AUTHORITY, MyTest2Table, MyTest2Table_MATCH);
    }
}
