package io.reed.dripr.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Currency;


/**
 * Created by reed on 12/3/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "coffee.db";
    private static final int DATABASE_VERSION = 1;

    //Creation SQL statement

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(DatabaseHelper.class.getName(), CoffeeDatabaseContract.CoffeeEntry.DATABASE_CREATE);
        db.execSQL(CoffeeDatabaseContract.CoffeeEntry.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DatabaseHelper.class.getName(), "Upgrading db from version " + oldVersion + " to " + newVersion + " which will destroy all data");
        db.execSQL(CoffeeDatabaseContract.CoffeeEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public Cursor getEntries(String profile) {
        return getReadableDatabase().rawQuery(CoffeeDatabaseContract.CoffeeEntry.SQL_SELECT_ENTRIES, null);
    }
}
