package io.reed.dripr;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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


import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import io.reed.dripr.Utils.CoffeeDatabaseContract;
import io.reed.dripr.Utils.Converters;
import io.reed.dripr.Utils.DatabaseHelper;
import io.reed.dripr.Utils.YieldTdsTarget;

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
    private Button mSaveButton;
    private Spinner mSpinner;
    private SimpleXYSeries[] mTargetBounds;
    private SimpleXYSeries mTargetLine;
    private DatabaseHelper mDbHelper;
    private ArrayList<YieldTdsTarget> mTargets;
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
        mSaveButton = (Button)v.findViewById(R.id.button_calc_save);
        mSpinner = (Spinner)v.findViewById(R.id.bean_spinner);
        // Instantiate the database
        mDbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        mTargets = YieldTdsTarget.getStoredTargets(mDbHelper);
        mTargetBounds = new SimpleXYSeries[4];
        setupGraph();
        setupSpinner();
        setupEditTexts();
        setupButtons();
        return v;
    }

    private void setupSpinner() {
        // Get the names of the profiles
        ArrayList<String> options = new ArrayList<>();
        for(YieldTdsTarget y: mTargets) {
           options.add(y.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, options);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateGraphBounds(position);
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
        mBrixEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double brix = Converters.convertEditToDouble(mBrixEdit);
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
                double tds = Converters.convertEditToDouble(mTdsEdit);
                double output = Converters.convertEditToDouble(mOutputEdit);
                double dose = Converters.convertEditToDouble(mDoseEdit);
                if (tds != 0 && output != 0 && dose != 0) {
                    // Set the text of the yield box
                    mYieldEdit.setText(format.format(tds * output / dose));
                    // Enable the save button
                    mSaveButton.setEnabled(true);
                    // Add a point to the graph
                    Number[] xVals = {tds * output / dose};
                    Number[] yVals = {tds};
                    if (mPoints != null) {
                        mGraph.removeSeries(mPoints);
                    }
                    mPoints = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "Yield vs TDS");
                    mGraph.addSeries(mPoints, mFormatter);
                    updateGraphBounds(mSpinner.getSelectedItemPosition());
                    mGraph.redraw();
                } else {
                    mYieldEdit.setText(null);
                    mSaveButton.setEnabled(false);
                    mGraph.removeSeries(mPoints);
                    mPoints = null;
                    mGraph.redraw();
                }
            }
        };
        mDoseEdit.addTextChangedListener(yieldWatcher);
        mOutputEdit.addTextChangedListener(yieldWatcher);
        mTdsEdit.addTextChangedListener(yieldWatcher);
    }

    private void setupButtons() {
        // Add the button listener
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store to the db
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(CoffeeDatabaseContract.CoffeeEntry.COLUMN_INPUT, Converters.convertEditToDouble(mDoseEdit));
                values.put(CoffeeDatabaseContract.CoffeeEntry.COLUMN_OUTPUT, Converters.convertEditToDouble(mOutputEdit));
                values.put(CoffeeDatabaseContract.CoffeeEntry.COLUMN_TDS, Converters.convertEditToDouble(mTdsEdit));
                values.put(CoffeeDatabaseContract.CoffeeEntry.COLUMN_BEANS, (String) mSpinner.getSelectedItem());
                db.insert(CoffeeDatabaseContract.CoffeeEntry.TABLE_COFFEE, CoffeeDatabaseContract.CoffeeEntry.COLUMN_NAME_NULLABLE, values);
                // Clear input fields after saving
                mSaveButton.setEnabled(false);
                mDoseEdit.setText(null);
                mOutputEdit.setText(null);
                mBrixEdit.setText(null);
                mTdsEdit.setText(null);
                mYieldEdit.setText(null);
                mSpinner.setSelection(0);
                mGraph.removeSeries(mPoints);
                mPoints = null;
                mGraph.redraw();
            }
        });
    }

    private void updateGraphBounds(int position) {
        mTargets.get(position).updateGraphBounds(getActivity(), mGraph, mTargetBounds);
        mGraph.removeSeries(mTargetLine);
        mTargetLine = mTargets.get(position).drawFormulaLine(getActivity(), mGraph);
    }
}
