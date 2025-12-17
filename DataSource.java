package com.demo.example.DB;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    public DataSource(Context context) {
        this.dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        this.database = this.dbHelper.getWritableDatabase();
    }

    public void close() {
        this.dbHelper.close();
    }

    public void upDateRecord(SingleRow singleRow, SharedPreferences sharedPreferences) {
        try {
            int steps = singleRow.getSteps() - sharedPreferences.getInt("stepsOfLastHr", 0);
            if (steps < 0) steps = 0;

            Log.i("Steps Of Last hr", sharedPreferences.getInt("stepsOfLastHr", 0) + "");

            ContentValues contentValues = new ContentValues();
            Calendar calendar = Calendar.getInstance();

            contentValues.put(MySQLiteHelper.COLUMN_MINUTS, singleRow.getMints());
            contentValues.put(MySQLiteHelper.COLUMN_HOURS, singleRow.getHours());
            contentValues.put(MySQLiteHelper.COLUMN_DAY_OF_WEEK, singleRow.getDaysOfWeek());
            contentValues.put(MySQLiteHelper.COLUMN_DAY_OF_MONTH, singleRow.getDaysOfMonth());
            contentValues.put(MySQLiteHelper.COLUMN_DAY_OF_YEAR, singleRow.getDaysOfYear());
            contentValues.put(MySQLiteHelper.COLUMN_WEEK, singleRow.getWeek());
            contentValues.put(MySQLiteHelper.COLUMN_MONTH, singleRow.getMonth());
            contentValues.put(MySQLiteHelper.COLUMN_YRAR, singleRow.getYear());
            contentValues.put(MySQLiteHelper.COLUMN_STEPS, steps);
            contentValues.put(MySQLiteHelper.COLUMN_CALORIES, singleRow.getCaloriesBurn());
            contentValues.put(MySQLiteHelper.COLUMN_DISTENCE, singleRow.getDistence());
            contentValues.put(MySQLiteHelper.COLUMN_STEPS_FOR_WEEKVIEW, singleRow.getStepsForWeekView());

            try {
                int updated = database.update(
                        MySQLiteHelper.TABLE_DITE,
                        contentValues,
                        "hours = ? and " + MySQLiteHelper.COLUMN_DAY_OF_WEEK + " = ? and "
                                + MySQLiteHelper.COLUMN_DAY_OF_MONTH + " = ? and "
                                + MySQLiteHelper.COLUMN_DAY_OF_YEAR + " = ? and "
                                + MySQLiteHelper.COLUMN_WEEK + " = ? and "
                                + MySQLiteHelper.COLUMN_MONTH + " = ? and "
                                + MySQLiteHelper.COLUMN_YRAR + " = ?",
                        new String[]{
                                String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)),
                                String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)),
                                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)),
                                String.valueOf(calendar.get(Calendar.DAY_OF_YEAR)),
                                String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)),
                                String.valueOf(calendar.get(Calendar.MONTH)),
                                String.valueOf(calendar.get(Calendar.YEAR))
                        }
                );

                if (updated != 0) {
                    sharedPreferences.edit().putInt("stepsOfLastHrDiff", singleRow.getSteps()).apply();
                } else {
                    sharedPreferences.edit().putInt("stepsOfLastHr", singleRow.getSteps() - 9).apply();
                    singleRow.setSteps(9);
                    createRecord(singleRow);
                }

            } catch (Exception e) {
                Log.e("DB_UPDATE_ERROR", e.getMessage());
            }

        } catch (Exception e) {
            Log.e("UPDATE_RECORD_ERROR", e.getMessage());
        }
    }

    public void createRecord(SingleRow singleRow) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLiteHelper.COLUMN_MINUTS, singleRow.getMints());
        contentValues.put(MySQLiteHelper.COLUMN_HOURS, singleRow.getHours());
        contentValues.put(MySQLiteHelper.COLUMN_DAY_OF_WEEK, singleRow.getDaysOfWeek());
        contentValues.put(MySQLiteHelper.COLUMN_DAY_OF_MONTH, singleRow.getDaysOfMonth());
        contentValues.put(MySQLiteHelper.COLUMN_DAY_OF_YEAR, singleRow.getDaysOfYear());
        contentValues.put(MySQLiteHelper.COLUMN_WEEK, singleRow.getWeek());
        contentValues.put(MySQLiteHelper.COLUMN_MONTH, singleRow.getMonth());
        contentValues.put(MySQLiteHelper.COLUMN_YRAR, singleRow.getYear());
        contentValues.put(MySQLiteHelper.COLUMN_STEPS, singleRow.getSteps());
        contentValues.put(MySQLiteHelper.COLUMN_CALORIES, singleRow.getCaloriesBurn());
        contentValues.put(MySQLiteHelper.COLUMN_DISTENCE, singleRow.getDistence());
        contentValues.put(MySQLiteHelper.COLUMN_STEPS_FOR_WEEKVIEW, singleRow.getStepsForWeekView());

        database.insert(MySQLiteHelper.TABLE_DITE, null, contentValues);
    }

    public void deleteRecord(SingleRow singleRow) {
        long id = singleRow.getId();
        database.delete(MySQLiteHelper.TABLE_DITE, "_id = ?", new String[]{String.valueOf(id)});
    }

    public List<SingleRow> getAllRecordsForDaysList() {
        List<SingleRow> records = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT _id,minutes,hours,dayofweek,dayofmonth,dayofyear,week,month,year,steps,calories,distence,forweekviewsteps " +
                        "FROM Dite GROUP BY dayofweek HAVING MAX(steps);", null);
        while (cursor.moveToNext()) {
            records.add(cursorToRecord(cursor));
        }
        cursor.close();
        return records;
    }

    public List<SingleRow> getAllRecordsForDay(int dayOfWeek, int month, int year) {
        List<SingleRow> records = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT _id,minutes,hours,dayofweek,dayofmonth,dayofyear,week,month,year,steps,calories,distence,forweekviewsteps " +
                        "FROM Dite WHERE dayofweek=" + dayOfWeek + " AND month=" + month + " AND year=" + year +
                        " GROUP BY hours HAVING MAX(steps);", null);
        while (cursor.moveToNext()) {
            records.add(cursorToRecord(cursor));
        }
        cursor.close();
        return records;
    }

    public List<SingleRow> getAllRecordsForWeek(int week, int weekNum, int month, int year) {
        List<SingleRow> records = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT _id,minutes,hours,dayofweek,dayofmonth,dayofyear,week,month,year,steps,calories,distence,forweekviewsteps " +
                        "FROM Dite WHERE week=" + weekNum + " AND year=" + year +
                        " GROUP BY dayofweek HAVING MAX(forweekviewsteps);", null);
        while (cursor.moveToNext()) {
            records.add(cursorToRecord(cursor));
        }
        cursor.close();
        return records;
    }

    public List<SingleRow> getAllRecordsForMonth(int day, int month, int year) {
        List<SingleRow> records = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT _id,minutes,hours,dayofweek,dayofmonth,dayofyear,week,month,year,steps,calories,distence,forweekviewsteps " +
                        "FROM Dite WHERE month=" + month + " AND year=" + year +
                        " GROUP BY dayofmonth HAVING MAX(forweekviewsteps);", null);
        while (cursor.moveToNext()) {
            records.add(cursorToRecord(cursor));
        }
        cursor.close();
        return records;
    }

    public List<SingleRow> getAllRecordsForYear(int week, int month, int year) {
        List<SingleRow> records = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT _id,minutes,hours,dayofweek,dayofmonth,dayofyear,week,month,year,steps,calories,distence,forweekviewsteps " +
                        "FROM Dite WHERE year=" + year + " GROUP BY month HAVING MAX(steps);", null);
        while (cursor.moveToNext()) {
            records.add(cursorToRecord(cursor));
        }
        cursor.close();
        return records;
    }

    private SingleRow cursorToRecord(Cursor cursor) {
        SingleRow singleRow = new SingleRow(0L, 0, 0, 0.0f, 0.0f, 0, 0, 0, 0, 0, 0, 0, 0);
        singleRow.setId(cursor.getLong(0));
        singleRow.setMints(cursor.getInt(1));
        singleRow.setHours(cursor.getInt(2));
        singleRow.setDaysOfWeek(cursor.getInt(3));
        singleRow.setDaysOfMonth(cursor.getInt(4));
        singleRow.setDaysOfYear(cursor.getInt(5));
        singleRow.setWeek(cursor.getInt(6));
        singleRow.setMonth(cursor.getInt(7));
        singleRow.setYear(cursor.getInt(8));
        singleRow.setSteps(cursor.getInt(9));
        singleRow.setCaloriesBurn(cursor.getFloat(10));
        singleRow.setDistence(cursor.getFloat(11));
        singleRow.setStepsForWeekView(cursor.getInt(12));
        return singleRow;
    }
}
