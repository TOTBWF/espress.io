package io.reed.dripr.Presenters;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;

import io.reed.dripr.Models.DatabaseHelper;
import io.reed.dripr.Models.YieldTdsTarget;
import io.reed.dripr.Presenters.Interfaces.IProfilePresenter;
import io.reed.dripr.R;
import io.reed.dripr.Views.Interfaces.IProfileView;

/**
 * Created by reed on 1/29/16.
 */
public class ProfilePresenter implements IProfilePresenter {

    private IProfileView view;
    private Context context;
    private Resources resources;
    private DatabaseHelper dbHelper;
    private ArrayList<YieldTdsTarget> targets;
    private YieldTdsTarget selectedTarget;

    public void onTakeView(IProfileView view, Context context) {
        this.view = view;
        this.context = context;
        this.resources = context.getResources();
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public void updateTargets() {
        this.targets = dbHelper.getStoredTargets();
        // Create a new profile option to add at the beginning of the list
        YieldTdsTarget newProfile = new YieldTdsTarget(resources.getString(R.string.new_profile),
                YieldTdsTarget.DEFAULT_DRIP_YIELD_TARGET,
                YieldTdsTarget.DEFAULT_ESPRESSO_YIELD_TOLERANCES,
                YieldTdsTarget.DEFAULT_DRIP_TDS_TARGET,
                YieldTdsTarget.DEFAULT_DRIP_TDS_TOLERANCES,
                YieldTdsTarget.DEFAULT_DRIP_BEAN_ABSORPTION);
        targets.add(0, newProfile);
        ArrayList<String> targetNames = new ArrayList<>();
        for(YieldTdsTarget target: targets) {
            targetNames.add(target.getName());
        }
        // The entry for the new profile has a different name then the profile
        targetNames.set(0, resources.getString(R.string.new_profile_spinner));
        view.updateTargetSpinner(targetNames);
    }

    @Override
    public void setSelectedTarget(int index) {
        YieldTdsTarget target = targets.get(index);
        view.updateName(target.getName());
        view.updateTDSTarget(Double.toString(target.getTdsTarget()));
        view.updateYieldTarget(Double.toString(target.getYieldTarget()));
        view.updateTDSTolerances(Double.toString(target.getTdsTolerances()));
        view.updateYieldTolerances(Double.toString(target.getYieldTolerances()));
        view.updateBeanAbsorption(Double.toString(target.getBeanAbsorptionFactor()));
        selectedTarget = targets.get(index);
        updateButtonStatus();
    }

    @Override
    public void updateFields(String name, String tdsTarget, String yieldTarget, String tdsTolerances, String yieldTolerances, String beanAbsorption) {
        // Make a new target object because targets are immutable
        try {
            selectedTarget = new YieldTdsTarget(name,
                    Double.parseDouble(yieldTarget),
                    Double.parseDouble(yieldTolerances),
                    Double.parseDouble(tdsTarget),
                    Double.parseDouble(tdsTolerances),
                    Double.parseDouble(beanAbsorption));
            updateButtonStatus();
        } catch (NumberFormatException e) {
            // Do nothing
            view.enableSavebutton(false);
        }
    }

    @Override
    public boolean saveSelectedToDB() {
        boolean isUpdate = isUpdate();
        if(isUpdate) {
            dbHelper.updateTargetInDatabase(selectedTarget);
        } else {
            dbHelper.writeTargetToDatabase(selectedTarget);
        }
        return isUpdate;
    }

    @Override
    public void deleteSelectedFromDB() {
        dbHelper.deleteTargetFromDatabase(selectedTarget);
    }

    // updates button status based off the current selected target
    private void updateButtonStatus() {
        if(selectedTarget.getName().equals(resources.getString(R.string.new_profile)) || selectedTarget.getName().equals("")) {
            // We don't want people saving things as new profile or "", so disable the save button
            view.enableDeleteButton(false);
            view.enableSavebutton(false);
            view.updateSaveButtonText(resources.getString(R.string.button_save));
            return;
        }
        // Check to see if the name exists
        // If it does, we consider the operation as an update rather than a save
        if(!isUpdate()) {
            view.enableSavebutton(true);
            view.enableDeleteButton(true);
            view.updateSaveButtonText(resources.getString(R.string.button_save));
        } else {
            view.enableSavebutton(true);
            view.enableDeleteButton(true);
            view.updateSaveButtonText(resources.getString(R.string.button_update));
        }
    }

    // Checks to see if the selected item is an update or not
    private boolean isUpdate() {
        for(YieldTdsTarget target: targets) {
            if(target.getName().equals(selectedTarget.getName())) {
                return true;
            }
        }
        return false;
    }
}
