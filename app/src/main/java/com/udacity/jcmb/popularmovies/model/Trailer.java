package com.udacity.jcmb.popularmovies.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.jcmb.popularmovies.db.contracts.PopularMoviesContract;

/**
 * @author Julio Mendoza on 7/15/15.
 */
public class Trailer implements Parcelable{

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    private String trailerId;

    /**
     * Required by ORMLite
     */
    public Trailer() {
    }

    public Trailer(String trailerId) {
        this.trailerId = trailerId;
    }

    protected Trailer(Parcel in) {
        trailerId = in.readString();
    }

    public String getTrailerId() {
        return trailerId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailerId);
    }

    public ContentValues toValues(int movieId)
    {
        ContentValues values = new ContentValues();
        values.put(PopularMoviesContract.TrailersEntry.COLUMN_TRAILER_ID, trailerId);
        values.put(PopularMoviesContract.TrailersEntry.COLUMN_MOV_KEY, movieId);
        return values;
    }
}
