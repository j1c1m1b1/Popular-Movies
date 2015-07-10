package com.udacity.jcmb.popularmovies.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.interfaces.OnMovieChosenListener;
import com.udacity.jcmb.popularmovies.model.Movie;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsMenu;

@EActivity(R.layout.activity_home)
@OptionsMenu(R.menu.menu_home)
public class HomeActivity extends AppCompatActivity implements OnMovieChosenListener{


    @FragmentById
    Fragment fragmentMovies;

    @AfterViews
    void init()
    {
        fragmentMovies.setHasOptionsMenu(true);
    }

    @Override
    public void onMovieChosen(Movie movie, int x, int y, int color) {
        FrameLayout movieDetail = (FrameLayout)findViewById(R.id.movieDetail);
        if(movieDetail != null)
        {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
//            transaction.add(R.id.movieDetail, )
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
}
