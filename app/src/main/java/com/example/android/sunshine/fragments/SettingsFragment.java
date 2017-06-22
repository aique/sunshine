package com.example.android.sunshine.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.android.sunshine.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        // se muestra el summary para las preferencias de tipo ListPreference
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int numPreferences = preferenceScreen.getPreferenceCount();

        for(int i = 0 ; i < numPreferences ; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            String value = sharedPreferences.getString(preference.getKey(), "");
            setPreferenceSummary(preference, value);
        }
    }

    private void setPreferenceSummary(Preference preference, String value) {
        if(preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);

            if(prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if(preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if(preference != null) {
            String value = sharedPreferences.getString(preference.getKey(), "");
            setPreferenceSummary(preference, value);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // se registra el listener para actualizar el summary cuando cambia el valor de la preferencia
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // siempre que se registra el listener de preferencias, se ha de desregistrar en el onDestroy
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
