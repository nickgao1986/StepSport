package com.pic.optimize.database;

import android.database.sqlite.SQLiteDatabase;

public abstract class DbBaseTable {

	private static final String TAG = "[RC]RCMDbTable";

	/**
	 * @return the DB table name
	 */
	abstract String getName();

	/**
	 * Creates the DB table according to the DB scheme
	 * 
	 * @param db
	 */
	abstract void onCreate(SQLiteDatabase db);
	
	void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, String tempName) {
        
        //Rename old table to temporary name
		DbUtils.renameTable(db, getName(), tempName);
		
        //Create clear table according to the new scheme
        onCreate(db);
        //Copy content of the matching columns from the old table to the new one
        joinColumns(db, tempName, getName());
        
        //Delete old table
        DbUtils.dropTable(db, tempName);
        
        initTableContent(db);
    }
    
    void initTableContent(SQLiteDatabase db) {
    }
   
    void joinColumns(SQLiteDatabase db, String tempName, String tableName) {
    	DbUtils.joinColumns(db, tempName, tableName);
    }

}
