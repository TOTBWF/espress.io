package io.reed.dripr.Views;

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

import java.util.ArrayList;

import io.reed.dripr.Presenters.IProfilePresenter;
import io.reed.dripr.Presenters.ISolverPresenter;
import io.reed.dripr.Presenters.SolverPresenter;
import io.reed.dripr.R;
import io.reed.dripr.Models.DatabaseHelper;
import io.reed.dripr.Models.YieldTdsTarget;

/**
 * Solver for equations
 * @author Reed Mullanix
 */

public class SolverFragment extends Fragment implements ISolverView {

    private EditText mInputEdit;
    private EditText mSolutionEdit;
    private TextView mInputLabel;
    private TextView mSolutionLabel;
    private Spinner mSolverSpinner;
    private Spinner mBeanSpinner;
    private CheckBox mIncludeBeans;

    private static ISolverPresenter presenter;

    public SolverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(presenter == null) {
            presenter = new SolverPresenter();
        }
        presenter.onTakeView(this, getActivity());
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
                presenter.updateSolutionType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private void setupEditTexts() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.computeSolution(s.toString(), mIncludeBeans.isChecked());
            }
        };
        mInputEdit.addTextChangedListener(textWatcher);
    }

    private void setupCheckbox() {
        mIncludeBeans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.computeSolution(mInputEdit.getText().toString(), isChecked);
            }
        });
    }

    @Override
    public void updateTargetSpinner(ArrayList<String> targetNames) {
        // Get the names of the profiles
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, targetNames);
        mBeanSpinner.setAdapter(adapter);

    }
    @Override
    public void updateInput(String input) {
        mInputEdit.setText(input);
    }

    @Override
    public void updateSolution(String solution) {
        mSolutionEdit.setText(solution);
    }

    @Override
    public void updateInputLabel(String label) {
        mInputLabel.setText(label);
    }

    @Override
    public void updateSolutionLabel(String label) {
        mSolutionLabel.setText(label);
    }
}
