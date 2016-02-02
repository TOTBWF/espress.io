package io.reed.dripr.Views;


import android.app.AlertDialog;
import android.content.DialogInterface;
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

import io.reed.dripr.Presenters.Interfaces.IProfilePresenter;
import io.reed.dripr.Presenters.ProfilePresenter;
import io.reed.dripr.R;
import io.reed.dripr.Views.Interfaces.IProfileView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements IProfileView {

    private Spinner mSpinner;
    private EditText mNameEdit;
    private EditText mTdsEdit;
    private EditText mYieldEdit;
    private EditText mTdsTolerancesEdit;
    private EditText mYieldTolerancesEdit;
    private EditText mAbsorptionEdit;
    private Button mSaveButton;
    private Button mDeleteButton;

    private static IProfilePresenter presenter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(presenter == null) {
            presenter = new ProfilePresenter();
        }
        presenter.onTakeView(this, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_profile, container, false);
        mSpinner = (Spinner)v.findViewById(R.id.profile_bean_spinner);
        mNameEdit = (EditText)v.findViewById(R.id.profile_edit_name);
        mTdsEdit = (EditText)v.findViewById(R.id.profile_edit_tds);
        mYieldEdit = (EditText)v.findViewById(R.id.profile_edit_yield);
        mTdsTolerancesEdit = (EditText)v.findViewById(R.id.profile_edit_tds_tolerances);
        mYieldTolerancesEdit = (EditText)v.findViewById(R.id.profile_edit_yield_tolerances);
        mAbsorptionEdit = (EditText)v.findViewById(R.id.profile_edit_absorption);
        mSaveButton = (Button)v.findViewById(R.id.profile_button_save);
        mDeleteButton = (Button)v.findViewById(R.id.profile_button_delete);
        setupSpinner();
        setupButtons();
        setupEditTexts();
        return v;
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
                presenter.updateFields(mNameEdit.getText().toString(),
                        mTdsEdit.getText().toString(),
                        mYieldEdit.getText().toString(),
                        mTdsTolerancesEdit.getText().toString(),
                        mYieldTolerancesEdit.getText().toString(),
                        mAbsorptionEdit.getText().toString());
            }
        };
        mNameEdit.addTextChangedListener(textWatcher);
        mTdsEdit.addTextChangedListener(textWatcher);
        mYieldEdit.addTextChangedListener(textWatcher);
        mTdsTolerancesEdit.addTextChangedListener(textWatcher);
        mYieldTolerancesEdit.addTextChangedListener(textWatcher);
        mAbsorptionEdit.addTextChangedListener(textWatcher);
    }

    private void setupButtons() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isUpdate = presenter.saveSelectedToDB();
                String message = (isUpdate ? "Updated Profile!" : "Saved Profile!");
                Snackbar snackbar = Snackbar
                        .make(getActivity().findViewById(R.id.main_coordinator_layout), message, Snackbar.LENGTH_SHORT);
                snackbar.show();
                // Refresh the spinner
                presenter.updateTargets();
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm Deletion?")
                        .setMessage("This will delete all associated data points as well!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                presenter.deleteSelectedFromDB();
                                Snackbar snackbar = Snackbar
                                        .make(getActivity().findViewById(R.id.main_coordinator_layout), "Deleted Profile!", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                // Refresh the spinner
                                presenter.updateTargets();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    @Override
    public void updateTargetSpinner(ArrayList<String> targetNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, targetNames);
        mSpinner.setAdapter(adapter);
    }

    @Override
    public void updateName(String name) {
        mNameEdit.setText(name);
    }

    @Override
    public void updateTDSTarget(String tds) {
        mTdsEdit.setText(tds);
    }

    @Override
    public void updateYieldTarget(String yield) {
        mYieldEdit.setText(yield);
    }

    @Override
    public void updateTDSTolerances(String tolerance) {
        mTdsTolerancesEdit.setText(tolerance);
    }

    @Override
    public void updateYieldTolerances(String tolerance) {
        mYieldTolerancesEdit.setText(tolerance);
    }

    @Override
    public void updateBeanAbsorption(String absorption) {
        mAbsorptionEdit.setText(absorption);
    }

    @Override
    public void updateSaveButtonText(String text) {
        mSaveButton.setText(text);
    }

    @Override
    public void enableSavebutton(boolean enabled) {
        mSaveButton.setEnabled(enabled);
    }

    @Override
    public void enableDeleteButton(boolean enabled) {
        mDeleteButton.setEnabled(enabled);
    }
}
