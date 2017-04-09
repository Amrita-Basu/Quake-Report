/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import android.content.AsyncTaskLoader;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    public static final int EARTHQUAKE_LOADER_ID = 1;
    private static EarthquakeAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "onCreate()");
        setContentView(R.layout.earthquake_activity);
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.progress_indicator);
            pb.setVisibility(View.GONE);
            TextView emptyView = (TextView) findViewById(R.id.emptyView);
            emptyView.setText("No internet connection.");

        } else {
            Log.e(LOG_TAG, "Loader being initialized by the Loader Manager");
            getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        Log.e(LOG_TAG, "Loader being created afresh");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this); // all shared prefs in this context
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));//  fetching its value stored by identifying it with its key

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key), //at a time, a pref obj can store one value, that when entered by the user gets mapped to its key attribute// we are fetching the value stored(entered by user) for the preference object Order By and mapped to its key
                getString(R.string.settings_order_by_default)
        );
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString()); // constructs an instance of our custom Earthquake Loader and returns it, if a callback to this method happens via initLoader, in case the loader with the specified ID isnt running


    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

        Log.e(LOG_TAG, "Loading finished");
        ProgressBar pb = (ProgressBar) findViewById(R.id.progress_indicator);
        pb.setVisibility(View.GONE); // hide the loading indicator when loading completes to display the appropriate results

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        TextView emptyView = (TextView) findViewById(R.id.emptyView);
        earthquakeListView.setEmptyView(emptyView);
        emptyView.setText("No earthquakes found.");// if there is no data to display i.e an empty list(or in our case, we aren't even creating the adapter in case the earthquake data is null), the text should change to this message

        if (earthquakes != null) {

            // Create a new {@link ArrayAdapter} of earthquakes
            mAdapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);

            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(mAdapter);

            //setting a clicklistener on the ListView to listen for whenever the user clicks a list item, so that they can be taken to the earthquake details web page corresponding to the earthquake they click on
            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    Earthquake eq = mAdapter.getItem(position);

                    Uri uri = Uri.parse(eq.getUrl());
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);

                    startActivity(i);


                }
            });


        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        // we need to worry about this only when some data is returned, otherwise we wont even create an adapter and the instance variable for it will stay null
        if (mAdapter != null) {
            mAdapter.clear();
        } // clearing out our existing data
        // a callback to this method happens when the requesting Activity is about to get destroyed by setting the Loader reference to null by the LoaderManager (not our job)and making it eligible for GC (user pressing the Back button or  killing the app) and so the Loader will also be destroyed
        // so the data contained by the loader is no longer available and we need to make sure that we are no longer using it in our adapter

        Log.e(LOG_TAG, "Loader  reset");

    }



}
