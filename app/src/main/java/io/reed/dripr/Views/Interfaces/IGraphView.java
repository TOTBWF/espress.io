package io.reed.dripr.Views.Interfaces;

import com.androidplot.xy.SimpleXYSeries;

import io.reed.dripr.Models.YieldTdsTarget;

/**
 * Created by reed on 1/29/16.
 */
public interface IGraphView {
    void drawFormulaLine(YieldTdsTarget target);
    void updateGraphbounds(YieldTdsTarget target);
    void plotPoint(double x, double y);
    void clearPoints();
}
