package com.example.localize;

/**
 * Created by Chiran on 11/4/15.
 */
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_WIFI = "WIFI_DATA";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COODS = "coods";
    public static final String COLUMN_VALUES = "rssiFeed";

    private static final String DATABASE_NAME = "localize.db";
    private static final int DATABASE_VERSION = 7;
    private Context context;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_WIFI + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_COODS + " VARCHAR(255), " + COLUMN_VALUES + " VARCHAR(255));";
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