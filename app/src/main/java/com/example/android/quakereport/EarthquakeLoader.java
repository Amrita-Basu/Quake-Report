package com.example.android.quakereport;

/**
 * Created by AMRITA BASU on 07-04-2017.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private static final String LOG_TAG = EarthquakeLoader.class.getSimpleName();
    private String mUrl;

    public EarthquakeLoader(Context context, String url){
              super(context);
              mUrl = url;

    }


    @Override
    protected void onStartLoading() {
        Log.e(LOG_TAG , "onStartLoading" );
         forceLoad(); // initLoad() calls onStartLoading automatically, good practice to call forceLoad here rather than with initLoad
                      // to start the loading task
    }


    @Override
    public List<Earthquake> loadInBackground() {
        Log.e(LOG_TAG , "loadInBackground " );
           if(mUrl == null)
               return null;


        ArrayList<Earthquake> data = QueryUtils.fetchEarthquakeData(mUrl);
        return data;



    }
}
