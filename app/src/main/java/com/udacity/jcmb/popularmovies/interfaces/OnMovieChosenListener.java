package com.udacity.jcmb.popularmovies.interfaces;

import com.udacity.jcmb.popularmovies.model.Movie;

/**
 * @author Julio Mendoza on 7/9/15.
 */
public interface OnMovieChosenListener {

    void onMovieChosen(Movie movie, int x, int y, int color);
}
