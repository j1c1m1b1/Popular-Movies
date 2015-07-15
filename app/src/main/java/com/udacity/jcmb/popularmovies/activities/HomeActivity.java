package com.udacity.jcmb.popularmovies.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.fragments.MovieDetailFragment;
import com.udacity.jcmb.popularmovies.fragments.MovieDetailFragment_;
import com.udacity.jcmb.popularmovies.fragments.MoviesFragment;
import com.udacity.jcmb.popularmovies.interfaces.OnFavoriteChangedListener;
import com.udacity.jcmb.popularmovies.interfaces.OnMovieChosenListener;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.prefs.MyPrefs_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_home)
@OptionsMenu(R.menu.menu_home)
public class HomeActivity extends AppCompatActivity implements OnMovieChosenListener,
        OnFavoriteChangedListener {

    @Pref
    MyPrefs_ prefs;

    @FragmentById
    MoviesFragment fragmentMovies;

    private MovieDetailFragment movieDetailFragment;

    @AfterViews
    void init()
    {
        fragmentMovies.setHasOptionsMenu(true);
        refreshActionBar();
    }

    @Override
    public void onMovieChosen(Movie movie, int x, int y, int color) {
        FrameLayout movieDetail = (FrameLayout)findViewById(R.id.movieDetail);
        if(movieDetail != null)
        {
            placeMovieDetailFragment(movie, color);
        }
        else
        {
            Log.d(this.getClass().getSimpleName(), "Movie Chosen");
            MovieDetailActivity_.intent(this).x(x).y(y)
                    .average(movie.getAverage())
                    .id(movie.getId())
                    .imageFileName(movie.getImageFileName())
                    .backdropFileName(movie.getBackdropFileName())
                    .name(movie.getName())
                    .color(color)
                    .start();
        }
    }

    @Override
    public void onFavoriteChanged() {
        if(prefs.fromFavorites().get())
        {
            Log.d(this.getClass().getSimpleName(), "Favorites Refreshed");
            fragmentMovies.getFavoriteMovies();
        }
    }

    private void refreshActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            String title = getString(R.string.popularity);
            actionBar.setSubtitle(R.string.app_name);
            if(prefs.fromFavorites().get())
            {
                title = getString(R.string.favorites);
            }
            else if(prefs.fromRanking().get())
            {
                title = getString(R.string.ranking);
            }
            actionBar.setTitle(title);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.orange)));
        }
    }

    private void removeMovieFragment()
    {
        if(movieDetailFragment != null)
        {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(movieDetailFragment);
            transaction.commit();

            movieDetailFragment = null;
        }
    }

    private void placeMovieDetailFragment(Movie movie, int color)
    {
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        movieDetailFragment = MovieDetailFragment_.builder()
                .average(movie.getAverage())
                .id(movie.getId())
                .backdropFileName(movie.getBackdropFileName())
                .imageFileName(movie.getImageFileName())
                .build();
        movieDetailFragment.setOnFavoriteChangedListener(this);
        transaction.replace(R.id.movieDetail, movieDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        removeMovieFragment();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if(movieDetailFragment != null && movieDetailFragment.isAdded())
        {
            refreshActionBar();
            removeMovieFragment();
        }
        else
        {
            super.onBackPressed();
        }
    }
}
