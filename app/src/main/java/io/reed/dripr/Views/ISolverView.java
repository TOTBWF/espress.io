package io.reed.dripr.Views;

/**
 * Created by reed on 2/1/16.
 */
public interface ISolverView extends ITargetSpinnerView {
    void updateInput(String input);
    void updateSolution(String solution);
    void updateInputLabel(String label);
    void updateSolutionLabel(String label);
}
