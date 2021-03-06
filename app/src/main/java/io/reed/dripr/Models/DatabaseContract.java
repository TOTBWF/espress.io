package io.reed.dripr.Models;

import android.provider.BaseColumns;

/**
 * Created by Reed Mullanix on 12/3/15.
 * Provides details for the database schema to the rest of the app
 * Also includes some useful SQL commands
 */
public final class DatabaseContract {
    public static final String COLUMN_NAME_NULLABLE = "NULL";
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {}

    public static abstract class ProfileEntry implements BaseColumns {
        public static final String TABLE_PROFILE = "PROFILE";
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_TDS = "TDS";
        public static final String COLUMN_YIELD = "YIELD";
        public static final String COLUMN_TDS_TOLERANCES = "TDS_TOLERANCES";
        public static final String COLUMN_YIELD_TOLERANCES = "YIELD_TOLERANCES";
        public static final String COLUMN_BEAN_ABSORPTION = "BEAN_ABSORPTION";
        public static final String DATABASE_CREATE = "CREATE TABLE " +
                TABLE_PROFILE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT UNIQUE, "
                + COLUMN_TDS + " DECIMAL, "
                + COLUMN_YIELD +  " DECIMAL, "
                + COLUMN_TDS_TOLERANCES + " DECIMAL, "
                + COLUMN_YIELD_TOLERANCES + " DECIMAL, "
                + COLUMN_BEAN_ABSORPTION + " DECIMAL);";
        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_PROFILE;
        public static final String SQL_SELECT_ENTRIES = "SELECT * FROM " + TABLE_PROFILE;
    }

    /* Inner class that defines the table contents */
    public static abstract class CoffeeEntry implements BaseColumns {
        public static final String TABLE_COFFEE = "COFFEE";
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_INPUT = "INPUT";
        public static final String COLUMN_OUTPUT = "OUTPUT";
        public static final String COLUMN_TDS = "TDS";
        public static final String COLUMN_YIELD = "YIELD";
        public static final String COLUMN_BEANS = "BEANS";
        public static final String COLUMN_DATE = "DATE";
        public static final String DATABASE_CREATE = "CREATE TABLE " +
                CoffeeEntry.TABLE_COFFEE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_INPUT + " DECIMAL, "
                + COLUMN_OUTPUT + " DECIMAL, "
                + COLUMN_TDS +" DECIMAL, "
                + COLUMN_YIELD + " DECIMAL, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_BEANS + " TEXT);";
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_COFFEE;
        public static final String SQL_SELECT_ENTRIES = "SELECT * FROM " + TABLE_COFFEE;
    }
}
