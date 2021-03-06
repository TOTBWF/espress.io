package io.reed.dripr.Presenters.Interfaces;

import android.content.Context;

import io.reed.dripr.Views.Interfaces.ICalculatorView;

/**
 * Created by Reed Mullanix on 1/29/16.
 * Interface for the Calculator Presenter
 */
public interface ICalculatorPresenter {
    void onTakeView(ICalculatorView view, Context context);
    void updateUnits();
    void updateFields(String dose, String output, String Brix);
    void updateTargets();
    void saveToDB(String dose, String output, String brix, String beans);
    void setSelectedTarget(int index);
}
