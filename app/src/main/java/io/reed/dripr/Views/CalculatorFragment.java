package io.reed.dripr.Views;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import io.reed.dripr.Presenters.CalculatorPresenter;
import io.reed.dripr.Presenters.Interfaces.ICalculatorPresenter;
import io.reed.dripr.R;
import io.reed.dripr.Models.YieldTdsTarget;
import io.reed.dripr.Views.Interfaces.ICalculatorView;

/**
 * Fragment for TDS and Yield Calculation
 * @author Reed Mullanix
 */
public class CalculatorFragment extends Fragment implements ICalculatorView {

    // Editable fields
    private EditText mDoseEdit;
    private EditText mOutputEdit;
    private EditText mBrixEdit;
    private EditText mTdsEdit;
    private EditText mYieldEdit;
    private TextView mDoseLabel;
    private TextView mOutputLabel;
    private XYPlot mGraph;
    private XYSeries mPoints;
    private LineAndPointFormatter mFormatter;
    private Button mSaveButton;
    private Spinner mSpinner;
    private SimpleXYSeries[] mTargetBounds;
    private SimpleXYSeries mTargetLine;
    // The presenter is static so that it is out of reach of the onCreate/onDestroy lifecycle
    private static ICalculatorPresenter presenter;

    public CalculatorFragment() {
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(presenter == null) {
            presenter = new CalculatorPresenter();
        }
        presenter.onTakeView(this, getActivity());
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
        mDoseLabel = (TextView)v.findViewById(R.id.calc_dose_unit);
        mOutputLabel = (TextView)v.findViewById(R.id.calc_output_unit);
        mGraph = (XYPlot)v.findViewById(R.id.graph_calc);
        mSaveButton = (Button)v.findViewById(R.id.button_calc_save);
        mSpinner = (Spinner)v.findViewById(R.id.bean_spinner);
        // Instantiate the database
        mTargetBounds = new SimpleXYSeries[4];
        setupLabels();
        setupGraph();
        setupSpinner();
        setupEditTexts();
        setupButtons();
        return v;
    }

    private void setupLabels() {
        // Set the appropriate label based off the shared preference value
        presenter.updateUnits();
    }

    private void setupSpinner() {
        presenter.updateTargets();
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setSelectedTarget(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private void setupEditTexts() {
        // Add on listeners so we know when to calculate
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.updateFields(mDoseEdit.getText().toString(),
                                       mOutputEdit.getText().toString(),
                                       mBrixEdit.getText().toString());
            }
        };
        mDoseEdit.addTextChangedListener(textWatcher);
        mOutputEdit.addTextChangedListener(textWatcher);
        mBrixEdit.addTextChangedListener(textWatcher);
    }

    private void setupButtons() {
        // Add the button listener
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.saveToDB(mDoseEdit.getText().toString(),
                        mOutputEdit.getText().toString(),
                        mBrixEdit.getText().toString(),
                        mSpinner.getSelectedItem().toString());
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
        Number[] xVals = {x};
        Number[] yVals = {y};
        if (mPoints != null) {
            mGraph.removeSeries(mPoints);
        }
        mPoints = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "Yield vs TDS");
        mGraph.addSeries(mPoints, mFormatter);
        mGraph.redraw();
    }

    @Override
    public void clearPoints() {
        mGraph.removeSeries(mPoints);
        mPoints = null;
        mGraph.redraw();
    }

    @Override
    public void updateDoseLabel(String doseLabel) {
        mDoseLabel.setText(doseLabel);
    }

    @Override
    public void updateOutputLabel(String outputLabel) {
        mOutputLabel.setText(outputLabel);
    }

    @Override
    public void updateDose(String dose) {
        mDoseEdit.setText(dose);
    }

    @Override
    public void updateOutput(String output) {
        mOutputEdit.setText(output);
    }

    @Override
    public void updateBrix(String brix) {
        mBrixEdit.setText(brix);
    }

    @Override
    public void updateTDS(String TDS) {
        mTdsEdit.setText(TDS);
    }

    @Override
    public void updateYield(String Yield) {
        mYieldEdit.setText(Yield);
    }

    @Override
    public void updateTargetSpinner(ArrayList<String> targetNames) {
        // Get the names of the profiles
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, targetNames);
        mSpinner.setAdapter(adapter);
    }

    @Override
    public void setSaveEnabled(boolean enabled) {
        mSaveButton.setEnabled(enabled);
    }
}
