package io.reed.dripr.Utils;

import android.content.ContentValues;
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
        Log.d(DatabaseHelper.class.getName(), CoffeeDatabaseContract.ProfileEntry.DATABASE_CREATE);
        db.execSQL(CoffeeDatabaseContract.ProfileEntry.DATABASE_CREATE);
        // Store the default values for profiles
        ContentValues values = new ContentValues();
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_NAME, YieldTdsTarget.DEFAULT_DRIP_NAME);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_TDS, YieldTdsTarget.DEFAULT_DRIP_TDS_TARGET);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_YIELD, YieldTdsTarget.DEFAULT_DRIP_YIELD_TARGET);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_TDS_TOLERANCES, YieldTdsTarget.DEFAULT_DRIP_TDS_TOLERANCES);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_YIELD_TOLERANCES, YieldTdsTarget.DEFAULT_DRIP_YIELD_TOLERANCES);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_BEAN_ABSORPTION, YieldTdsTarget.DEFAULT_DRIP_BEAN_ABSORPTION);
        db.insert(CoffeeDatabaseContract.ProfileEntry.TABLE_PROFILE, CoffeeDatabaseContract.CoffeeEntry.COLUMN_NAME_NULLABLE, values);
        // Do the same for espresso
        values = new ContentValues();
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_NAME, YieldTdsTarget.DEFAULT_ESPRESSO_NAME);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_TDS, YieldTdsTarget.DEFAULT_ESPRESSO_TDS_TARGET);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_YIELD, YieldTdsTarget.DEFAULT_ESPRESSO_YIELD_TARGET);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_TDS_TOLERANCES, YieldTdsTarget.DEFAULT_ESPRESSO_TDS_TOLERANCES);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_YIELD_TOLERANCES, YieldTdsTarget.DEFAULT_ESPRESSO_YIELD_TOLERANCES);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_BEAN_ABSORPTION, YieldTdsTarget.DEFAULT_ESPRESSO_BEAN_ABSORPTION);
        db.insert(CoffeeDatabaseContract.ProfileEntry.TABLE_PROFILE, CoffeeDatabaseContract.CoffeeEntry.COLUMN_NAME_NULLABLE, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DatabaseHelper.class.getName(), "Upgrading db from version " + oldVersion + " to " + newVersion + " which will destroy all data");
        db.execSQL(CoffeeDatabaseContract.CoffeeEntry.SQL_DELETE_ENTRIES);
        db.execSQL(CoffeeDatabaseContract.ProfileEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public Cursor getEntries(String profile) {
        return getReadableDatabase().rawQuery(CoffeeDatabaseContract.CoffeeEntry.SQL_SELECT_ENTRIES + " WHERE " + CoffeeDatabaseContract.CoffeeEntry.COLUMN_BEANS + "='"+profile+"'", null);
    }

    public Cursor getProfiles() {
        return getReadableDatabase().rawQuery(CoffeeDatabaseContract.ProfileEntry.SQL_SELECT_ENTRIES, null);
    }
}
