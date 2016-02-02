package io.reed.dripr.Presenters;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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
import java.util.List;

import io.reed.dripr.Models.CoffeeDatabaseContract;
import io.reed.dripr.Models.DatabaseHelper;
import io.reed.dripr.Views.ISettingsView;

/**
 * Created by reed on 1/29/16.
 */
public class SettingsPresenter implements ISettingsPresenter{

    private ISettingsView view;
    private DatabaseHelper dbHelper;
    private Context context;

    public SettingsPresenter(Context context) {
        this.context = context;
    }

    public void onTakeView(ISettingsView view, Context context) {
        this.view = view;
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }
    public void settingNotYetImplemented() {
        Toast.makeText(context, "Setting Not Implemented!", Toast.LENGTH_SHORT).show();
        Log.d(this.getClass().getName(), "Setting Not Yet Implemented!");
    }


    public void dumpDbToCsv() {
        new DumpDBToCsvAsyncTask().execute(CoffeeDatabaseContract.CoffeeEntry.TABLE_COFFEE,
                CoffeeDatabaseContract.ProfileEntry.TABLE_PROFILE);
    }

    public void clearDb() {
        // TODO: Add in saftey checks!!!!!!!
        dbHelper.clearDatabase();
    }

    // Task for writing out the DB to a csv file. Takes in table names.
    private class DumpDBToCsvAsyncTask extends AsyncTask<String, Integer, Boolean> {
        // TODO: Fix the progress bar so that it actually, well, progresses
        private final ProgressDialog progressDialog = new ProgressDialog(context);
        private int taskIndex;

        @Override
        protected void onPreExecute() {
            // Setup the dialog box
            progressDialog.setMessage("Exporting Database to CSV...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.show();
            taskIndex = 0;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // First check if we can write to external storage
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                Log.e(this.getClass().getName(), "DB Dump failed! External Storage in state:" + Environment.getExternalStorageState());
                return false;
            }
            // Set up our progress stuff
            Integer[] taskProgress = new Integer[params.length];
            for (int i = 0; i < params.length; i++) {
                String tableName = params[i];
                taskIndex = i;
                // First begin by auto-generating file names
                SimpleDateFormat date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                final String fileName = tableName + "_" + date.format(new Date()) + ".csv";
                File csvFile = new File(Environment.getExternalStorageDirectory(), fileName);
                try {
                    OutputStream fOut = new BufferedOutputStream(new FileOutputStream(csvFile));
                    // Start looping moving through the db
                    Cursor cursor = null;
                    switch (tableName) {
                        case CoffeeDatabaseContract.CoffeeEntry.TABLE_COFFEE:
                            cursor = dbHelper.getReadableDatabase().rawQuery(CoffeeDatabaseContract.CoffeeEntry.SQL_SELECT_ENTRIES, null);
                            break;
                        case CoffeeDatabaseContract.ProfileEntry.TABLE_PROFILE:
                            cursor = dbHelper.getReadableDatabase().rawQuery(CoffeeDatabaseContract.ProfileEntry.SQL_SELECT_ENTRIES, null);
                            break;
                        default:
                            Log.e(this.getClass().getName(), "DB Dump failed! Not a valid table name");
                            return false;
                    }
                    cursor.moveToFirst();
                    // Start by creating the columns of the csv
                    int bufferSize = 0;
                    String buffer = "";
                    // Get the column names, write them into a buffer, and replace the last char with a newline
                    String[] columnNames = cursor.getColumnNames();
                    for (String columnName : columnNames) {
                        buffer += columnName + ",";
                    }
                    buffer = buffer.substring(0, buffer.length() - 1) + "\n";

                    bufferSize = buffer.getBytes().length;
                    fOut.write(buffer.getBytes(), 0, bufferSize);
                    // Now that we have our columns, start writing entries
                    while (!cursor.isAfterLast()) {
                        // First, publish the current progress
                        int progress = 100 * (cursor.getPosition() / cursor.getCount());
                        taskProgress[taskIndex] = progress;
                        publishProgress(taskProgress);
                        // Clear the buffer
                        buffer = "";
                        // Loop through all of the columns
                        for (int columnIndex = 0; columnIndex < cursor.getColumnCount(); columnIndex++) {
                            buffer += cursor.getString(columnIndex) + ",";
                        }
                        // Replace the last char with a newline
                        buffer = buffer.substring(0, buffer.length() - 1) + "\n";
                        bufferSize = buffer.getBytes().length;
                        fOut.write(buffer.getBytes(), 0, bufferSize);
                        cursor.moveToNext();
                    }
                    cursor.close();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    Log.e(this.getClass().getName(), "DB Dump failed! FileNotFoundException:" + e.getMessage());
                    return false;
                } catch (IOException e) {
                    Log.e(this.getClass().getName(), "DB Dump failed! IOException:" + e.getMessage());
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[taskIndex]);
        }
    }

