package com.example.android.quakereport;

/**
 * Created by Amrita Basu on 13-03-2017.
 */

import org.json.JSONException;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.android.quakereport.R.id.mag;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    public static ArrayList<Earthquake> fetchEarthquakeData(String requestUrl) {

        ArrayList<Earthquake> list = null;
        try {
            URL url = createUrl(requestUrl);
            if (url == null) {
                return null;
            } else {
                String jsonResponse = null;
                jsonResponse = makeHttpRequest(url);

                list = extractEarthquakesListFromJSON(jsonResponse);
                Log.e(LOG_TAG, "data fetched");
                //Thread.sleep(3000);

            }


        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem fetching the earthquake data", e);
        }

        return list;
    }

    private static URL createUrl(String requestUrl) {
        URL url = null;

        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem forming the url", e);
        }


        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                Log.e(LOG_TAG, "Successful HTTP cycle. The http response code is: " + urlConnection.getResponseCode());
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Unsuccessful HTTP cycle. The http response code is: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the http request", e);

        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }


        return jsonResponse;
    }


    private static String readFromInputStream(InputStream is) throws IOException {

        StringBuilder sb = new StringBuilder();
        try {

            InputStreamReader r = new InputStreamReader(is, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(r);
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException while trying to read input from stream", e);
        }

        return sb.toString();
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */

    public static ArrayList<Earthquake> extractEarthquakesListFromJSON(String jsonResponse) {

        ArrayList<Earthquake> earthquakes = null;
        if (jsonResponse.length() == 0)
            return earthquakes;

        try {
            Log.e(LOG_TAG, "The response is: " + jsonResponse);
            earthquakes = new ArrayList<>();
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray features = response.getJSONArray("features");
            Earthquake eq;
            for (int i = 0; i < features.length(); i++) {
                JSONObject eqobj = features.getJSONObject(i);
                JSONObject properties = eqobj.getJSONObject("properties");
                // String mag = String.valueOf(properties.getDouble("mag"));
                double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                long time = properties.getLong("time");

                String url = properties.getString("url");

                eq = new Earthquake(mag, place, time, url);

                earthquakes.add(eq);

            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}