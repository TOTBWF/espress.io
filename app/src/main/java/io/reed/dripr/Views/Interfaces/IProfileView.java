package io.reed.dripr.Views.Interfaces;

/**
 * Created by reed on 1/29/16.
 */
public interface IProfileView extends ITargetSpinnerView {
    void updateName(String name);
    void updateTDSTarget(String tds);
    void updateYieldTarget(String yield);
    void updateTDSTolerances(String tolerance);
    void updateYieldTolerances(String tolerance);
    void updateBeanAbsorption(String absorption);
    void updateSaveButtonText(String text);
    void enableSavebutton(boolean enabled);
    void enableDeleteButton(boolean enabled);
}
