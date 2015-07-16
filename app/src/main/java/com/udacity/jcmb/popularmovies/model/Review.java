package com.udacity.jcmb.popularmovies.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.jcmb.popularmovies.db.contracts.PopularMoviesContract;

/**
 * @author Julio Mendoza on 7/9/15.
 */
public class Review implements Parcelable{

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
    private int id;

    private String author;

    private String content;

    /**
     * Required by ORMLite
     */
    public Review() {
    }

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    protected Review(Parcel in) {
        id = in.readInt();
        author = in.readString();
        content = in.readString();
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
    }

    public ContentValues toValues(int movieId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_MOV_KEY, movieId);
        contentValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_AUTHOR, author);
        contentValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_CONTENT, content);
        return contentValues;
    }
}
