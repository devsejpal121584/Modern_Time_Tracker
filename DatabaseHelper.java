package com.example.timetrackpro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "timetrackpro.db";
    public static final int DB_VERSION = 3;

    // =========================
    // ✅ USERS TABLE
    // =========================
    public static final String TBL_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_FULL_NAME = "full_name";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // =========================
    // ✅ ACTIVITIES TABLE
    // =========================
    public static final String TBL_ACTIVITIES = "activities";
    public static final String COL_ACT_ID = "id";
    public static final String COL_ACT_NAME = "name";

    // =========================
    // ✅ ACTIVITY LOGS TABLE
    // =========================
    public static final String TBL_LOGS = "activity_logs";
    public static final String COL_LOG_ID = "id";
    public static final String COL_LOG_ACTIVITY_ID = "activity_id";
    public static final String COL_LOG_START = "start_time";
    public static final String COL_LOG_END = "end_time";
    public static final String COL_LOG_DURATION = "duration_ms";

    // =========================
    // ✅ SLEEP LOGS TABLE
    // =========================
    public static final String TBL_SLEEP = "sleep_logs";
    public static final String COL_SLEEP_ID = "id";
    public static final String COL_SLEEP_START = "sleep_start";
    public static final String COL_SLEEP_END = "sleep_end";
    public static final String COL_SLEEP_DURATION = "duration_ms";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ✅ Users Table
        db.execSQL("CREATE TABLE " + TBL_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FULL_NAME + " TEXT NOT NULL, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD + " TEXT NOT NULL)");

        // ✅ Activities Table
        db.execSQL("CREATE TABLE " + TBL_ACTIVITIES + " (" +
                COL_ACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ACT_NAME + " TEXT NOT NULL UNIQUE)");

        // ✅ Activity Logs Table
        db.execSQL("CREATE TABLE " + TBL_LOGS + " (" +
                COL_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LOG_ACTIVITY_ID + " INTEGER NOT NULL, " +
                COL_LOG_START + " INTEGER NOT NULL, " +
                COL_LOG_END + " INTEGER, " +
                COL_LOG_DURATION + " INTEGER DEFAULT 0)");

        // ✅ Sleep Logs Table
        db.execSQL("CREATE TABLE " + TBL_SLEEP + " (" +
                COL_SLEEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SLEEP_START + " INTEGER NOT NULL, " +
                COL_SLEEP_END + " INTEGER, " +
                COL_SLEEP_DURATION + " INTEGER DEFAULT 0)");

        // ✅ Insert Default Activities
        db.execSQL("INSERT INTO " + TBL_ACTIVITIES + "(" + COL_ACT_NAME + ") VALUES " +
                "('Study'),('Workout'),('Eating'),('Gaming'),('Meditation'),('Work'),('Reading')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // ✅ Delete old tables (Beginner-friendly)
        db.execSQL("DROP TABLE IF EXISTS " + TBL_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_ACTIVITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_SLEEP);

        onCreate(db);
    }

    // =====================================================
    // ✅ USER REGISTER / LOGIN FUNCTIONS
    // =====================================================

    // ✅ Check if username already exists
    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TBL_USERS +
                        " WHERE " + COL_USERNAME + "=?",
                new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // ✅ Register New User
    public boolean registerUser(String fullName, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_FULL_NAME, fullName);
        cv.put(COL_USERNAME, username);
        cv.put(COL_PASSWORD, password);

        long res = db.insert(TBL_USERS, null, cv);
        return res != -1;
    }

    // ✅ Login User
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TBL_USERS +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    // =====================================================
    // ✅ ACTIVITIES FUNCTIONS
    // =====================================================

    // ✅ Add new custom activity
    public boolean addActivity(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ACT_NAME, name);

        long res = db.insert(TBL_ACTIVITIES, null, cv);
        return res != -1;
    }

    // ✅ Get all activities list
    public Cursor getAllActivities() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TBL_ACTIVITIES +
                " ORDER BY " + COL_ACT_ID + " DESC", null);
    }

    // ✅ Start activity tracking log
    public long startActivityLog(int activityId, long startTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_LOG_ACTIVITY_ID, activityId);
        cv.put(COL_LOG_START, startTime);

        return db.insert(TBL_LOGS, null, cv);
    }

    // ✅ Stop activity tracking log
    public boolean stopActivityLog(long logId, long endTime, long durationMs) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_LOG_END, endTime);
        cv.put(COL_LOG_DURATION, durationMs);

        int updated = db.update(TBL_LOGS, cv,
                COL_LOG_ID + "=?",
                new String[]{String.valueOf(logId)});

        return updated > 0;
    }

    // ✅ Get all activity logs (History)
    public Cursor getAllLogs() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT l." + COL_LOG_ID + ", a." + COL_ACT_NAME + ", l." + COL_LOG_START + ", l." + COL_LOG_END + ", l." + COL_LOG_DURATION +
                        " FROM " + TBL_LOGS + " l INNER JOIN " + TBL_ACTIVITIES + " a ON l." + COL_LOG_ACTIVITY_ID + "=a." + COL_ACT_ID +
                        " ORDER BY l." + COL_LOG_ID + " DESC",
                null
        );
    }

    // =====================================================
    // ✅ SLEEP TRACKER FUNCTIONS
    // =====================================================

    // ✅ Start Sleep
    public long startSleep(long startTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SLEEP_START, startTime);

        return db.insert(TBL_SLEEP, null, cv);
    }

    // ✅ Stop Sleep (Wake Up)
    public boolean stopSleep(long sleepId, long endTime, long durationMs) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_SLEEP_END, endTime);
        cv.put(COL_SLEEP_DURATION, durationMs);

        int updated = db.update(TBL_SLEEP, cv,
                COL_SLEEP_ID + "=?",
                new String[]{String.valueOf(sleepId)});

        return updated > 0;
    }

    // ✅ Get Sleep History
    public Cursor getAllSleepLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TBL_SLEEP +
                " ORDER BY " + COL_SLEEP_ID + " DESC", null);
    }
}
