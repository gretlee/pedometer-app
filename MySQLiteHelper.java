package com.demo.example.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_DITE = "Dite";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MINUTS = "minutes";
    public static final String COLUMN_HOURS = "hours";
    public static final String COLUMN_DAY_OF_WEEK = "dayofweek";
    public static final String COLUMN_DAY_OF_MONTH = "dayofmonth";
    public static final String COLUMN_DAY_OF_YEAR = "dayofyear";
    public static final String COLUMN_WEEK = "week";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_YRAR = "year";
    public static final String COLUMN_STEPS = "steps";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_DISTENCE = "distence";
    public static final String COLUMN_STEPS_FOR_WEEKVIEW = "forweekviewsteps";

    private static final String DATABASE_NAME = "PEDO.db";
    private static final int DATABASE_VERSION = 1;

    // SQL create statement — clean and formatted for readability
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_DITE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MINUTS + " INTEGER NOT NULL, " +
                    COLUMN_HOURS + " INTEGER NOT NULL, " +
                    COLUMN_DAY_OF_WEEK + " INTEGER NOT NULL, " +
                    COLUMN_DAY_OF_MONTH + " INTEGER NOT NULL, " +
                    COLUMN_WEEK + " INTEGER NOT NULL, " +
                    COLUMN_MONTH + " INTEGER NOT NULL, " +
                    COLUMN_YRAR + " INTEGER NOT NULL, " +
                    COLUMN_STEPS + " INTEGER NOT NULL, " +
                    COLUMN_CALORIES + " REAL, " +
                    COLUMN_DISTENCE + " REAL NOT NULL, " +
                    COLUMN_STEPS_FOR_WEEKVIEW + " INTEGER NOT NULL, " +
                    COLUMN_DAY_OF_YEAR + " INTEGER NOT NULL" +
                    ");";

    public MySQLiteHelper(Context context) {
        // always use DATABASE_VERSION constant for clarity
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.i("DB_CREATE", "Database created successfully with table: " + TABLE_DITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getSimpleName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion +
                        ", which will destroy all old data");

        // Optional: drop and recreate if schema changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DITE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Disables WAL to avoid concurrency conflicts on some devices
        db.disableWriteAheadLogging();
    }
}
