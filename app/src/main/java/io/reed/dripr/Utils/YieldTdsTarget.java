package io.reed.dripr.Utils;

import android.content.Context;
import android.util.Log;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.util.Arrays;

import io.reed.dripr.R;

/**
 * Created by reed on 12/3/15.
 */
public class YieldTdsTarget {
    private double yieldTarget;
    private double yieldTolerances;
    private double tdsTarget;
    private double tdsTolerances;


    private double beanAbsorptionFactor;

    // Default values
    public static final double DEFAULT_YIELD_TARGET = 20;
    public static final double DEFAULT_YIELD_TOLERANCES = 2;
    public static final double DEFAULT_TDS_TARGET = 1.25;
    public static final double DEFAULT_BEAN_ABSORPTION = 2;
    public static final double DEFAULT_TDS_TOLERANCES = .1;
    public static final double DEFAULT_ESPRESSO_YIELD = 25;
    public static final double DEFAULT_ESPRESSO_TDS = 9.25;

    // Default constructor
    public YieldTdsTarget() {
        this.yieldTarget = DEFAULT_YIELD_TARGET;
        this.yieldTolerances = DEFAULT_YIELD_TOLERANCES;
        this.tdsTarget = DEFAULT_TDS_TARGET;
        this.tdsTolerances = DEFAULT_TDS_TOLERANCES;
        this.beanAbsorptionFactor = DEFAULT_BEAN_ABSORPTION;
    }

    public YieldTdsTarget(double yieldTarget, double yieldTolerances,
                           double tdsTarget, double tdsTolerances, double beanAbsorptionFactor) {
        this.yieldTarget = yieldTarget;
        this.yieldTolerances = yieldTolerances;
        this.tdsTarget = tdsTarget;
        this.tdsTolerances = tdsTolerances;
    }

    // Updates a given graph with bounds
    public void updateGraphBounds(Context context, XYPlot graph, SimpleXYSeries[] mTargetBounds) {
        graph.setDomainLowerBoundary(getYieldMin(), BoundaryMode.FIXED);
        graph.setDomainUpperBoundary(getYieldMax(), BoundaryMode.FIXED);
        graph.setRangeLowerBoundary(getTdsMin(), BoundaryMode.FIXED);
        graph.setRangeUpperBoundary(getTdsMax(), BoundaryMode.FIXED);
        SimpleXYSeries[] targetBounds = new SimpleXYSeries[4];
        Number[] domain = {getYieldMin(), getYieldMax()};
        Number[] range = {getTdsMin(), getTdsMax()};
        Number[][] bounds = new Number[4][2];
        bounds[0] = new Number[]{getTdsTarget() - getTdsTolerances(), getTdsTarget() - getTdsTolerances()};
        bounds[1] = new Number[]{getTdsTarget() + getTdsTolerances(), getTdsTarget() + getTdsTolerances()};
        bounds[2] = new Number[]{getYieldTarget() - getYieldTolerances(), getYieldTarget() - getYieldTolerances()};
        bounds[3] = new Number[]{getYieldTarget() + getYieldTolerances(), getYieldTarget() + getYieldTolerances()};
        for(int i = 0; i < targetBounds.length; i++) {
            if(i/2 == 0) {
                targetBounds[i] = new SimpleXYSeries(Arrays.asList(domain), Arrays.asList(bounds[i]), "Title");
            } else {
                targetBounds[i] = new SimpleXYSeries(Arrays.asList(bounds[i]), Arrays.asList(range), "Title");
            }
        }
        // Set up the formatter for the lines
        LineAndPointFormatter prefFormatter = new LineAndPointFormatter();
        prefFormatter.configure(context, R.xml.calc_pref_formatter);
        for(int i = 0; i < mTargetBounds.length; i++) {
            graph.removeSeries(mTargetBounds[i]);
            graph.addSeries(targetBounds[i], prefFormatter);
            mTargetBounds[i] = targetBounds[i];
        }
        graph.redraw();
    }

    public SimpleXYSeries drawFormulaLine(Context context, XYPlot graph) {
        Number[] xVals = null;
        Number[] yVals = null;
        if(graph.getHeight() >= graph.getWidth()) {
            xVals = new Number[]{getTdsMin()*getYieldTarget()/getTdsTarget(),  getTdsMax()*getYieldTarget()/getTdsTarget()};
            yVals = new Number[]{getTdsMin(), getTdsMax()};
        } else {
            xVals = new Number[]{getYieldMin(), getYieldMax()};
            yVals = new Number[]{getYieldMin()*getTdsTarget()/getYieldTarget(), getYieldMax()*getTdsTarget()/getYieldTarget()};
        }
        SimpleXYSeries line = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "Title");
        LineAndPointFormatter formatter = new LineAndPointFormatter();
        formatter.configure(context, R.xml.line_graph_formatter);
        graph.addSeries(line, formatter);
        graph.redraw();
        return line;
    }

    public double getYieldMin() {
        return yieldTarget - 3*yieldTolerances;
    }

    public double getYieldMax() {
        return yieldTarget + 3*yieldTolerances;
    }

    public double getTdsMin() {
        return  tdsTarget - 3*tdsTolerances;
    }

    public double getTdsMax() {
        return tdsTarget + 3*tdsTolerances;
    }

    public double getYieldTarget() {
        return yieldTarget;
    }

    public double getYieldTolerances() {
        return yieldTolerances;
    }

    public double getTdsTarget() {
        return tdsTarget;
    }

    public double getTdsTolerances() {
        return tdsTolerances;
    }

    public double getBeanAbsorptionFactor() {
        return beanAbsorptionFactor;
    }
}
