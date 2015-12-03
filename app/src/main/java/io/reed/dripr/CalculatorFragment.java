package io.reed.dripr;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.text.DecimalFormat;

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
        // Set up our formatter
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
                if(brix != 0) {
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
                } else {
                    mYieldEdit.setText(null);
                }
            }
        };
        mDoseEdit.addTextChangedListener(yieldWatcher);
        mOutputEdit.addTextChangedListener(yieldWatcher);
        mTdsEdit.addTextChangedListener(yieldWatcher);
        return v;
    }

    private double convertEditToDouble(EditText edit) {
        try {
            return Double.parseDouble(edit.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
