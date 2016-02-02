package io.reed.dripr.Views;


import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import io.reed.dripr.Presenters.IVisualizerPresenter;
import io.reed.dripr.Presenters.VisualizerPresenter;
import io.reed.dripr.R;
import io.reed.dripr.Models.DatabaseHelper;
import io.reed.dripr.Models.YieldTdsTarget;


/**
 * A simple {@link Fragment} subclass.
 */
public class VisualizerFragment extends Fragment implements IVisualizerView {

    private Spinner mBeanSpinner;
    private XYPlot mGraph;
    private LineAndPointFormatter mFormatter;
    private SimpleXYSeries mTargetLine;
    private SimpleXYSeries mPoints;
    private SimpleXYSeries[] mTargetBounds;

    private IVisualizerPresenter presenter;

    public VisualizerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(presenter == null) {
            presenter = new VisualizerPresenter();
        }
        presenter.onTakeView(this, getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_visualizer, container, false);
        mBeanSpinner = (Spinner)v.findViewById(R.id.visualizer_spinner_bean);
        mGraph = (XYPlot)v.findViewById(R.id.visualizer_graph);
        mTargetBounds = new SimpleXYSeries[4];
        setupGraph();
        setupSpinner();
        return v;
    }

    private void setupGraph() {
        // TODO: Get preferred values from settings
        // Scaling
        mGraph.getDomainLabelWidget().position(0, XLayoutStyle.ABSOLUTE_FROM_CENTER,
                0, YLayoutStyle.RELATIVE_TO_BOTTOM, AnchorPosition.BOTTOM_MIDDLE);
        mGraph.setDomainStep(XYStepMode.INCREMENT_BY_VAL, .25);
        mGraph.setDomainValueFormat(new DecimalFormat("#"));
        mGraph.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.025);
        mGraph.setRangeValueFormat(new DecimalFormat("#.##"));
        // Set up point formatter
        mFormatter = new LineAndPointFormatter();
        mFormatter.configure(getActivity(), R.xml.calc_graph_formatter);
    }

    private void setupSpinner() {
        presenter.updateTargets();
        mBeanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setSelectedTarget(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void drawFormulaLine(YieldTdsTarget target) {
        Number[] xVals = null;
        Number[] yVals = null;
        if(mGraph.getHeight() >= mGraph.getWidth()) {
            xVals = new Number[]{target.getTdsMin()*target.getYieldTarget()/target.getTdsTarget(),
                    target.getTdsMax()*target.getYieldTarget()/target.getTdsTarget()};
            yVals = new Number[]{target.getTdsMin(), target.getTdsMax()};
        } else {
            xVals = new Number[]{target.getYieldMin(), target.getYieldMax()};
            yVals = new Number[]{target.getYieldMin()*target.getTdsTarget()/target.getYieldTarget(),
                    target.getYieldMax()*target.getTdsTarget()/target.getYieldTarget()};
        }
        SimpleXYSeries line = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "Title");
        LineAndPointFormatter formatter = new LineAndPointFormatter();
        formatter.configure(getActivity(), R.xml.line_graph_formatter);
        if(mTargetLine != null) {
            mGraph.removeSeries(mTargetLine);
        }
        mTargetLine = line;
        mGraph.addSeries(mTargetLine, formatter);
        mGraph.redraw();
    }

    @Override
    public void updateGraphbounds(YieldTdsTarget target) {
        // Set the graph bounds
        mGraph.setDomainLowerBoundary(target.getYieldMin(), BoundaryMode.FIXED);
        mGraph.setDomainUpperBoundary(target.getYieldMax(), BoundaryMode.FIXED);
        mGraph.setRangeLowerBoundary(target.getTdsMin(), BoundaryMode.FIXED);
        mGraph.setRangeUpperBoundary(target.getTdsMax(), BoundaryMode.FIXED);
        SimpleXYSeries[] targetBounds = new SimpleXYSeries[4];
        Number[] domain = {target.getYieldMin(), target.getYieldMax()};
        Number[] range = {target.getTdsMin(), target.getTdsMax()};
        Number[][] bounds = new Number[4][2];
        bounds[0] = new Number[]{target.getTdsTarget() - target.getTdsTolerances(), target.getTdsTarget() - target.getTdsTolerances()};
        bounds[1] = new Number[]{target.getTdsTarget() + target.getTdsTolerances(), target.getTdsTarget() + target.getTdsTolerances()};
        bounds[2] = new Number[]{target.getYieldTarget() - target.getYieldTolerances(), target.getYieldTarget() - target.getYieldTolerances()};
        bounds[3] = new Number[]{target.getYieldTarget() + target.getYieldTolerances(), target.getYieldTarget() + target.getYieldTolerances()};
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
        for(int i = 0; i < mTargetBounds.length; i++) {
            if(mTargetBounds[i] != null) {
                mGraph.removeSeries(mTargetBounds[i]);
            }
            mGraph.addSeries(targetBounds[i], prefFormatter);
            mTargetBounds[i] = targetBounds[i];
        }
        mGraph.redraw();
    }

    @Override
    public void plotPoint(double x, double y) {
        mPoints.addLast(x, y);
    }

    @Override
    public void clearPoints() {
        mGraph.removeSeries(mPoints);
        mPoints = new SimpleXYSeries("Title");
        mGraph.addSeries(mPoints, mFormatter);
    }

    @Override
    public void updateTargetSpinner(ArrayList<String> targetNames) {
        // Get the names of the profiles
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, targetNames);
        mBeanSpinner.setAdapter(adapter);
    }
}
