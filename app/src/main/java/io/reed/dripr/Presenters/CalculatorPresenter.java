package io.reed.dripr.Presenters;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import io.reed.dripr.Models.CoffeeDatabaseContract;
import io.reed.dripr.Models.CoffeeModel;
import io.reed.dripr.Models.DatabaseHelper;
import io.reed.dripr.Models.SettingsHelper;
import io.reed.dripr.Models.YieldTdsTarget;
import io.reed.dripr.Views.ICalculatorView;

/**
 * Created by reed on 1/29/16.
 */
public class CalculatorPresenter implements ICalculatorPresenter {

    private ICalculatorView view;
    private CoffeeModel model;
    private Context context;
    private DatabaseHelper dbHelper;
    private ArrayList<YieldTdsTarget> targets;
    private YieldTdsTarget selectedTarget;
    private String unit;

    public CalculatorPresenter() {
        model = new CoffeeModel();
    }

    public void onTakeView(ICalculatorView view, Context context) {
        this.view = view;
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.targets = dbHelper.getStoredTargets();
    }

    @Override
    public void updateUnits() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unit = sharedPreferences.getString(SettingsHelper.MASS_UNIT_KEY, "g");
        this.unit = unit;
        view.updateDoseLabel(unit);
        view.updateOutputLabel(unit);
    }

    @Override
    public void updateFields(String dose, String output, String brix) {
        double dDose;
        double dOutput;
        double dBrix;
        try {
            dDose = Double.parseDouble(dose);
            dOutput = Double.parseDouble(output);
            dBrix = Double.parseDouble(brix);
        } catch (NumberFormatException e) {
            dDose = 0;
            dOutput = 0;
            dBrix = 0;
        }
        if(dBrix != 0) {
            double dTDS = model.convertBrixToTDS(dBrix);
            view.updateTDS(dTDS + "");
            if(dDose != 0 && dOutput != 0) {
                double dYield = model.computeYield(dDose, dOutput, dTDS);
                view.updateYield(dYield + "");
                view.plotPoint(dYield, dTDS);
                view.setSaveEnabled(true);
            } else {
                view.updateYield("");
                view.setSaveEnabled(false);
            }
        } else {
            view.updateTDS("");
            view.updateYield("");
            view.setSaveEnabled(false);
        }
    }

    @Override
    public void updateTargets() {
        ArrayList<String> targetNames = new ArrayList<>();
        for(YieldTdsTarget target: targets) {
            targetNames.add(target.getName());
        }
        view.updateTargetSpinner(targetNames);
    }

    @Override
    public void saveToDB(String dose, String output, String brix, String beans) {
        // First convert values to doubles and compute other values
        double dDose = Double.parseDouble(dose);
        double dOutput = Double.parseDouble(output);
        double dBrix = Double.parseDouble(brix);
        double dTDS = model.convertBrixToTDS(dBrix);
        // Make sure we always save in grams
        if(unit.equals("oz")) {
            dDose = model.convertOuncesToGrams(dDose);
            dOutput = model.convertOuncesToGrams(dOutput);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CoffeeDatabaseContract.CoffeeEntry.COLUMN_INPUT, dDose);
        values.put(CoffeeDatabaseContract.CoffeeEntry.COLUMN_OUTPUT, dOutput);
        values.put(CoffeeDatabaseContract.CoffeeEntry.COLUMN_TDS, dTDS);
        values.put(CoffeeDatabaseContract.CoffeeEntry.COLUMN_BEANS, beans);
        db.insert(CoffeeDatabaseContract.CoffeeEntry.TABLE_COFFEE, CoffeeDatabaseContract.COLUMN_NAME_NULLABLE, values);
        view.setSaveEnabled(false);
        view.updateDose("");
        view.updateOutput("");
        view.updateBrix("");
        view.clearPoints();
    }

    @Override
    public void setSelectedTarget(int index) {
        selectedTarget = targets.get(index);
        view.drawFormulaLine(selectedTarget);
        view.updateGraphbounds(selectedTarget);
    }
}
