package io.reed.dripr.Presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import io.reed.dripr.Models.CoffeeModel;
import io.reed.dripr.Models.DatabaseHelper;
import io.reed.dripr.Models.SettingsHelper;
import io.reed.dripr.Models.YieldTdsTarget;
import io.reed.dripr.Presenters.Interfaces.ISolverPresenter;
import io.reed.dripr.R;
import io.reed.dripr.Views.Interfaces.ISolverView;

/**
 * Created by reed on 2/1/16.
 */
public class SolverPresenter implements ISolverPresenter {

    private ISolverView view;
    private Resources resources;
    private CoffeeModel coffeeModel;
    private Context context;
    private DatabaseHelper dbHelper;
    private ArrayList<YieldTdsTarget> targets;
    private YieldTdsTarget selectedTarget;
    private Solutions selectedSolution;
    private String unit;

    // Enum of solution types, makes code a little less magic number-y
    private enum Solutions {
        DOSE, OUTPUT
    }

    public SolverPresenter() {
        this.coffeeModel = new CoffeeModel();
    }

    public void onTakeView(ISolverView view, Context context) {
        this.view = view;
        this.context = context;
        this.resources = context.getResources();
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.targets = dbHelper.getStoredTargets();
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
    public void updateUnits() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unit = sharedPreferences.getString(SettingsHelper.MASS_UNIT_KEY, "g");
        this.unit = unit;
        view.updateInputUnitLabel(unit);
        view.updateSolutionUnitLabel(unit);
    }

    @Override
    public void setSelectedTarget(int index) {
        selectedTarget = targets.get(index);
    }

    @Override
    public void updateSolutionType(int index) {
        selectedSolution = Solutions.values()[index];
        switch (selectedSolution) {
            case DOSE:
                view.updateInputLabel(resources.getString(R.string.field_output));
                view.updateSolutionLabel(resources.getString(R.string.field_dose));
                break;
            case OUTPUT:
                view.updateInputLabel(resources.getString(R.string.field_dose));
                view.updateSolutionLabel(resources.getString(R.string.field_output));
                break;
        }
        view.updateInput("");
        view.updateSolution("");
    }

    @Override
    public void computeSolution(String input, boolean includeGrindMass) {
        double dInput;
        try {
            dInput = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            // If the number is funky, then just set the solution to empty
            view.updateSolution("");
            return;
        }
        if(dInput == 0) {
            view.updateSolution("");
            return;
        }
        boolean isOunces = unit.equals(resources.getString(R.string.unit_ounces));
        if(isOunces) {
            dInput = coffeeModel.convertOuncesToGrams(dInput);
        }
        double tds = selectedTarget.getTdsTarget();
        double yield = selectedTarget.getYieldTarget();
        double absorption = selectedTarget.getBeanAbsorptionFactor();
        double solution;
        switch (selectedSolution) {
            case DOSE:
                if(includeGrindMass) {
                    solution = coffeeModel.computeDose(dInput, tds, yield, absorption);
                } else {
                    solution = coffeeModel.computeDose(dInput, tds, yield, 0);
                }
                break;
            case OUTPUT:
                if(includeGrindMass) {
                    solution = coffeeModel.computeOutput(dInput, tds, yield, absorption);
                } else {
                    solution = coffeeModel.computeOutput(dInput, tds, yield, 0);
                }
                break;
            default:
                view.updateInput("");
                return;
        }
        if(isOunces) {
            solution = coffeeModel.convertGramsToOunces(solution);
        }
        view.updateSolution(Double.toString(solution));
    }
}