    private class LoadDbFromCsvAsync extends AsyncTask<String, Integer, Boolean> {
        // TODO: Fix the progress bar so that it actually, well, progresses
        private final ProgressDialog progressDialog = new ProgressDialog(context);
        private int taskIndex;

        @Override
        protected void onPreExecute() {
            // Setup the dialog box
            progressDialog.setMessage("Importing Database from CSV...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.show();
            taskIndex = 0;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // First check if we can read from external storage
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                Log.e(this.getClass().getName(), "DB load failed! External Storage in state:" + Environment.getExternalStorageState());
                return false;
            }
            Integer[] taskProgress = new Integer[params.length];
            for (int i = 0; i < params.length; i++) {
                String filePath = params[i];
                taskIndex = i;
                // First, open up the file at the given path
                File csvFile = new File(filePath);
                /* Now we determine the table type, which is the string until the first underscore of the filename
                 *  Is this hacky? Yes. Could it cause unforeseen issues with renamed files? Hell yes.
                 *  Will I change this if it becomes an issue? Yes.
                 *  Until that point is reached, this is the way it is going to be.
                 *  Later me will probably hate me for this.
                 */
                String tableName = filePath.split("_")[0];
                Cursor cursor = null;
                switch (tableName) {
                    case CoffeeDatabaseContract.CoffeeEntry.TABLE_COFFEE:
                        cursor = dbHelper.getReadableDatabase().rawQuery(CoffeeDatabaseContract.CoffeeEntry.SQL_SELECT_ENTRIES, null);
                        Log.d(this.getClass().getName(), "Loading data to coffee table");
                        break;
                    case CoffeeDatabaseContract.ProfileEntry.TABLE_PROFILE:
                        cursor = dbHelper.getReadableDatabase().rawQuery(CoffeeDatabaseContract.ProfileEntry.SQL_SELECT_ENTRIES, null);
                        Log.d(this.getClass().getName(), "Loading data to profile table");
                        break;
                    default:
                        Log.e(this.getClass().getName(), "DB load failed! Not a valid table name");
                        return false;
                }
                try {
                    InputStream fIn = new BufferedInputStream(new FileInputStream(csvFile));
                    cursor.moveToFirst();
                    int readByte = 0;
                    int columnIndex = 0;
                    String buffer = "";
                    ArrayList<String> columnNames = new ArrayList<>();
                    boolean firstRow = true;
                    ContentValues values = new ContentValues();
                    while ((readByte = fIn.read()) >= 0) {
                        // TODO: Check for duplicates
                        // If the byte that has been read in is a newline char, write the buffer and move the cursor one row down
                        if (readByte == '\n') {
                            if (firstRow) {
                                firstRow = false;
                            } else {
                                dbHelper.getReadableDatabase().insert(tableName, CoffeeDatabaseContract.COLUMN_NAME_NULLABLE, values);
                                values = new ContentValues();
                                cursor.moveToNext();
                            }
                            columnIndex = 0;
                        } else if (readByte == ',') {
                            if (firstRow) {
                                columnNames.add(columnIndex++, buffer);
                            } else {

                            }
                        }
                    }
                } catch (Exception e) {

                }
                cursor.close();
            }
            return true;
        }
    }
}
