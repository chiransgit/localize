package com.example.localize;

/**
 * Created by Chiran on 11/4/15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteDatabaseAdapter {

    MySQLiteHelper sqlhelper;

    public MySQLiteDatabaseAdapter(Context context){
        sqlhelper = new MySQLiteHelper(context);
    }


    public String getRow(String coods){
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        String [] columns = {sqlhelper.COLUMN_VALUES};
        Cursor cursor = db.query(sqlhelper.TABLE_WIFI, columns, coods, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while(cursor.moveToNext()){
            String rssiValues = cursor.getString(0);
            buffer.append(rssiValues);
        }
        return buffer.toString();

    }


    public long insertData(String coods, String values)
    {
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(sqlhelper.COLUMN_COODS, coods);
        contentValues.put(sqlhelper.COLUMN_VALUES, values);
        long id = db.insertWithOnConflict(sqlhelper.TABLE_WIFI, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return id;
    }


    public String getAllData(){
        SQLiteDatabase db = sqlhelper.getWritableDatabase();

        String [] columns = {sqlhelper.COLUMN_COODS, sqlhelper.COLUMN_VALUES};
        Cursor cursor = db.query(sqlhelper.TABLE_WIFI, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while(cursor.moveToNext()){
            String coordinates = cursor.getString(0);
            String rssi = cursor.getString(1);
            buffer.append(rssi + coordinates+System.getProperty("line.separator"));
        }
        return buffer.toString();
    }

    public class MySQLiteHelper extends SQLiteOpenHelper{

        public static final String TABLE_WIFI = "WIFI_DATA";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_COODS = "coods";
        public static final String COLUMN_VALUES = "rssiFeed";

        private static final String DATABASE_NAME = "localize.db";
        private static final int DATABASE_VERSION = 11;
        private Context context;

        // Database creation sql statement
        private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_WIFI + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_COODS + " TEXT NOT NULL UNIQUE, " + COLUMN_VALUES + " TEXT NOT NULL);";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_WIFI;

        public MySQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
            Message.message(context, "Constructor Called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
                Message.message(context, "On Create Called");
            } catch (SQLException e) {
                Message.message(context, ""+e);
                Log.d("SQLITE EXCEPTION", e.getLocalizedMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(MySQLiteHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            Message.message(context, "On Upgrade Called");
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch (SQLException e) {
                Message.message(context, "" + e);
                Log.d("SQLITE EXCEPTION", e.getLocalizedMessage());
            }
        }

    }



}