package com.udacity.jcmb.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Julio Mendoza on 7/15/15.
 */
@DatabaseTable(tableName = "trailers")
public class Trailer implements Parcelable{

    public static final String MOVIE_ID = "movie_id";
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
    @DatabaseField(id = true, columnName = "uid")
    private String trailerId;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = MOVIE_ID)
    private Movie movie;

    /**
     * Required by ORMLite
     */
    public Trailer() {
    }

    public Trailer(String trailerId, Movie movie) {
        this.trailerId = trailerId;
        this.movie = movie;
    }

    protected Trailer(Parcel in) {
        trailerId = in.readString();
        movie = in.readParcelable(Movie.class.getClassLoader());
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
        dest.writeParcelable(movie, flags);
    }
}
