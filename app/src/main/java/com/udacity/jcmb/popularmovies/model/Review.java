package com.udacity.jcmb.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@DatabaseTable(tableName = "reviews")
public class Review implements Parcelable{

    public static final String MOVIE_ID = "movie_id";
    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String author;
    @DatabaseField
    private String content;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = MOVIE_ID)
    private Movie movie;

    /**
     * Required by ORMLite
     */
    public Review() {
    }

    public Review(String author, String content, Movie movie) {
        this.author = author;
        this.content = content;
        this.movie = movie;
    }

    protected Review(Parcel in) {
        id = in.readInt();
        author = in.readString();
        content = in.readString();
        movie = in.readParcelable(Movie.class.getClassLoader());
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeParcelable(movie, flags);
    }
}
