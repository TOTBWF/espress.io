package io.reed.dripr;


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
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.reed.dripr.Utils.DatabaseHelper;
import io.reed.dripr.Utils.YieldTdsTarget;


/**
 * A simple {@link Fragment} subclass.
 */
public class VisualizerFragment extends Fragment {

    private Spinner mBeanSpinner;
    private XYPlot mGraph;
    private LineAndPointFormatter mFormatter;
    private DatabaseHelper mDbHelper;
    private SimpleXYSeries mTargetLine;
    private SimpleXYSeries mPoints;
    private YieldTdsTarget mTargetValues;
    private SimpleXYSeries[] mTargetBounds;

    public VisualizerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_visualizer, container, false);
        mBeanSpinner = (Spinner)v.findViewById(R.id.visualizer_spinner_bean);
        mGraph = (XYPlot)v.findViewById(R.id.visualizer_graph);
        mDbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        mTargetValues = new YieldTdsTarget();
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
        // TODO Read values from db
        ArrayList<String> options = new ArrayList<String>();
        // Add defaults
        options.add("Default Drip");
        options.add("Default Espresso");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, options);
        mBeanSpinner.setAdapter(adapter);
        mBeanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Default drip
                        mTargetValues = new YieldTdsTarget();
                        break;
                    case 1:
                        mTargetValues = new YieldTdsTarget(YieldTdsTarget.DEFAULT_ESPRESSO_YIELD, YieldTdsTarget.DEFAULT_YIELD_TOLERANCES,
                                YieldTdsTarget.DEFAULT_ESPRESSO_TDS, YieldTdsTarget.DEFAULT_TDS_TOLERANCES, YieldTdsTarget.DEFAULT_BEAN_ABSORPTION);
                        break;
                    default:
                        break;

                }
                mTargetValues.updateGraphBounds(getActivity(), mGraph, mTargetBounds);
                mPoints = getDatabasePoints(mDbHelper.getEntries(""));
                mGraph.addSeries(mPoints, mFormatter);
                mGraph.removeSeries(mTargetLine);
                mTargetLine = mTargetValues.drawFormulaLine(getActivity(), mGraph);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private SimpleXYSeries getDatabasePoints(Cursor cursor) {
        ArrayList<Double> xVals = new ArrayList<>();
        ArrayList<Double> yVals = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            double input = cursor.getDouble(1);
            double output = cursor.getDouble(2);
            double tds = cursor.getDouble(3);
            double yield = tds * output / input;
            xVals.add(yield);
            yVals.add(tds);
            cursor.moveToNext();
        }
        return new SimpleXYSeries(xVals, yVals, "Title");
    }
}
