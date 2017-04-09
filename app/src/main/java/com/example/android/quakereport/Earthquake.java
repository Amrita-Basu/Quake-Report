package com.example.android.quakereport;

/**
 * Created by Amrita Basu on 07-03-2017.
 */

public class Earthquake {

    private double  magnitude;
    private String location;
    private long time;
    private String url;

    public Earthquake(double m, String l, long t, String url) {

        this.magnitude = m;
        this.location = l;
        this.time = t;
        this.url = url;


    }

    public double getMagnitude() {
        return magnitude;


    }

    public String getLocation() {
        return location;


    }

    public long getTime() {
        return time;


    }

    public String getUrl(){

        return url;
    }


}
