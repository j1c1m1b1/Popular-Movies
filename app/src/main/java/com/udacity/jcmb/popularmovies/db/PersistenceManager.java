package com.udacity.jcmb.popularmovies.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.udacity.jcmb.popularmovies.model.Movie;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Julio Mendoza on 7/15/15.
 */
@EBean
public class PersistenceManager {

    @RootContext
    Context context;

    @OrmLiteDao(helper = DatabaseHelper.class)
    Dao<Movie, Integer> movieDao;

    public void saveMovie(Movie movie)
    {
        try {
            movieDao.createOrUpdate(movie);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeMovie(Movie movie)
    {
        try {
            movieDao.delete(movie);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Movie> getAllMovies()
    {
        ArrayList<Movie> favoriteMovies = null;
        try {
            List<Movie> list = movieDao.queryForAll();
            if(!list.isEmpty())
            {
                favoriteMovies = new ArrayList<>();
                favoriteMovies.addAll(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoriteMovies;
    }

    public boolean isFavorite(Movie movie)
    {
        boolean isFavorite = false;
        try {
            isFavorite = movieDao.idExists(Integer.valueOf(movie.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isFavorite;
    }

}
