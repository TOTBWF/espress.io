package io.reed.dripr.Views.Interfaces;

/**
 * Created by reed on 1/29/16.
 */
public interface ICalculatorView extends IGraphView, ITargetSpinnerView {
    void updateDoseLabel(String doseLabel);
    void updateOutputLabel(String outputLabel);
    void updateDose(String dose);
    void updateOutput(String output);
    void updateBrix(String brix);
    void updateTDS(String tds);
    void updateYield(String yield);
    void setSaveEnabled(boolean enabled);
}
