package io.reed.dripr;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * The fragment for settings. All values to be added are found in /res/xml/preferences.xml
 * @author Reed Mullanix
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static final String MASS_UNIT = "mass_unit_preference";
        private static final String DUMP_TO_CSV = "dump_to_csv_preference";
        private static final String LOAD_FROM_CSV = "load_from_csv_preference";
        private static final String CLEAR_DB = "delete_database_preference";

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
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
            case MASS_UNIT:
                settingNotYetImplemented();
                break;
            default:
                settingNotYetImplemented();
                break;
        }
        Log.d("Settings", "Setting with key:" + key + " changed value");
    }

    private void setupPreferenceClickListeners() {
        findPreference(DUMP_TO_CSV).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                settingNotYetImplemented();
                return true;
            }
        });
        findPreference(LOAD_FROM_CSV).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                settingNotYetImplemented();
                return true;
            }
        });
        findPreference(CLEAR_DB).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                settingNotYetImplemented();
                return true;
            }
        });
    }

    private void settingNotYetImplemented() {
        Toast.makeText(getActivity(), "Setting Not Implemented!", Toast.LENGTH_SHORT).show();
        Log.d("Settings", "Setting Not Yet Implemented!");
    }
}
