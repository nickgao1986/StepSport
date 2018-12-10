package com.pic.optimize.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


final class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "[RC]RCMDbHelper";

    private static final String TEMP_SUFFIX = "_temp_";
    
    private SQLiteDatabase db_r = null; // readable database
    private SQLiteDatabase db_w = null; // writable database
    
    private static DbHelper dbHelper;
    
    private DbHelper(Context context) {
        super(context, DataBaseClass.DB_FILE, null, DataBaseClass.DB_VERSION);
    }
    
    public static synchronized DbHelper getInstance(Context context) {
    	if (dbHelper == null) {
    		dbHelper = new DbHelper(context);
    	}
    	return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
        Collection<DbBaseTable> tables = DataBaseClass.sRCMDbTables.values();
        Iterator<DbBaseTable> iterator = tables.iterator();
        System.out.println("====DBHelp oncreate");
        try {
            db.beginTransaction();
            while (iterator.hasNext()) {
                iterator.next().onCreate(db);
            }
            db.setTransactionSuccessful();
        } catch (Throwable e) {

            throw new RuntimeException("DB creation failed: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Get table names in the old DB
        Collection<String> old_tables = DbUtils.listTables(db);
        if (old_tables == null || old_tables.size() == 0) {
            onCreate(db);
            return;
        }
        
        //Get table names in the new DB
        Set<String> new_tables = DataBaseClass.sRCMDbTables.keySet();
        
        try {
            db.beginTransaction();
            //Remove old tables which are not in the new DB scheme
            HashSet<String> obsolete_tables = new HashSet<String>();
            for (String table : old_tables) {
                if (!new_tables.contains(table)) {
                	System.out.println("====DBHelp onUpgrade droptable table="+table);
                    DbUtils.dropTable(db, table);
                    obsolete_tables.add(table);
                }
            }
            old_tables.removeAll(obsolete_tables);
    
            //Create and upgrade new tables 
            DbBaseTable table_descriptor; 
            for (String table : new_tables) {
                table_descriptor = DataBaseClass.sRCMDbTables.get(table);
                
                //Check if the new table exists in the old DB
                if (old_tables.contains(table)) {
                    String temp_name = getTempTableName(table, old_tables, new_tables);
                    System.out.println("====DBHelp onUpgrade temp_name ="+temp_name);
                    table_descriptor.onUpgrade(db, oldVersion, newVersion, temp_name);
                } else {
                    table_descriptor.onCreate(db);
                }
            }
            db.setTransactionSuccessful();
        } catch (Throwable e) {
            
            throw new RuntimeException("DB upgrade failed: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    
    private String getTempTableName(String tableName, Collection<String> oldTableNames, Set<String> newTableNames) {
        String temp_name_base = tableName + TEMP_SUFFIX;
         
        if (!oldTableNames.contains(temp_name_base) && !newTableNames.contains(temp_name_base)) {
            return temp_name_base;
        }
    
        Random random = new Random();
        String temp_name;
        for (;;) {
            temp_name = temp_name_base + random.nextInt();
            if (!oldTableNames.contains(temp_name) && !newTableNames.contains(temp_name)) {
                return temp_name;
            }
        }
    }

    
    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (db_r == null || !db_r.isOpen()) {
            try {
                db_r = super.getReadableDatabase();
            } catch (SQLiteException e) {
                //TODO Implement proper error handling
                db_r = null;
                
                throw e;
            }
        }            
        return db_r;
    }

    
    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (db_w == null || !db_w.isOpen() || db_w.isReadOnly()) {
            try {
                db_w = super.getWritableDatabase();
            } catch (SQLiteException e) {
                //TODO Implement proper error handling
                db_w = null;
                
                throw e;
            }
        }            
        return db_w;
    }

}


