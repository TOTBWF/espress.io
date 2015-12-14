package io.reed.dripr;

import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

import io.reed.dripr.Utils.Converters;
import io.reed.dripr.Utils.DatabaseHelper;
import io.reed.dripr.Utils.YieldTdsTarget;

/**
 * Solver for equations
 * @author Reed Mullanix
 */

public class SolverFragment extends Fragment {


    private EditText mInputEdit;
    private EditText mSolutionEdit;
    private TextView mInputLabel;
    private TextView mSolutionLabel;
    private Spinner mSolverSpinner;
    private Spinner mBeanSpinner;
    private CheckBox mIncludeBeans;
    private DatabaseHelper mDbHelper;
    private ArrayList<YieldTdsTarget> mTargets;
    private int selectedTargetIndex;

    private enum Solutions {
        DOSE, OUTPUT
    }

    public SolverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_solver, container, false);
        // Set fields
        mInputEdit = (EditText)v.findViewById(R.id.solver_edit_input);
        mSolutionEdit = (EditText)v.findViewById(R.id.solver_edit_solution);
        mInputLabel = (TextView)v.findViewById(R.id.solver_label_input);
        mSolutionLabel = (TextView)v.findViewById(R.id.solver_label_solution);
        mSolverSpinner = (Spinner)v.findViewById(R.id.solver_spinner);
        mBeanSpinner = (Spinner)v.findViewById(R.id.solver_bean_spinner);
        mIncludeBeans = (CheckBox)v.findViewById(R.id.solver_check_include);
        mDbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        mTargets = YieldTdsTarget.getStoredTargets(mDbHelper);
        setupSpinners();
        setupEditTexts();
        setupCheckbox();
        return v;
    }

    public void setupSpinners() {
        ArrayList<String> solverOptions = new ArrayList<String>();
        // Add options
        solverOptions.add("Dose");
        solverOptions.add("Output");
        ArrayAdapter<String> solverAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, solverOptions);
        mSolverSpinner.setAdapter(solverAdapter);
        mSolverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Solutions solution = Solutions.values()[position];
                switch (solution) {
                    case DOSE:
                        mInputLabel.setText(R.string.field_output);
                        mSolutionLabel.setText(R.string.field_dose);
                        break;
                    case OUTPUT:
                        mInputLabel.setText(R.string.field_dose);
                        mSolutionLabel.setText(R.string.field_output);
                        break;
                }
                // Zero out input fields
                mInputEdit.setText(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // TODO Read values from db
        ArrayList<String> beanOptions = new ArrayList<String>();
        for(YieldTdsTarget y: mTargets) {
            beanOptions.add(y.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, beanOptions);
        mBeanSpinner.setAdapter(adapter);
        mBeanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTargetIndex = position;
                mInputEdit.setText(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupEditTexts() {
        // Set up the watcher for the 3 fields associated with yield
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSolution();
            }
        };
        mInputEdit.addTextChangedListener(textWatcher);
    }

    private void setupCheckbox() {
        mIncludeBeans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSolution();
            }
        });
    }

    private void updateSolution() {
        double input = Converters.convertEditToDouble(mInputEdit);
        double tds = mTargets.get(selectedTargetIndex).getTdsTarget();
        double yield = mTargets.get(selectedTargetIndex).getYieldTarget();
        double absorption = mTargets.get(selectedTargetIndex).getBeanAbsorptionFactor();
        boolean includeBeans= mIncludeBeans.isChecked();
        // Get the solution from the enum using the spinner to index
        // Decoupling UI from logic is good!
        Solutions solution = Solutions.values()[mSolverSpinner.getSelectedItemPosition()];
        if (input != 0) {
            mSolutionEdit.setText(Double.toString(computeSolution(input, tds, yield, absorption, includeBeans, solution)));
        } else {
            mSolutionEdit.setText(null);
        }
    }

    private double computeSolution(double userInput, double tds, double yield, double absorption, boolean includeBeans, Solutions solution) {
        // Get the chosen variable to solve for
        switch (solution) {
            case DOSE:
                // Dose, input = output
                // Dose = TDS*Out/Yield
                if(includeBeans)
                    return userInput/(yield/tds + absorption);
                return tds*userInput/yield;
            case OUTPUT:
                // Output, input = dose
                // Output = Yield*Dose/TDS
                if(includeBeans)
                    return  yield*userInput/tds + absorption*userInput;
                return yield*userInput/tds;
        }
        // Just in case weird shit happens
        return -1;
    }
}
