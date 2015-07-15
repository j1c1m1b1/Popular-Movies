package com.udacity.jcmb.popularmovies.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.model.Review;
import com.udacity.jcmb.popularmovies.model.Trailer;

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

    @OrmLiteDao(helper = DatabaseHelper.class)
    Dao<Review, Integer> reviewDao;

    @OrmLiteDao(helper = DatabaseHelper.class)
    Dao<Trailer, String> trailerDao;

    public void saveMovie(Movie movie, ArrayList<Trailer> trailers, ArrayList<Review> reviews)
    {
        try {
            movieDao.createOrUpdate(movie);
            saveTrailers(trailers);
            saveReviews(reviews);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeMovie(Movie movie)
    {
        try {
            deleteReviewsOfMovie(movie);
            deleteTrailersOfMovie(movie);
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

    public boolean isFavorite(String id) {
        boolean isFavorite = false;
        try {
            isFavorite = movieDao.idExists(Integer.parseInt(id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isFavorite;
    }

    public boolean isFavorite(Movie movie)
    {
        return isFavorite(movie.getId());
    }

    public Movie getMovie(String id) {

        try {
            return movieDao.queryForId(Integer.parseInt(id));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Review> getReviewsOfMovie(Movie movie)
    {
        ArrayList<Review> reviews = new ArrayList<>();
        QueryBuilder<Review, Integer> qb = reviewDao.queryBuilder();
        Where<Review, Integer> where = qb.where();
        try {
            where.eq(Review.MOVIE_ID, Integer.parseInt(movie.getId()));
            PreparedQuery<Review> preparedQuery = qb.prepare();
            List<Review> list = reviewDao.query(preparedQuery);
            if(!list.isEmpty())
            {
                reviews.addAll(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  reviews;
    }

    public void saveReviews(ArrayList<Review> reviews)
    {
        for(Review review : reviews)
        {
            try {
                reviewDao.createOrUpdate(review);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteReviewsOfMovie(Movie movie)
    {
        QueryBuilder<Review, Integer> qb = reviewDao.queryBuilder();
        Where<Review, Integer> where = qb.where();
        try {
            where.eq(Review.MOVIE_ID, Integer.parseInt(movie.getId()));
            PreparedQuery<Review> preparedQuery = qb.prepare();
            List<Review> reviews = reviewDao.query(preparedQuery);
            reviewDao.delete(reviews);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Trailer> getTrailersOfMovie(Movie movie)
    {
        ArrayList<Trailer> trailers = new ArrayList<>();
        QueryBuilder<Trailer, String> qb = trailerDao.queryBuilder();
        Where<Trailer, String> where = qb.where();
        try {
            where.eq(Trailer.MOVIE_ID, Integer.parseInt(movie.getId()));
            PreparedQuery<Trailer> preparedQuery = qb.prepare();
            List<Trailer> list = trailerDao.query(preparedQuery);
            if(!list.isEmpty())
            {
                trailers.addAll(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    public void saveTrailers(ArrayList<Trailer> trailers)
    {
        for(Trailer trailer: trailers)
        {
            try {
                trailerDao.createOrUpdate(trailer);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteTrailersOfMovie(Movie movie)
    {
        QueryBuilder<Trailer, String> qb = trailerDao.queryBuilder();
        Where<Trailer, String> where = qb.where();
        try {
            where.eq(Trailer.MOVIE_ID, Integer.parseInt(movie.getId()));
            PreparedQuery<Trailer> preparedQuery = qb.prepare();
            List<Trailer> trailers = trailerDao.query(preparedQuery);
            trailerDao.delete(trailers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
