package com.udacity.jcmb.popularmovies.application;

import android.app.Application;

import com.udacity.jcmb.popularmovies.db.PersistenceManager;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.model.Review;
import com.udacity.jcmb.popularmovies.model.Trailer;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

import java.util.ArrayList;

/**
 * @author Julio Mendoza on 7/15/15.
 */
@EApplication
public class PopularMovies extends Application {

    @Bean
    PersistenceManager persistenceManager;

    public void saveMovie(Movie movie, ArrayList<Trailer> trailers, ArrayList<Review> reviews)
    {
        persistenceManager.saveMovie(movie, trailers, reviews);
    }

    public void removeMovie(Movie movie)
    {
        persistenceManager.removeMovie(movie);
    }

    public ArrayList<Movie> getAllMovies()
    {
        return persistenceManager.getAllMovies();
    }

    public boolean isFavorite(Movie movie)
    {
        return persistenceManager.isFavorite(movie);
    }

    public boolean isFavorite(String id)
    {
        return persistenceManager.isFavorite(id);
    }

    public Movie getMovie(String id) {
        return persistenceManager.getMovie(id);
    }

    public ArrayList<Trailer> getTrailersOfMovie(Movie movie) {
        return persistenceManager.getTrailersOfMovie(movie);
    }

    public ArrayList<Review> getReviewsOfMovie(Movie movie)
    {
        return persistenceManager.getReviewsOfMovie(movie);
    }
}
