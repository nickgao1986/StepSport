package com.pic.optimize.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


final class DbUtils {
    
    private static final String TAG = "DbUtils";
    private static final boolean DEBUG = false;
    
    private static final String SQLITE_STMT_LIST_TABLES =
        "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android%'";
    private static final String SQLITE_TABLE_NAME_COLUMN = "name";
    private static final String SQLITE_STMT_TEMPLATE_LIST_COLUMNS = "SELECT * FROM %s LIMIT 1";
    private static final String SQLITE_STMT_TEMPLATE_DROP_TABLE = "DROP TABLE IF EXISTS %s";
    private static final String SQLITE_STMT_TEMPLATE_RENAME_TABLE = "ALTER TABLE %s RENAME TO %s";
    private static final String SQLITE_STMT_TEMPLATE_COPY_COLUMNS = "INSERT INTO %s (%s) SELECT %s FROM %s";

    
    /**
     * @param db
     * @return Collection object containing table names in the database 
     */
    static Collection<String> listTables(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery(SQLITE_STMT_LIST_TABLES, null);
        if (cursor == null || !cursor.moveToFirst()) {

            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
        
        int table_name_column = cursor.getColumnIndex(SQLITE_TABLE_NAME_COLUMN);
        HashSet<String> tables = new HashSet<String>(cursor.getCount());
        do {
            tables.add(cursor.getString(table_name_column));
        } while (cursor.moveToNext());
        cursor.close();

        
        return tables;
    }

    
    /**
     * @param db
     * @param table
     * @return List of column names in the DB table
     */
    public static List<String> listColumns(SQLiteDatabase db, String table) {
        
        Cursor cursor = db.rawQuery(String.format(SQLITE_STMT_TEMPLATE_LIST_COLUMNS, table), null);
        if (cursor == null) {
            return null;
        }
        
        List<String> columns = Arrays.asList(cursor.getColumnNames());
        cursor.close();

        return columns;
    }
    
    
    /**
     * @param db
     * @param table
     */
    static void dropTable(SQLiteDatabase db, String table) {

        db.execSQL(String.format(SQLITE_STMT_TEMPLATE_DROP_TABLE, table));
    }
    

    static void renameTable(SQLiteDatabase db, String oldName, String newName) {

        db.execSQL(String.format(SQLITE_STMT_TEMPLATE_RENAME_TABLE, oldName, newName));
    }
    
    
    static void joinColumns(SQLiteDatabase db, String oldTable, String newTable) {
        
        //Delete all records in the new table before copying from the old table
        db.delete(newTable, null, null);
        
        //Find columns which exist in both tables
        ArrayList<String> old_columns = new ArrayList<String>(listColumns(db, oldTable));
        List<String> new_columns = listColumns(db, newTable);
        old_columns.retainAll(new_columns);

        String common_columns = TextUtils.join(",", old_columns);
        
        //Copy records from old table to new table
        System.out.println("====DBHelp joinColumns sql ="+String.format(SQLITE_STMT_TEMPLATE_COPY_COLUMNS, newTable, common_columns, common_columns, oldTable));
        db.execSQL(String.format(SQLITE_STMT_TEMPLATE_COPY_COLUMNS, newTable, common_columns, common_columns, oldTable));
    }

}
