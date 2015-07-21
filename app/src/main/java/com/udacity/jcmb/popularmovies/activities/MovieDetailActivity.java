package com.udacity.jcmb.popularmovies.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.application.PopularMovies;
import com.udacity.jcmb.popularmovies.fragments.MovieDetailFragment;
import com.udacity.jcmb.popularmovies.fragments.MovieDetailFragment_;
import com.udacity.jcmb.popularmovies.utils.AnimationUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@EActivity(R.layout.activity_movie_detail)
@OptionsMenu(R.menu.menu_detail)
public class MovieDetailActivity extends AppCompatActivity
{
    @App
    PopularMovies app;

    @ViewById
    FrameLayout movieDetail;

    @Extra
    int id;

    @Extra
    String name;

    @Extra
    String imageFileName;

    @Extra
    String backdropFileName;

    @Extra
    int x;

    @Extra
    int y;

    @Extra
    double average;

    @Extra
    int color;

    boolean noInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noInstance = savedInstanceState == null;
    }

    @AfterViews
    void init()
    {
        if(noInstance)
        {
            MovieDetailFragment fragment = MovieDetailFragment_.builder()
                    .id(id)
                    .imageFileName(imageFileName)
                    .backdropFileName(backdropFileName)
                    .average(average)
                    .color(color)
                    .build();

            fragment.setHasOptionsMenu(true);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.add(R.id.movieDetail, fragment);

            transaction.commit();
        }
    }

    public void createSnackBar(int stringId)
    {
        Snackbar.make(movieDetail, stringId, Snackbar.LENGTH_LONG).show();
    }

    public void createCircularReveal(int x, int y) {
        AnimationUtils.createCircularReveal(movieDetail, x, y, this);
    }
}
