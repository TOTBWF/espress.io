package io.reed.dripr.Utils;

import android.provider.BaseColumns;

/**
 * Created by reed on 12/3/15.
 */
public final class CoffeeDatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public CoffeeDatabaseContract () {}

    /* Inner class that defines the table contents */
    public static abstract class CoffeeEntry implements BaseColumns {
        public static final String TABLE_COFFEE = "COFFEE";
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_INPUT = "INPUT";
        public static final String COLUMN_OUTPUT = "OUTPUT";
        public static final String COLUMN_TDS = "TDS";
        public static final String COLUMN_BEANS = "BEANS";
        public static final String DATABASE_CREATE = "CREATE TABLE " +
                CoffeeEntry.TABLE_COFFEE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_INPUT + " DECIMAL, "
                + COLUMN_OUTPUT + " DECIMAL, "
                + COLUMN_TDS +" DECIMAL, "
                + COLUMN_BEANS + " TEXT);";
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_COFFEE;
        public static final String COLUMN_NAME_NULLABLE = "NULL";
        public static final String SQL_SELECT_ENTRIES = "SELECT * FROM " + TABLE_COFFEE;
    }
}
