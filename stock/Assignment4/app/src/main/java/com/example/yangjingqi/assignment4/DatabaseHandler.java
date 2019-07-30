package com.example.yangjingqi.assignment4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by yangjingqi on 7/24/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "StockAppDB";
    // DB Table Name
    private static final String TABLE_NAME = "StockWatchTable";
    ///DB Columns
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null)";

    private SQLiteDatabase database;

    // Singleton instance
    private static DatabaseHandler instance;

    public static DatabaseHandler getInstance(MainActivity context) {
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

    public ArrayList<Stock> loadStocks() {

        ArrayList<Stock> stocks = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{SYMBOL, COMPANY }, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order
        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                stocks.add(new Stock(symbol, company, null, null, null));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    public String getOldData_sym() {
        String symbol = "";
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                symbol = cursor.getString(0);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return symbol;
    }

    public String getOldData_cmp() {
        String company = "";
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                company = cursor.getString(1);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return company;
    }

    public void addStock(Stock stock) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY, stock.getCompany());
        Log.d(TAG, "addStock:-------------------------->>>>>>>>>>>>>>>>>>>> "+ values);
        deleteStock(stock.getCompany());
        deleteStock(stock.getSymbol());
        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: " + key);
    }

    public void updateStock(Stock stock) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY, stock.getCompany());

        long key = database.update(
                TABLE_NAME, values, SYMBOL + " = ?", new String[]{stock.getCompany()});
        Log.d(TAG, "updateStock: "+key);
    }

    public void deleteStock(String name) {
        Log.d(TAG, "deleteStock: " + name);
        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{name});
        Log.d(TAG, "deleteStock: " + cnt);
    }

    public void dumpLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                Log.d(TAG, "dumpLog: " +
                        String.format("%s %-18s", SYMBOL + ":", symbol) +
                        String.format("%s %-18s", COMPANY + ":", company));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    public void shutDown() {
        database.close();
    }
}