package com.udacity.jcmb.popularmovies.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.fragments.MovieDetailFragment;
import com.udacity.jcmb.popularmovies.fragments.MovieDetailFragment_;
import com.udacity.jcmb.popularmovies.fragments.MoviesFragment;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.prefs.MyPrefs_;
import com.udacity.jcmb.popularmovies.sync.PopularMoviesSyncAdapter;
import com.udacity.jcmb.popularmovies.utils.AnimationUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_home)
@OptionsMenu(R.menu.menu_home)
public class HomeActivity extends AppCompatActivity
{

    @Pref
    MyPrefs_ prefs;

    @ViewById
    LinearLayout rootLayout;

    @ViewById
    FrameLayout movieDetail;

    @FragmentById
    MoviesFragment fragmentMovies;

    private MenuItem menuShare;

    private FragmentManager manager;

    @AfterViews
    void init()
    {
        boolean singleChoice = movieDetail != null;
        fragmentMovies.setSingleChoice(singleChoice);
        fragmentMovies.setHasOptionsMenu(true);
        manager = getSupportFragmentManager();

        manager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            int previousCount;

            @Override
            public void onBackStackChanged() {
                int entryCount = manager.getBackStackEntryCount();
                if(entryCount == 0)
                {
                    refreshActionBar();
                    fragmentMovies.clearSelection();
                }

                else if(entryCount < previousCount)
                {
                    int position = Integer.parseInt(manager.getBackStackEntryAt(entryCount - 1).getName());
                    fragmentMovies.setSelection(position);
                }
                previousCount = entryCount;
            }
        });

        refreshActionBar();
        PopularMoviesSyncAdapter.initializeSyncAdapter(this);
    }

    public void onMovieChosen(Movie movie, int x, int y, int color, int position) {
        FrameLayout movieDetail = (FrameLayout)findViewById(R.id.movieDetail);
        if(movieDetail != null)
        {
            placeMovieDetailFragment(movie, color, position);
        }
        else
        {
            goToMovieDetail(movie, x, y, color);
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

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                getWindow().setStatusBarColor(getResources().getColor(R.color.orange_dark));
            }
        }
        if(menuShare != null)
        {
            menuShare.setVisible(false);
        }
    }

    public void placeMovieDetailFragment(Movie movie, int color, int position)
    {

        FragmentTransaction transaction = manager.beginTransaction();
        MovieDetailFragment movieDetailFragment = MovieDetailFragment_.builder()
                .average(movie.getAverage())
                .id(movie.getId())
                .backdropFileName(movie.getBackdropFileName())
                .imageFileName(movie.getImageFileName())
                .color(color)
                .position(position)
                .build();

        movieDetailFragment.setHasOptionsMenu(true);

        transaction.replace(R.id.movieDetail, movieDetailFragment);
        transaction.addToBackStack("" + position);
        transaction.commit();
    }

    public void goToMovieDetail(Movie movie, int x, int y, int color)
    {
        MovieDetailActivity_.intent(this).x(x).y(y)
                .average(movie.getAverage())
                .id(movie.getId())
                .imageFileName(movie.getImageFileName())
                .backdropFileName(movie.getBackdropFileName())
                .name(movie.getName())
                .color(color)
                .start();
    }

    public void createSnackBar(int resId) {
        Snackbar.make(rootLayout, resId, Snackbar.LENGTH_LONG).show();
    }

    public void createCircularReveal(int x, int y)
    {
        AnimationUtils.createCircularReveal(movieDetail, x, y, this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuShare = menu.findItem(R.id.share);
        return super.onPrepareOptionsMenu(menu);
    }
}
