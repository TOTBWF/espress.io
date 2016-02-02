package io.reed.dripr.Presenters;

import android.content.Context;

import io.reed.dripr.Views.ICalculatorView;

/**
 * Created by reed on 1/29/16.
 */
public interface ICalculatorPresenter {
    void onTakeView(ICalculatorView view, Context context);
    void updateUnits();
    void updateFields(String dose, String output, String Brix);
    void updateTargets();
    void saveToDB(String dose, String output, String brix, String beans);
    void setSelectedTarget(int index);
}
