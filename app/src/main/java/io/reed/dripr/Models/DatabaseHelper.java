package io.reed.dripr.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created by Reed Mullanix on 12/3/15.
 * Helper class for database interactions
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    private static final String DATABASE_NAME = "coffee.db";
    private static final int DATABASE_VERSION = 1;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if(instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(DatabaseHelper.class.getName(), DatabaseContract.CoffeeEntry.DATABASE_CREATE);
        db.execSQL(DatabaseContract.CoffeeEntry.DATABASE_CREATE);
        Log.d(DatabaseHelper.class.getName(), DatabaseContract.ProfileEntry.DATABASE_CREATE);
        db.execSQL(DatabaseContract.ProfileEntry.DATABASE_CREATE);
        // Store the default values for profiles
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ProfileEntry.COLUMN_NAME, YieldTdsTarget.DEFAULT_DRIP_NAME);
        values.put(DatabaseContract.ProfileEntry.COLUMN_TDS, YieldTdsTarget.DEFAULT_DRIP_TDS_TARGET);
        values.put(DatabaseContract.ProfileEntry.COLUMN_YIELD, YieldTdsTarget.DEFAULT_DRIP_YIELD_TARGET);
        values.put(DatabaseContract.ProfileEntry.COLUMN_TDS_TOLERANCES, YieldTdsTarget.DEFAULT_DRIP_TDS_TOLERANCES);
        values.put(DatabaseContract.ProfileEntry.COLUMN_YIELD_TOLERANCES, YieldTdsTarget.DEFAULT_DRIP_YIELD_TOLERANCES);
        values.put(DatabaseContract.ProfileEntry.COLUMN_BEAN_ABSORPTION, YieldTdsTarget.DEFAULT_DRIP_BEAN_ABSORPTION);
        db.insert(DatabaseContract.ProfileEntry.TABLE_PROFILE, DatabaseContract.COLUMN_NAME_NULLABLE, values);
        // Do the same for espresso
        values = new ContentValues();
        values.put(DatabaseContract.ProfileEntry.COLUMN_NAME, YieldTdsTarget.DEFAULT_ESPRESSO_NAME);
        values.put(DatabaseContract.ProfileEntry.COLUMN_TDS, YieldTdsTarget.DEFAULT_ESPRESSO_TDS_TARGET);
        values.put(DatabaseContract.ProfileEntry.COLUMN_YIELD, YieldTdsTarget.DEFAULT_ESPRESSO_YIELD_TARGET);
        values.put(DatabaseContract.ProfileEntry.COLUMN_TDS_TOLERANCES, YieldTdsTarget.DEFAULT_ESPRESSO_TDS_TOLERANCES);
        values.put(DatabaseContract.ProfileEntry.COLUMN_YIELD_TOLERANCES, YieldTdsTarget.DEFAULT_ESPRESSO_YIELD_TOLERANCES);
        values.put(DatabaseContract.ProfileEntry.COLUMN_BEAN_ABSORPTION, YieldTdsTarget.DEFAULT_ESPRESSO_BEAN_ABSORPTION);
        db.insert(DatabaseContract.ProfileEntry.TABLE_PROFILE, DatabaseContract.COLUMN_NAME_NULLABLE, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DatabaseHelper.class.getName(), "Upgrading db from version " + oldVersion + " to " + newVersion + " which will destroy all data");
        db.execSQL(DatabaseContract.CoffeeEntry.SQL_DELETE_ENTRIES);
        db.execSQL(DatabaseContract.ProfileEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void clearDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DatabaseContract.CoffeeEntry.SQL_DELETE_ENTRIES);
        db.execSQL(DatabaseContract.ProfileEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public Cursor getEntries(String profile) {
        return getReadableDatabase().rawQuery(DatabaseContract.CoffeeEntry.SQL_SELECT_ENTRIES + " WHERE " + DatabaseContract.CoffeeEntry.COLUMN_BEANS + "='"+profile+"'", null);
    }

    public Cursor getProfiles() {
        return getReadableDatabase().rawQuery(DatabaseContract.ProfileEntry.SQL_SELECT_ENTRIES, null);
    }

    public ArrayList<YieldTdsTarget> getStoredTargets() {
        ArrayList<YieldTdsTarget> storedTargets = new ArrayList<>();
        Cursor c = getProfiles();
        c.moveToFirst();
        while(!c.isAfterLast()) {
            String name = c.getString(1);
            double tds = c.getDouble(2);
            double yield = c.getDouble(3);
            double tdsTolerance = c.getDouble(4);
            double yieldTolerance = c.getDouble(5);
            double beanAbsorption = c.getDouble(6);
            storedTargets.add(new YieldTdsTarget(name, yield, yieldTolerance, tds, tdsTolerance, beanAbsorption));
            c.moveToNext();
        }
        c.close();
        return storedTargets;
    }

    public void writeTargetToDatabase(YieldTdsTarget target) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ProfileEntry.COLUMN_NAME, target.getName());
        values.put(DatabaseContract.ProfileEntry.COLUMN_TDS, target.getTdsTarget());
        values.put(DatabaseContract.ProfileEntry.COLUMN_YIELD, target.getYieldTarget());
        values.put(DatabaseContract.ProfileEntry.COLUMN_TDS_TOLERANCES, target.getTdsTolerances());
        values.put(DatabaseContract.ProfileEntry.COLUMN_YIELD_TOLERANCES, target.getYieldTolerances());
        values.put(DatabaseContract.ProfileEntry.COLUMN_BEAN_ABSORPTION, target.getBeanAbsorptionFactor());
        db.insert(DatabaseContract.ProfileEntry.TABLE_PROFILE, DatabaseContract.COLUMN_NAME_NULLABLE, values);
        db.close();
    }

    public void updateTargetInDatabase(YieldTdsTarget target) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ProfileEntry.COLUMN_TDS, target.getTdsTarget());
        values.put(DatabaseContract.ProfileEntry.COLUMN_YIELD, target.getYieldTarget());
        values.put(DatabaseContract.ProfileEntry.COLUMN_TDS_TOLERANCES, target.getTdsTolerances());
        values.put(DatabaseContract.ProfileEntry.COLUMN_YIELD_TOLERANCES, target.getYieldTolerances());
        values.put(DatabaseContract.ProfileEntry.COLUMN_BEAN_ABSORPTION, target.getBeanAbsorptionFactor());
        db.update(DatabaseContract.ProfileEntry.TABLE_PROFILE, values, DatabaseContract.ProfileEntry.COLUMN_NAME + "='" + target.getName() + "'", null);
        db.close();
    }

    public void deleteTargetFromDatabase(YieldTdsTarget target) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DatabaseContract.ProfileEntry.TABLE_PROFILE, DatabaseContract.ProfileEntry.COLUMN_NAME + "='" + target.getName()+ "'", null);
        db.delete(DatabaseContract.CoffeeEntry.TABLE_COFFEE, DatabaseContract.CoffeeEntry.COLUMN_BEANS + "='" + target.getName()+ "'", null);
        db.close();
    }
}
