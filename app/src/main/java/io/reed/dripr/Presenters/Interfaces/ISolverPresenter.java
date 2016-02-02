package io.reed.dripr.Presenters.Interfaces;

import android.content.Context;

import io.reed.dripr.Views.Interfaces.ISolverView;

/**
 * Created by reed on 2/1/16.
 */
public interface ISolverPresenter {
    void onTakeView(ISolverView view, Context context);
    void updateTargets();
    void updateUnits();
    void setSelectedTarget(int index);
    void updateSolutionType(int index);
    void computeSolution(String input, boolean includeGrindMass);
}
