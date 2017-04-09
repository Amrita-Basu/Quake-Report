package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Amrita Basu on 07-03-2017.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    public static final String LOCATION_SEPARATOR = " of ";

    public EarthquakeAdapter(Activity context, List<Earthquake> l) {
        super(context, 0, l);


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            v = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        }

        Earthquake e = getItem(position);


        double mag = e.getMagnitude();
        DecimalFormat df = new DecimalFormat("0.0");
        String magnitude = df.format(mag);


        TextView magTextView = (TextView) v.findViewById(R.id.mag);
        magTextView.setText(magnitude);

        GradientDrawable magnitudeCircle = (GradientDrawable) magTextView.getBackground();

        magnitudeCircle.setColor(changeMagCircleColour(e.getMagnitude()));

        String location = e.getLocation();

        String offsetLocation = null;
        String primaryLocation = null;

        if(location.contains(LOCATION_SEPARATOR)) {

            String[] parts = location.split(LOCATION_SEPARATOR);
            offsetLocation = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];


        }

        else{

            offsetLocation = getContext().getString(R.string.near_the);
            primaryLocation = location;

        }

        // setting the text of the offset location textview:
        TextView offsetLocTextView = (TextView) v.findViewById(R.id.location_offset);
        offsetLocTextView.setText(offsetLocation);

        //setting the text of the primary location textview:
        TextView primaryLocTextView = (TextView) v.findViewById(R.id.location_primary);
        primaryLocTextView.setText(primaryLocation);


        long timeInMs = e.getTime();
        // turning the time in millisecs into a Date obj:
        Date date = new Date(timeInMs);
        // Formatting into date:
        SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd, yyyy");
        String formattedDate = sdf1.format(date);
        //setting the date:
        TextView dateView = (TextView) v.findViewById(R.id.date);
        dateView.setText(formattedDate);
        //formatting into time:
        SimpleDateFormat sdf2 = new SimpleDateFormat("h:mm a ");
        String formattedTime = sdf2.format(date);
        // setting the time:
        TextView timeView = (TextView) v.findViewById(R.id.time);
        timeView.setText(formattedTime);


        return v;


    }

    private int changeMagCircleColour(Double mag){

        int circleColorResourceId = 0;
        int magnitude = (int)Math.floor(mag);

        switch(magnitude){

            case 0:
            case 1:
                circleColorResourceId = R.color.magnitude1;
                break;

            case 2:
                circleColorResourceId = R.color.magnitude2;
                break;
            case 3:
                circleColorResourceId = R.color.magnitude3;
                break;
            case 4:
                circleColorResourceId = R.color.magnitude4;
                break;
            case 5:
                circleColorResourceId = R.color.magnitude5;
                break;
            case 6:
                circleColorResourceId = R.color.magnitude6;
                break;
            case 7:
                circleColorResourceId = R.color.magnitude7;
                break;
            case 8:
                circleColorResourceId = R.color.magnitude8;
                break;
            case 9:
                circleColorResourceId = R.color.magnitude9;
                break;
            default:
                circleColorResourceId = R.color.magnitude10plus;
                break;

        }


return ContextCompat.getColor(getContext(), circleColorResourceId);

    }

}
