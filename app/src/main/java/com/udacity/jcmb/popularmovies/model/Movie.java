package com.udacity.jcmb.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@DatabaseTable(tableName = "favorite_movies")
public class Movie implements Parcelable
{
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    @DatabaseField(id = true, columnName = "uid")
    private String id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String imageFileName;
    @DatabaseField
    private String backdropFileName;
    @DatabaseField
    private double average;
    //ADDITIONAL INFO
    @DatabaseField
    private String synopsis;
    @DatabaseField
    private int year;
    @DatabaseField
    private int duration;

    public Movie(String id, String name, String imageFileName, String backdropFileName, double average) {
        this.id = id;
        this.name = name;
        this.imageFileName = imageFileName;
        this.backdropFileName = backdropFileName;
        this.average = average;
    }

    public Movie(Parcel parcel) {

        id = parcel.readString();
        name = parcel.readString();
        imageFileName = parcel.readString();
        backdropFileName = parcel.readString();
        average = parcel.readDouble();
        synopsis = parcel.readString();
        year = parcel.readInt();
        duration = parcel.readInt();
    }

    /**
     * Required by OrmLite
     */
    public Movie()
    {

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
        parcel.writeString(synopsis);
        parcel.writeInt(year);
        parcel.writeInt(duration);
    }
}
