package com.example.android.visualizerpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.pref_visualizer);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();

        for(int i=0;i<count;i++){
            Preference p = preferenceScreen.getPreference(i);
            if(!(p instanceof CheckBoxPreference)){

                String value = sharedPreferences.getString(p.getKey(),"");
                setPreferenceSummary(p,value);

            }
        }

    }

    private void setPreferenceSummary(Preference preference,String value){

        if(preference instanceof ListPreference){
            //for list preference, figure out the label of the selected value
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if(prefIndex>=0){
                //Set the summary to that label
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }


        }


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        //Figure out which preference was changed
        Preference preference = findPreference(key);
        if(preference!=null){
            //Updates the summary for the preference
            if(!(preference instanceof CheckBoxPreference)){
                //finding value through key
                String value = sharedPreferences.getString(preference.getKey(),"");
                //setting label through value
                setPreferenceSummary(preference,value);
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
    public void onDestroy(){
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
