package io.reed.dripr.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.util.ArrayList;
import java.util.Arrays;

import io.reed.dripr.R;

/**
 * Created by reed on 12/3/15.
 */
public class YieldTdsTarget {
    private String name;
    private double yieldTarget;
    private double yieldTolerances;
    private double tdsTarget;
    private double tdsTolerances;


    private double beanAbsorptionFactor;

    // Default values
    public static final String DEFAULT_DRIP_NAME = "Default Drip";
    public static final double DEFAULT_DRIP_YIELD_TARGET = 20;
    public static final double DEFAULT_DRIP_YIELD_TOLERANCES = 2;
    public static final double DEFAULT_DRIP_TDS_TARGET = 1.25;
    public static final double DEFAULT_DRIP_TDS_TOLERANCES = .1;
    public static final double DEFAULT_DRIP_BEAN_ABSORPTION = 2;

    public static final String DEFAULT_ESPRESSO_NAME = "Default Espresso";
    public static final double DEFAULT_ESPRESSO_YIELD_TARGET = 25;
    public static final double DEFAULT_ESPRESSO_YIELD_TOLERANCES = 2;
    public static final double DEFAULT_ESPRESSO_TDS_TARGET = 9.25;
    public static final double DEFAULT_ESPRESSO_TDS_TOLERANCES = .1;
    public static final double DEFAULT_ESPRESSO_BEAN_ABSORPTION = 2;

    public YieldTdsTarget(String name, double yieldTarget, double yieldTolerances,
                           double tdsTarget, double tdsTolerances, double beanAbsorptionFactor) {
        this.name = name;
        this.yieldTarget = yieldTarget;
        this.yieldTolerances = yieldTolerances;
        this.tdsTarget = tdsTarget;
        this.tdsTolerances = tdsTolerances;
        this.beanAbsorptionFactor = beanAbsorptionFactor;
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

    public String getName() {
        return name;
    }
}
