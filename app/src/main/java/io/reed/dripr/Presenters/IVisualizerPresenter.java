package io.reed.dripr.Presenters;

import android.content.Context;

import io.reed.dripr.Views.IVisualizerView;

/**
 * Created by reed on 2/1/16.
 */
public interface IVisualizerPresenter {
    void onTakeView(IVisualizerView view, Context context);
    void updateTargets();
    void setSelectedTarget(int index);
}
