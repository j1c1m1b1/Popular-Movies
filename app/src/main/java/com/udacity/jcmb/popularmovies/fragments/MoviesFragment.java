package com.udacity.jcmb.popularmovies.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.activities.HomeActivity;
import com.udacity.jcmb.popularmovies.adapters.MoviesAdapter;
import com.udacity.jcmb.popularmovies.application.PopularMovies;
import com.udacity.jcmb.popularmovies.connection.ContentSolver;
import com.udacity.jcmb.popularmovies.connection.Requests;
import com.udacity.jcmb.popularmovies.db.contracts.PopularMoviesContract;
import com.udacity.jcmb.popularmovies.interfaces.ConnectionEventsListener;
import com.udacity.jcmb.popularmovies.interfaces.OnMovieChosenListener;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.prefs.MyPrefs_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@EFragment(R.layout.fragment_movies)
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnMovieChosenListener {

    private static final String MOVIES = "movies";

    private static final String POSITION = "position";

    private static final int MOVIES_LOADER = 0;

    @App
    PopularMovies app;

    @Bean
    MoviesAdapter adapter;

    @Pref
    MyPrefs_ prefs;

    @ViewById
    RecyclerView rvMovies;

    @FragmentArg
    boolean singleChoice;
    int position = -1;
    private ArrayList<Movie> movies;
    private Cursor cursor;

    @AfterViews
    void init()
    {
        GridLayoutManager manager = new GridLayoutManager(getActivity(),2,
                GridLayoutManager.VERTICAL, false);
        rvMovies.setLayoutManager(manager);
        rvMovies.setHasFixedSize(false);
        rvMovies.setItemAnimator(new DefaultItemAnimator());
        adapter.initialize(this, singleChoice);
        rvMovies.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(movies == null)
        {
            if(prefs.fromFavorites().get())
            {
                getFavoriteMovies();
            }
            else if(prefs.fromRanking().get())
            {
                getMovies(false);
            }
            else
            {
                getMovies(true);
            }
        }
        else if(prefs.fromFavorites().get())
        {
            getFavoriteMovies();
        }
        else
        {
            refreshAdapter();
        }
    }

    /**
     * Requests the popular movies from the server.
     * @param popularity <b>True</b> if should sort by popularity, <b>False</b> on the contrary.
     */
    @Background
    void getMovies(boolean popularity)
    {
        getLoaderManager().destroyLoader(MOVIES_LOADER);
        ConnectionEventsListener connectionEventsListener = new ConnectionEventsListener() {
            @Override
            public void onSuccess(JSONObject response) {
                cursor = null;
                movies = ContentSolver.parseMoviesFromResponse(response);
                refreshAdapter();
            }

            @Override
            public void onFail() {

            }
        };
        if(popularity)
        {
            Requests.sortByPopularity(connectionEventsListener);
        }
        else
        {
            Requests.sortByRanking(connectionEventsListener);
        }
    }

    public void getFavoriteMovies()
    {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
    }

    @UiThread
    void refreshAdapter()
    {
        adapter.setMovies(movies);
        adapter.setCursor(cursor);
    }

    @OptionsItem({R.id.action_popularity, R.id.action_ranking, R.id.action_favorites})
    void sort(MenuItem item)
    {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        switch (item.getItemId())
        {
            case R.id.action_popularity:
                if(actionBar != null)
                {
                    actionBar.setTitle(R.string.popularity);
                }
                prefs.fromFavorites().remove();
                prefs.fromRanking().remove();
                prefs.fromPopular().put(true);
                getMovies(true);
                break;

            case R.id.action_ranking:
                if(actionBar != null)
                {
                    actionBar.setTitle(R.string.ranking);
                }
                prefs.fromFavorites().remove();
                prefs.fromRanking().put(true);
                prefs.fromPopular().remove();
                getMovies(false);
                break;

            case R.id.action_favorites:
                if(actionBar != null)
                {
                    actionBar.setTitle(R.string.favorites);
                }
                prefs.fromFavorites().put(true);
                prefs.fromRanking().remove();
                prefs.fromPopular().remove();
                getFavoriteMovies();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(movies != null && !movies.isEmpty())
        {
            outState.putParcelableArrayList(MOVIES, movies);
        }
        if(position != -1)
        {
            outState.putInt(POSITION, position);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIES))
        {
            position = savedInstanceState.getInt(POSITION);
            movies = savedInstanceState.getParcelableArrayList(MOVIES);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri moviesUri = PopularMoviesContract.MoviesEntry.CONTENT_URI;

        return new CursorLoader(getActivity(), moviesUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("Loader", "Loader task finished");
        cursor = data;
        if(movies != null)
        {
            movies.clear();
        }
        refreshAdapter();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursor = null;
    }

    @Override
    public void onMovieChosen(Movie movie, int x, int y, int color, int position)
    {
        HomeActivity activity = (HomeActivity) getActivity();
        if(singleChoice)
        {
            this.position = position;
            adapter.setSelection(position);
        }
        activity.onMovieChosen(movie, x, y, color);
    }
}
