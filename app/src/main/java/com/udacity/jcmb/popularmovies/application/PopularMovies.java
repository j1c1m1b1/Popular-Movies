package com.udacity.jcmb.popularmovies.application;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.udacity.jcmb.popularmovies.db.contracts.PopularMoviesContract;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.model.Review;
import com.udacity.jcmb.popularmovies.model.Trailer;

import org.androidannotations.annotations.EApplication;

import java.util.ArrayList;

/**
 * @author Julio Mendoza on 7/15/15.
 */
@EApplication
public class PopularMovies extends Application
{

    public void saveMovie(Movie movie, ArrayList<Trailer> trailers, ArrayList<Review> reviews) {

        ContentResolver contentResolver = getContentResolver();

        Uri uri = PopularMoviesContract.MoviesEntry.CONTENT_URI.buildUpon()
                .appendPath("" + movie.getId())
                .build();
        Cursor cursor = contentResolver.query(uri,null, null, null, null);

        ContentValues values = new ContentValues();
        values.put(PopularMoviesContract.MoviesEntry._ID, (long)movie.getId());
        values.put(PopularMoviesContract.MoviesEntry.COLUMN_NAME, movie.getName());
        values.put(PopularMoviesContract.MoviesEntry.COLUMN_IMAGE_FILE_NAME,
                movie.getImageFileName());
        values.put(PopularMoviesContract.MoviesEntry.COLUMN_BACKDROP_FILE_NAME,
                movie.getBackdropFileName());
        values.put(PopularMoviesContract.MoviesEntry.COLUMN_AVERAGE, movie.getAverage());
        values.put(PopularMoviesContract.MoviesEntry.COLUMN_SYNOPSIS, movie.getSynopsis());
        values.put(PopularMoviesContract.MoviesEntry.COLUMN_YEAR, movie.getYear());
        values.put(PopularMoviesContract.MoviesEntry.COLUMN_DURATION, movie.getDuration());

        if(cursor.moveToFirst())
        {
            cursor.close();
            contentResolver.update(PopularMoviesContract.MoviesEntry.CONTENT_URI,
                    values,
                    PopularMoviesContract.MoviesEntry._ID + " = ?", new String[]{"" + movie.getId()});
        }
        else
        {
            contentResolver.insert(PopularMoviesContract.MoviesEntry.CONTENT_URI,
                    values);

            ContentValues[] valuesArray = new ContentValues[trailers.size()];
            Trailer trailer;
            for(int i = 0; i < valuesArray.length; i ++)
            {
                trailer = trailers.get(i);
                values = trailer.toValues(movie.getId());
                valuesArray[i] = values;
            }

            contentResolver.bulkInsert(PopularMoviesContract.TrailersEntry.CONTENT_URI, valuesArray);

            valuesArray = new ContentValues[reviews.size()];
            Review review;
            for(int i = 0; i < valuesArray.length; i++)
            {
                review = reviews.get(i);
                values = review.toValues(movie.getId());
                valuesArray[i] = values;
            }

            contentResolver.bulkInsert(PopularMoviesContract.ReviewsEntry.CONTENT_URI, valuesArray);
        }
    }

    public void removeMovie(Movie movie) {
        ContentResolver contentResolver = getContentResolver();

        contentResolver.delete(PopularMoviesContract.MoviesEntry.CONTENT_URI,
                PopularMoviesContract.MoviesEntry._ID + " = ?",
                new String[]{"" + movie.getId()});
    }

    public boolean isFavorite(int id) {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = PopularMoviesContract.MoviesEntry.CONTENT_URI
                .buildUpon()
                .appendPath("" + id)
                .build();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        boolean isFavorite = cursor.moveToFirst();
        cursor.close();
        return isFavorite;
    }

    public Movie getMovie(int movieId) {
        Movie movie = null;
        ContentResolver contentResolver = getContentResolver();

        Uri uri = PopularMoviesContract.MoviesEntry.CONTENT_URI.buildUpon()
                .appendPath("" + movieId)
                .build();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if(cursor.moveToFirst())
        {
            int id = (int)cursor.getLong(0);
            String name = cursor.getString(1);
            String imageFileName = cursor.getString(2);
            String backdropFileName = cursor.getString(3);
            double average = cursor.getDouble(4);
            String synopsis = cursor.getString(5);
            int year = cursor.getInt(6);
            int duration = cursor.getInt(7);
            movie = new Movie(id, name, imageFileName, backdropFileName, average);
            movie.setSynopsis(synopsis);
            movie.setYear(year);
            movie.setDuration(duration);
            cursor.close();
        }
        return movie;
    }

    public ArrayList<Trailer> getTrailersOfMovie(Movie movie)
    {
        ArrayList<Trailer> trailers = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = PopularMoviesContract.TrailersEntry.CONTENT_URI
                .buildUpon()
                .appendQueryParameter(PopularMoviesContract.TrailersEntry.COLUMN_MOV_KEY, "" + movie.getId())
                .build();

        String[] projection = new String[]{PopularMoviesContract.TrailersEntry.COLUMN_TRAILER_ID};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if(cursor.moveToFirst())
        {
            Trailer trailer;
            String trailerId;
            do
            {
                trailerId = cursor.getString(0);
                trailer = new Trailer(trailerId);
                trailers.add(trailer);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return trailers;
    }

    public ArrayList<Review> getReviewsOfMovie(Movie movie)
    {
        ArrayList<Review> reviews = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = PopularMoviesContract.ReviewsEntry.CONTENT_URI
                .buildUpon()
                .appendQueryParameter(PopularMoviesContract.ReviewsEntry.COLUMN_MOV_KEY, "" + movie.getId())
                .build();

        String[] projection = new String[]{PopularMoviesContract.ReviewsEntry.COLUMN_AUTHOR,
        PopularMoviesContract.ReviewsEntry.COLUMN_CONTENT};

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if(cursor.moveToFirst())
        {
            String author, content;
            Review review;
            do {
                author = cursor.getString(0);
                content = cursor.getString(1);
                review = new Review(author, content);
                reviews.add(review);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return reviews;
    }
}
