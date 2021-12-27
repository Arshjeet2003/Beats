package com.example.android.visualizerpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.pref_visualizer);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {

                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);

            }
        }
        Preference preference = findPreference(getString(R.string.size_key));
        preference.setOnPreferenceChangeListener(this);

    }

    private void setPreferenceSummary(Preference preference, String value) {

        if (preference instanceof ListPreference) {
            //for list preference, figure out the label of the selected value
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                //Set the summary to that label
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }

        } else if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        //Figure out which preference was changed
        Preference preference = findPreference(key);
        if (preference != null) {
            //Updates the summary for the preference
            if (!(preference instanceof CheckBoxPreference)) {
                //finding value through key
                String value = sharedPreferences.getString(preference.getKey(), "");
                //setting label through value
                setPreferenceSummary(preference, value);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast error = Toast.makeText(getContext(), "Please enter a value between 0.1 and 3", Toast.LENGTH_SHORT);
        String sizeKey = getString(R.string.size_key);

        if (preference.getKey().equals(sizeKey)) {
            String stringSize = (String) newValue;

            try {
                float size = Float.parseFloat(stringSize);
                if (size <= 0 || size > 3) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {

                //if whatever user has entered cannot be parsed to integer
                error.show();
                return false;
            }
        }
        return true;
    }
}
