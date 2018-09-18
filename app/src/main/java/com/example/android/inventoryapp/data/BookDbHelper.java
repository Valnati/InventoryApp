package com.example.android.inventoryapp.data;

/**
 * Created by ~ Adam ~ on 2/25/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventoryapp.data.BooksContract.BookEntry;

/**
 * Created by ~ Adam ~ on 2/24/2018.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "Inventory.db";

    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, " +
                BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, " +
                BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                BookEntry.COLUMN_BOOK_SUPPLIER + " TEXT, " +
                BookEntry.COLUMN_BOOK_PHONE + " TEXT);";

        Log.v(LOG_TAG,SQL_CREATE_BOOK_TABLE);

        db.execSQL(SQL_CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
