package com.udacity.jcmb.popularmovies.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.jcmb.popularmovies.interfaces.OnMovieChosenListener;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.views.MovieView;
import com.udacity.jcmb.popularmovies.views.MovieView_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@EBean
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    @RootContext
    Context context;

    private ArrayList<Movie> movies;

    private OnMovieChosenListener onMovieChosenListener;

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void setOnMovieChosenListener(OnMovieChosenListener onMovieChosenListener) {
        this.onMovieChosenListener = onMovieChosenListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        MovieView view = MovieView_.build(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Movie movie = movies.get(position);
        viewHolder.bind(movie, onMovieChosenListener);
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        MovieView view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = (MovieView)itemView;
        }

        public void bind(Movie movie, OnMovieChosenListener onMovieChosenListener)
        {
            view.bind(movie, onMovieChosenListener);
        }
    }


}
