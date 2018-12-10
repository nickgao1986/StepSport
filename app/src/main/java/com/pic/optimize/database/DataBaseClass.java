package com.pic.optimize.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.LinkedHashMap;


public final class DataBaseClass {

    private DataBaseClass() {};

    public static final int DB_VERSION = 2;
    
    static final String DB_FILE = "my.db";

    public interface RCMColumns {
        public static final String account_ID = "account_id";                   //INTEGER (long)
    }
    
    public static final class MyTest1Table extends DbBaseTable implements BaseColumns, RCMColumns {

		private MyTest1Table() {
		}

		private static final MyTest1Table sInstance = new MyTest1Table();

		static MyTest1Table getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "Mytest1";

		/* Columns */
		public static final String TEST1 					= "test1";
		public static final String TEST2 						= "test2";
		public static final String TEST3				= "test3";

		private static final String CREATE_TABLE_STMT = "CREATE TABLE IF NOT EXISTS " 
				+ TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ account_ID + " INTEGER, " 
				+ TEST1 + " TEXT, "
//				+ TEST2 + " TEXT, "
				+ TEST3 + " TEXT"
				+ ");";
		
		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}
    
    
    public static final class MyTest2Table extends DbBaseTable implements BaseColumns, RCMColumns {

		private MyTest2Table() {
		}

		private static final MyTest2Table sInstance = new MyTest2Table();

		static MyTest2Table getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "Mytest2";

		/* Columns */
		public static final String TEST4 					= "test4";
		public static final String TEST5						= "test5";
		public static final String TEST6				= "test6";

		private static final String CREATE_TABLE_STMT = "CREATE TABLE IF NOT EXISTS " 
				+ TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ account_ID + " INTEGER, " 
				+ TEST4 + " TEXT, "
				+ TEST5 + " TEXT, "
				+ TEST6 + " TEXT"
				+ ");";
		
		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

 static LinkedHashMap<String, DbBaseTable> sRCMDbTables = new LinkedHashMap<String, DbBaseTable>();
    
    static {
        sRCMDbTables.put(MyTest1Table.getInstance().getName(), MyTest1Table.getInstance());
      //  sRCMDbTables.put(MyTest2Table.getInstance().getName(), MyTest2Table.getInstance());
    }
}
