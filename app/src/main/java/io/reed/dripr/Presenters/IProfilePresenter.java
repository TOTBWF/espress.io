package io.reed.dripr.Presenters;

import android.content.Context;

import io.reed.dripr.Views.IProfileView;

/**
 * Created by reed on 1/29/16.
 */
public interface IProfilePresenter {
    void onTakeView(IProfileView view, Context context);
    void updateTargets();
    void setSelectedTarget(int index);
    void updateFields(String name, String tdsTarget, String yieldTarget, String tdsTolerances, String yieldTolerances, String beanAbsorption);
    // Returns true if updated, false if saved
    boolean saveSelectedToDB();
    void deleteSelectedFromDB();
}
