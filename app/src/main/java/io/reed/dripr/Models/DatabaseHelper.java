package io.reed.dripr.Models;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by reed on 12/3/15.
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
        db.insert(CoffeeDatabaseContract.ProfileEntry.TABLE_PROFILE, CoffeeDatabaseContract.COLUMN_NAME_NULLABLE, values);
        // Do the same for espresso
        values = new ContentValues();
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_NAME, YieldTdsTarget.DEFAULT_ESPRESSO_NAME);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_TDS, YieldTdsTarget.DEFAULT_ESPRESSO_TDS_TARGET);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_YIELD, YieldTdsTarget.DEFAULT_ESPRESSO_YIELD_TARGET);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_TDS_TOLERANCES, YieldTdsTarget.DEFAULT_ESPRESSO_TDS_TOLERANCES);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_YIELD_TOLERANCES, YieldTdsTarget.DEFAULT_ESPRESSO_YIELD_TOLERANCES);
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_BEAN_ABSORPTION, YieldTdsTarget.DEFAULT_ESPRESSO_BEAN_ABSORPTION);
        db.insert(CoffeeDatabaseContract.ProfileEntry.TABLE_PROFILE, CoffeeDatabaseContract.COLUMN_NAME_NULLABLE, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DatabaseHelper.class.getName(), "Upgrading db from version " + oldVersion + " to " + newVersion + " which will destroy all data");
        db.execSQL(CoffeeDatabaseContract.CoffeeEntry.SQL_DELETE_ENTRIES);
        db.execSQL(CoffeeDatabaseContract.ProfileEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void clearDatabase() {
        SQLiteDatabase db = getWritableDatabase();
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
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_NAME, target.getName());
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_TDS, target.getTdsTarget());
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_YIELD, target.getYieldTarget());
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_TDS_TOLERANCES, target.getTdsTolerances());
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_YIELD_TOLERANCES, target.getYieldTolerances());
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_BEAN_ABSORPTION, target.getBeanAbsorptionFactor());
        db.insert(CoffeeDatabaseContract.ProfileEntry.TABLE_PROFILE, CoffeeDatabaseContract.COLUMN_NAME_NULLABLE, values);
        db.close();
    }

    public void updateTargetInDatabase(YieldTdsTarget target) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_TDS, target.getTdsTarget());
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_YIELD, target.getYieldTarget());
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_TDS_TOLERANCES, target.getTdsTolerances());
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_YIELD_TOLERANCES, target.getYieldTolerances());
        values.put(CoffeeDatabaseContract.ProfileEntry.COLUMN_BEAN_ABSORPTION, target.getBeanAbsorptionFactor());
        db.update(CoffeeDatabaseContract.ProfileEntry.TABLE_PROFILE, values, CoffeeDatabaseContract.ProfileEntry.COLUMN_NAME + "='" + target.getName() + "'", null);
        db.close();
    }

    public void deleteTargetFromDatabase(YieldTdsTarget target) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CoffeeDatabaseContract.ProfileEntry.TABLE_PROFILE, CoffeeDatabaseContract.ProfileEntry.COLUMN_NAME + "='" + target.getName()+ "'", null);
        db.delete(CoffeeDatabaseContract.CoffeeEntry.TABLE_COFFEE, CoffeeDatabaseContract.CoffeeEntry.COLUMN_BEANS + "='" + target.getName()+ "'", null);
        db.close();
    }
}
