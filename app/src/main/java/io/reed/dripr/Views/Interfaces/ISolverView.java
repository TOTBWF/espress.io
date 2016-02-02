package io.reed.dripr.Views.Interfaces;

/**
 * Created by reed on 2/1/16.
 */
public interface ISolverView extends ITargetSpinnerView {
    void updateInput(String input);
    void updateSolution(String solution);
    void updateInputLabel(String label);
    void updateSolutionLabel(String label);
    void updateInputUnitLabel(String unit);
    void updateSolutionUnitLabel(String unit);
}
