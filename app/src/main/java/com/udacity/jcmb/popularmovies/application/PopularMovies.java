package com.udacity.jcmb.popularmovies.application;

import android.app.Application;

import com.udacity.jcmb.popularmovies.db.PersistenceManager;
import com.udacity.jcmb.popularmovies.model.Movie;

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

    public void saveMovie(Movie movie)
    {
        persistenceManager.saveMovie(movie);
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

}
