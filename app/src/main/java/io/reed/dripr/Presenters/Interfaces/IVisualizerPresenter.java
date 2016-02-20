package io.reed.dripr.Presenters.Interfaces;

import android.content.Context;

import io.reed.dripr.Views.Interfaces.IVisualizerView;

/**
 * Created by Reed Mullanix on 2/1/16.
 */
public interface IVisualizerPresenter {
    void onTakeView(IVisualizerView view, Context context);
    void updateTargets();
    void setSelectedTarget(int index);
}
