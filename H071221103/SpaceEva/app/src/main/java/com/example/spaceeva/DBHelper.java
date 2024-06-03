package com.example.spaceeva;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "User.db";

    public static class UserEntry {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_LOGGED_IN = "logged_in";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry.COLUMN_NAME_USERNAME + " TEXT PRIMARY KEY," +
                    UserEntry.COLUMN_NAME_PASSWORD + " TEXT," +
                    UserEntry.COLUMN_NAME_LOGGED_IN + " INTEGER DEFAULT 0)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public long registerUser(String user, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_USERNAME, user);
        values.put(UserEntry.COLUMN_NAME_PASSWORD, password);
        return db.insert(UserEntry.TABLE_NAME, null, values);
    }

    public boolean isValidLogin(String user, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_USERNAME + " = ? AND " + UserEntry.COLUMN_NAME_PASSWORD + " = ?", new String[]{user, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public void saveLoginStatus(String user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_LOGGED_IN, 1);
        db.update(UserEntry.TABLE_NAME, values, UserEntry.COLUMN_NAME_USERNAME + " = ?", new String[]{user});
    }

    public boolean checkLoginStatus() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_LOGGED_IN + " = 1", null);
        boolean isLoggedIn = cursor.getCount() > 0;
        cursor.close();
        return isLoggedIn;
    }

    public String getLoggedInUser() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + UserEntry.COLUMN_NAME_USERNAME + " FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_LOGGED_IN + " = 1", null);
        if (cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USERNAME));
            cursor.close();
            return username;
        } else {
            cursor.close();
            return null;
        }
    }

    public void logout() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_LOGGED_IN, 0);
        db.update(UserEntry.TABLE_NAME, values, null, null);
    }
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                UserEntry.TABLE_NAME,
                null,
                UserEntry.COLUMN_NAME_USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        );
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}

