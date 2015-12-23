package io.reed.dripr;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
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

import java.util.ArrayList;

import io.reed.dripr.Utils.Converters;
import io.reed.dripr.Utils.DatabaseHelper;
import io.reed.dripr.Utils.YieldTdsTarget;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Spinner mProfileSpinner;
    private EditText mNameEdit;
    private EditText mTdsEdit;
    private EditText mYieldEdit;
    private EditText mTdsTolerancesEdit;
    private EditText mYieldTolerancesEdit;
    private EditText mAbsorptionEdit;
    private Button mSaveButton;
    private Button mDeleteButton;
    private DatabaseHelper mDbHelper;
    private ArrayList<YieldTdsTarget> mTargets;
    private boolean isUpdate;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_profile, container, false);
        mProfileSpinner = (Spinner)v.findViewById(R.id.profile_bean_spinner);
        mNameEdit = (EditText)v.findViewById(R.id.profile_edit_name);
        mTdsEdit = (EditText)v.findViewById(R.id.profile_edit_tds);
        mYieldEdit = (EditText)v.findViewById(R.id.profile_edit_yield);
        mTdsTolerancesEdit = (EditText)v.findViewById(R.id.profile_edit_tds_tolerances);
        mYieldTolerancesEdit = (EditText)v.findViewById(R.id.profile_edit_yield_tolerances);
        mAbsorptionEdit = (EditText)v.findViewById(R.id.profile_edit_absorption);
        mSaveButton = (Button)v.findViewById(R.id.profile_button_save);
        mDeleteButton = (Button)v.findViewById(R.id.profile_button_delete);
        mDbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        mTargets = YieldTdsTarget.getStoredTargets(mDbHelper);
        setupEditTexts();
        setupSpinner();
        setupButtons();
        return v;
    }

    private void setupSpinner() {
        final ArrayList<String> options = new ArrayList<>();
        // Add the option to create a new profile as the first entry
        options.add("Create a new profile...");
        for(YieldTdsTarget y: mTargets) {
            options.add(y.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, options);
        mProfileSpinner.setAdapter(adapter);
        mProfileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Setup for new profile creation
                    mNameEdit.setText(R.string.new_profile);
                    mTdsEdit.setText(Double.toString(YieldTdsTarget.DEFAULT_DRIP_TDS_TARGET));
                    mYieldEdit.setText(Double.toString(YieldTdsTarget.DEFAULT_DRIP_YIELD_TARGET));
                    mTdsTolerancesEdit.setText(Double.toString(YieldTdsTarget.DEFAULT_DRIP_TDS_TOLERANCES));
                    mYieldTolerancesEdit.setText(Double.toString(YieldTdsTarget.DEFAULT_DRIP_YIELD_TOLERANCES));
                    mAbsorptionEdit.setText(Double.toString(YieldTdsTarget.DEFAULT_DRIP_BEAN_ABSORPTION));
                    setButtonStatus("New Profile", null);
                } else {
                    // Decrement by one due to new profile button
                    YieldTdsTarget target = mTargets.get(position - 1);
                    mNameEdit.setText(target.getName());
                    mTdsEdit.setText(Double.toString(target.getTdsTarget()));
                    mYieldEdit.setText(Double.toString(target.getYieldTarget()));
                    mTdsTolerancesEdit.setText(Double.toString(target.getTdsTolerances()));
                    mYieldTolerancesEdit.setText(Double.toString(target.getYieldTolerances()));
                    mAbsorptionEdit.setText(Double.toString(target.getBeanAbsorptionFactor()));
                    setButtonStatus(target.getName(), target);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupEditTexts() {
        TextWatcher nameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                YieldTdsTarget t = (mProfileSpinner.getSelectedItemPosition() == 0 ? null : mTargets.get(mProfileSpinner.getSelectedItemPosition() - 1));
                setButtonStatus(s.toString(), t);
            }
        };
        mNameEdit.addTextChangedListener(nameTextWatcher);
        TextWatcher genericTextWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSaveButton.setEnabled(!s.toString().equals(""));
            }
        };
        mTdsEdit.addTextChangedListener(genericTextWatcher);
        mYieldEdit.addTextChangedListener(genericTextWatcher);
        mTdsTolerancesEdit.addTextChangedListener(genericTextWatcher);
        mYieldTolerancesEdit.addTextChangedListener(genericTextWatcher);
        mAbsorptionEdit.addTextChangedListener(genericTextWatcher);
    }

    private void setupButtons() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make a new yieldTdsTarget
                String name = mNameEdit.getText().toString();
                Double tds = Converters.convertEditToDouble(mTdsEdit);
                Double yield = Converters.convertEditToDouble(mYieldEdit);
                Double tdsTolerance = Converters.convertEditToDouble(mTdsTolerancesEdit);
                Double yieldTolerance = Converters.convertEditToDouble(mYieldTolerancesEdit);
                Double absorption = Converters.convertEditToDouble(mAbsorptionEdit);
                YieldTdsTarget target = new YieldTdsTarget(name, yield, yieldTolerance, tds, tdsTolerance, absorption);
                if(isUpdate) {
                    target.updateTargetInDatabase(mDbHelper);
                } else {
                    target.writeTargetToDatabase(mDbHelper);
                }
                String message = (isUpdate ? "Updated Profile!" : "Saved Profile!");
                Snackbar snackbar = Snackbar
                        .make(getActivity().findViewById(R.id.main_coordinator_layout), message, Snackbar.LENGTH_SHORT);
                snackbar.show();
                // Refresh the spinner
                mTargets = YieldTdsTarget.getStoredTargets(mDbHelper);
                setupSpinner();
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Title")
                        .setMessage("Do you really want to whatever?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                mTargets.get(mProfileSpinner.getSelectedItemPosition() - 1).deleteTargetFromDatabase(mDbHelper);
                                Snackbar snackbar = Snackbar
                                        .make(getActivity().findViewById(R.id.main_coordinator_layout), "Deleted Profile!", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                // Refresh the spinner
                                mTargets = YieldTdsTarget.getStoredTargets(mDbHelper);
                                setupSpinner();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void setButtonStatus(String name, YieldTdsTarget target) {
        // Make sure that the name is valid and unique
        // Is this the best way to do this? No
        // Does it matter? Probably not
        isUpdate = false;
        boolean bEnabled = !name.equals("");
        int buttonName = R.string.button_save;
        for(YieldTdsTarget y: mTargets) {
            if(y.getName().equals(name)) {
                // First check if the
                buttonName = R.string.button_update;
                isUpdate = true;
            }
        }
        mSaveButton.setText(buttonName);
        mSaveButton.setEnabled(bEnabled);
        // Check to make sure we don't delete defaults
        if(target != null) {
            bEnabled = !target.getName().equals("Default Drip");
            bEnabled &= !target.getName().equals("Default Espresso");
        } else {
            bEnabled = false;
        }
        mDeleteButton.setEnabled(bEnabled);
    }

}
