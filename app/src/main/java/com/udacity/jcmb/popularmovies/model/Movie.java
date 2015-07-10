package com.udacity.jcmb.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Julio Mendoza on 7/9/15.
 */
public class Movie implements Parcelable
{
    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private String id;
    private String name;
    private String imageFileName;
    private String backdropFileName;

    //ADDITIONAL INFO
    private double average;
    private String synopsis;
    private int year;
    private int duration;

    public Movie(String id, String name, String imageFileName, String backdropFileName, double average) {
        this.id = id;
        this.name = name;
        this.imageFileName = imageFileName;
        this.backdropFileName = backdropFileName;
        this.average = average;
    }

    public Movie(Parcel parcel) {
        this.id = parcel.readString();
        this.name = parcel.readString();
        this.imageFileName = parcel.readString();
        this.backdropFileName = parcel.readString();
        this.average = parcel.readDouble();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public double getAverage() {
        return average;
    }

    public String getBackdropFileName() {
        return backdropFileName;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(imageFileName);
        parcel.writeString(backdropFileName);
        parcel.writeDouble(average);
    }
}
