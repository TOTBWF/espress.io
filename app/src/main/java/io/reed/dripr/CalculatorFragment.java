package io.reed.dripr;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.androidplot.LineRegion;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphBounds;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Fragment for TDS and Yield Calculation
 * @author Reed Mullanix
 */
public class CalculatorFragment extends Fragment {

    // Editable fields
    private EditText mDoseEdit;
    private EditText mOutputEdit;
    private EditText mBrixEdit;
    private EditText mTdsEdit;
    private EditText mYieldEdit;
    private XYPlot mGraph;
    private XYSeries mPoints;
    private LineAndPointFormatter mFormatter;
    private final DecimalFormat format = new DecimalFormat("0.##");

    public CalculatorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set all of our fields
        View v = inflater.inflate(R.layout.fragment_calculator, container, false);
        mDoseEdit = (EditText)v.findViewById(R.id.edit_dose);
        mOutputEdit = (EditText)v.findViewById(R.id.edit_output);
        mBrixEdit = (EditText)v.findViewById(R.id.edit_brix);
        mTdsEdit = (EditText)v.findViewById(R.id.edit_tds);
        mYieldEdit = (EditText)v.findViewById(R.id.edit_yield);
        mGraph = (XYPlot)v.findViewById(R.id.graph_calc);
        setupGraph();
        // Add on listeners so we know when to calculate
        mBrixEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double brix = convertEditToDouble(mBrixEdit);
                if (brix != 0) {
                    mTdsEdit.setText(format.format(brix / 1.18), EditText.BufferType.NORMAL);
                } else {
                    mTdsEdit.setText(null);
                }
            }
        });
        // Set up the watcher for the 3 fields associated with yield
        TextWatcher yieldWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double tds = convertEditToDouble(mTdsEdit);
                double output = convertEditToDouble(mOutputEdit);
                double dose = convertEditToDouble(mDoseEdit);
                if(tds != 0 && output != 0 && dose != 0) {
                    mYieldEdit.setText(format.format(tds * output / dose));
                    // Add a point to the graph
                    // Cant just add one point, have to do it in a silly way
                    Number[] xVals = {tds*output/dose};
                    Number[] yVals = {tds};
                    if(mPoints != null) {
                        mGraph.removeSeries(mPoints);
                    }
                    mPoints = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "Yield vs TDS");
                    mGraph.addSeries(mPoints, mFormatter);
                    mGraph.redraw();
                } else {
                    mYieldEdit.setText(null);
                    mGraph.removeSeries(mPoints);
                    mPoints = null;
                    mGraph.redraw();
                }
            }
        };
        mDoseEdit.addTextChangedListener(yieldWatcher);
        mOutputEdit.addTextChangedListener(yieldWatcher);
        mTdsEdit.addTextChangedListener(yieldWatcher);
        return v;
    }

    private void setupGraph() {
        // TODO: Get preferred values from settings
        double yieldMin = 14;
        double yieldMax = 26;
        double yieldTarget = 20;
        double yieldTolerances = 2;
        double tdsMin = .9;
        double tdsMax = 1.6;
        double tdsTarget = 1.25;
        double tdsTolerances = .1;
        // Scaling
        mGraph.getDomainLabelWidget().position(0, XLayoutStyle.ABSOLUTE_FROM_CENTER,
                0, YLayoutStyle.RELATIVE_TO_BOTTOM, AnchorPosition.BOTTOM_MIDDLE);
        mGraph.setDomainLowerBoundary(yieldMin, BoundaryMode.FIXED);
        mGraph.setDomainUpperBoundary(yieldMax, BoundaryMode.FIXED);
        mGraph.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        mGraph.setDomainValueFormat(new DecimalFormat("#"));
        mGraph.setRangeLowerBoundary(tdsMin, BoundaryMode.FIXED);
        mGraph.setRangeUpperBoundary(tdsMax, BoundaryMode.FIXED);
        mGraph.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.1);
        mGraph.setTicksPerRangeLabel(2);
        mGraph.setRangeValueFormat(new DecimalFormat("#.#"));
        // Set up point formatter
        mFormatter = new LineAndPointFormatter();
        mFormatter.configure(getActivity(), R.xml.calc_graph_formatter);
        // Try to add the preference lines
        SimpleXYSeries[] targetBounds = new SimpleXYSeries[4];
        Number[] domain = {yieldMin,  yieldMax};
        Number[] range = {tdsMin, tdsMax};
        Number[][] bounds = new Number[4][2];
        bounds[0] = new Number[]{tdsTarget - tdsTolerances, tdsTarget - tdsTolerances};
        bounds[1] = new Number[]{tdsTarget + tdsTolerances, tdsTarget + tdsTolerances};
        bounds[2] = new Number[]{yieldTarget - yieldTolerances, yieldTarget - yieldTolerances};
        bounds[3] = new Number[]{yieldTarget + yieldTolerances, yieldTarget + yieldTolerances};
        for(int i = 0; i < targetBounds.length; i++) {
            if(i/2 == 0) {
                targetBounds[i] = new SimpleXYSeries(Arrays.asList(domain), Arrays.asList(bounds[i]), "Title");
            } else {
                targetBounds[i] = new SimpleXYSeries(Arrays.asList(bounds[i]), Arrays.asList(range), "Title");
            }
        }
        // Set up the formatter for the lines
        LineAndPointFormatter prefFormatter = new LineAndPointFormatter();
        prefFormatter.configure(getActivity(), R.xml.calc_pref_formatter);
        for(SimpleXYSeries s: targetBounds) {
           mGraph.addSeries(s, prefFormatter);
        }
    }

    private double convertEditToDouble(EditText edit) {
        try {
            return Double.parseDouble(edit.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
