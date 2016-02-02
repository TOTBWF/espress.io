package io.reed.dripr.Presenters;

import android.content.Context;

import io.reed.dripr.Views.ISolverView;

/**
 * Created by reed on 2/1/16.
 */
public interface ISolverPresenter {
    void onTakeView(ISolverView view, Context context);
    void updateTargets();
    void setSelectedTarget(int index);
    void updateSolutionType(int index);
    void computeSolution(String input, boolean includeGrindMass);
}
