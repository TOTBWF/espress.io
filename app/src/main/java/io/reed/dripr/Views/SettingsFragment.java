package io.reed.dripr.Views;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.util.Log;

import io.reed.dripr.Models.SettingsHelper;
import io.reed.dripr.Presenters.Interfaces.ISettingsPresenter;
import io.reed.dripr.R;
import io.reed.dripr.Presenters.SettingsPresenter;
import io.reed.dripr.Views.Interfaces.ISettingsView;


/**
 * The fragment for settings. All values to be added are found in /res/xml/preferences.xml
 * @author Reed Mullanix
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, ISettingsView {

    private static ISettingsPresenter presenter;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        if(presenter == null) {
            presenter = new SettingsPresenter(getActivity());
        }
        presenter.onTakeView(this, getActivity());
        // Setup on click listeners
        setupPreferenceClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case SettingsHelper.MASS_UNIT_KEY:
                break;
            default:
                presenter.settingNotYetImplemented();
                break;
        }
        Log.d("Settings", "Setting with key:" + key + " changed value");
    }

    private void setupPreferenceClickListeners() {
        findPreference(SettingsHelper.DUMP_TO_CSV_KEY).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                presenter.dumpDbToCsv();
                Snackbar snackbar = Snackbar
                        .make(getActivity().findViewById(R.id.main_coordinator_layout), "Database Dump Complete!", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return true;
            }
        });
        findPreference(SettingsHelper.LOAD_FROM_CSV_KEY).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                presenter.settingNotYetImplemented();
                return true;
            }
        });
        findPreference(SettingsHelper.CLEAR_DB_KEY).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm Deletion?")
                        .setMessage("This will delete all data, and cannot be undone!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                presenter.clearDb();
                                Snackbar snackbar = Snackbar
                                        .make(getActivity().findViewById(R.id.main_coordinator_layout), "Cleared Database!", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        });
    }

}
