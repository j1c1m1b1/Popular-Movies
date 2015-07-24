package com.udacity.jcmb.popularmovies.adapters;


import android.content.Context;
import android.database.Cursor;
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

    private Cursor cursor;

    private OnMovieChosenListener onMovieChosenListener;

    private ArrayList<Movie> movies;

    private boolean singleChoice;

    private int selectedIndex = -1;

    public void initialize(OnMovieChosenListener onMovieChosenListener, boolean singleChoice) {
        this.onMovieChosenListener = onMovieChosenListener;
        this.singleChoice = singleChoice;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        MovieView view = MovieView_.build(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Movie movie;
        if(cursor != null)
        {
            movie = getMovieFromCursorAt(position);
        }
        else
        {
            movie = movies.get(position);
        }

        boolean selected = selectedIndex == position;

        if(movie != null)
        {
            viewHolder.bind(movie, onMovieChosenListener, selected, position);
        }
    }

    @Override
    public int getItemCount() {
        if(cursor != null)
        {
            return cursor.getCount();
        }
        else if(movies != null)
        {
            return movies.size();
        }
        else
        {
            return  0;
        }
    }

    private Movie getMovieFromCursorAt(int position)
    {
        Movie movie = null;
        if(!cursor.isClosed() && cursor.moveToPosition(position))
        {
            int id, year, duration;
            double average;
            String name, imageFileName, backdropFileName, synopsis;

            id = cursor.getInt(0);
            name = cursor.getString(1);
            imageFileName = cursor.getString(2);
            backdropFileName = cursor.getString(3);
            average = cursor.getDouble(4);
            synopsis = cursor.getString(5);
            year = cursor.getInt(6);
            duration = cursor.getInt(7);

            movie = new Movie(id,name,imageFileName, backdropFileName, average);
            movie.setSynopsis(synopsis);
            movie.setYear(year);
            movie.setDuration(duration);
        }
        return movie;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void setCursor(Cursor cursor)
    {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public void setSelection(int position)
    {
        this.selectedIndex = position;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        MovieView view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = (MovieView)itemView;
        }

        public void bind(Movie movie, OnMovieChosenListener onMovieChosenListener,
                         boolean selected, int position)
        {
            view.bind(movie, onMovieChosenListener, selected, position, singleChoice);
        }
    }


}
