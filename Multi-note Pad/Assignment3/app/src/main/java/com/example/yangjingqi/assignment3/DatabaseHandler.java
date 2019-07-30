package com.example.yangjingqi.assignment3;

/**
 * Created by yangjingqi on 7/21/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "multiNotesAppDB";
    // DB Table Name
    private static final String TABLE_NAME = "multiNotesTable";
    ///DB Columns
    private static final String MULTINOTES_TITLE = "multiNotesName";
    private static final String MULTINOTES_CONTENT = "multiNotesContent";
    private static final String MULTINOTES_TIME = "multiNotesTime";

    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    MULTINOTES_TITLE + " TEXT not null," +
                    MULTINOTES_CONTENT + " TEXT not null, " +
                    MULTINOTES_TIME + " TEXT not null)";

    private SQLiteDatabase database;

    // Singleton instance
    private static DatabaseHandler instance;

    public static DatabaseHandler getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHandler(context);
        return instance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist
        Log.d(TAG, "onCreate: Mking New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    public void setupDb() {
        database = getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<NotesEdit> loadNotes() {

        ArrayList<NotesEdit> newNotes = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{MULTINOTES_TITLE, MULTINOTES_TIME, MULTINOTES_CONTENT}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order
        if (cursor != null) {
            cursor.moveToLast();

            for (int i = 0; i < cursor.getCount(); i++) {
                String title = cursor.getString(0);
                String time = cursor.getString(1);
                String content = cursor.getString(2);
                newNotes.add(new NotesEdit(title, time, content));
                cursor.moveToPrevious();
            }
            cursor.close();
        }
        return newNotes;
    }

    public void addNotes(NotesEdit notes) {
        ContentValues values = new ContentValues();
        values.put(MULTINOTES_TITLE, notes.getNotes_title());
        values.put(MULTINOTES_TIME, notes.getTime());
        values.put(MULTINOTES_CONTENT, notes.getContent());
        deleteNotes(notes.getNotes_title());
        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addNotes: " + key);
    }

    public void updateNotes(NotesEdit notes) {
        ContentValues values = new ContentValues();
        values.put(MULTINOTES_TITLE, notes.getNotes_title());
        values.put(MULTINOTES_TIME, notes.getTime());
        values.put(MULTINOTES_CONTENT, notes.getContent());

        long key = database.update(
                TABLE_NAME, values, MULTINOTES_TITLE + " = ?", new String[]{notes.getNotes_title()});

        Log.d(TAG, "updateNotes: " + key);
    }

    public void deleteNotes(String name) {
        Log.d(TAG, "deleteNotes: " + name);
        int cnt = database.delete(TABLE_NAME, MULTINOTES_TITLE + " = ?", new String[]{name});
        Log.d(TAG, "deleteNotes: " + cnt);
    }

    public void dumpLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String title = cursor.getString(0);
                String time = cursor.getString(1);
                String content = cursor.getString(2);
                Log.d(TAG, "dumpLog: " +
                        String.format("%s %-18s", MULTINOTES_TITLE + ":", title) +
                        String.format("%s %-18s", MULTINOTES_TIME + ":", time) +
                        String.format("%s %-18s", MULTINOTES_CONTENT + ":", content));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }

    public void shutDown() {
        database.close();
    }

    public String getDbPath(Context context,String DbName)
    {
        return context.getDatabasePath(DbName).getAbsolutePath();
    }

}

