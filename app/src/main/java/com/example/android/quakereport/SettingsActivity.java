package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by AMRITA BASU on 08-04-2017.
 */
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class SettingsActivity extends AppCompatActivity {

    public static final String LOG_TAG = SettingsActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(LOG_TAG, "Activity created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }




    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.e(LOG_TAG, "Fragment created");
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);
            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagnitude);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);


        }



        // Defining what should happen when a preference value changes:
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                // we need to get the index of this value stored in our values array. The same index will correspond the appropriate label in our labels array and we simply set the summary of the Order By pref to this label
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue); // finding the index
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries(); // this gives us all the labels that we passed  in with our array of labels as entries in the entries attribute of our List Preference tag in settings_main.xml
                    listPreference.setSummary(labels[prefIndex]); // setting the summary to the corresponding label
                }

            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }


        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this); // setting the listener on this pref obj
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());// list of all the shared prefs for this context
            String preferenceString = preferences.getString(preference.getKey(), ""); // value stored for this preference object by identifying it with its key// passing the default value is optional
            onPreferenceChange(preference, preferenceString);
        }


    }


}
