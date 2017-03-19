package com.example.android.quakereport;

import android.support.v4.content.ContextCompat;

/**
 * Created by D3B3st on 2/28/2017.
 */
// klasa earthquake potrzebna do przekazywania danych do adaptera
public class Earthquake {
    double mMagnitude;
    private String mLocation;
    private Long mDate;
    String mUrl;

    public Earthquake(double magnitude, String location, Long date, String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mDate = date;
        mUrl = url;
}
    public double getMagnitude(){return mMagnitude;}
    public String getLocation(){return mLocation;}
    public Long getDate(){return mDate;}
    public String getUrl() {return mUrl;}

}
