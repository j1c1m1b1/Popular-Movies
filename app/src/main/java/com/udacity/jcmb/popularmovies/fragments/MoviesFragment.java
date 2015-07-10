package com.udacity.jcmb.popularmovies.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.adapters.MoviesAdapter;
import com.udacity.jcmb.popularmovies.connection.ContentSolver;
import com.udacity.jcmb.popularmovies.connection.Requests;
import com.udacity.jcmb.popularmovies.interfaces.ConnectionEventsListener;
import com.udacity.jcmb.popularmovies.interfaces.OnMovieChosenListener;
import com.udacity.jcmb.popularmovies.model.Movie;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@EFragment(R.layout.fragment_movies)
public class MoviesFragment extends Fragment {

    private static final String MOVIES = "movies";

    @Bean
    MoviesAdapter adapter;

    @ViewById
    RecyclerView rvMovies;

    private ArrayList<Movie> movies;

    private OnMovieChosenListener onMovieChosenListener;

    @AfterViews
    void init()
    {
        GridLayoutManager manager = new GridLayoutManager(getActivity(),2,
                GridLayoutManager.VERTICAL, false);
        rvMovies.setLayoutManager(manager);
        rvMovies.setHasFixedSize(false);
        rvMovies.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnMovieChosenListener(onMovieChosenListener);
        rvMovies.setAdapter(adapter);
        if(movies == null)
        {
            getMovies(true);
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
        ConnectionEventsListener connectionEventsListener = new ConnectionEventsListener() {
            @Override
            public void onSuccess(JSONObject response) {
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

    @UiThread
    void refreshAdapter()
    {
        adapter.setMovies(movies);
    }

    @OptionsItem({R.id.action_popularity, R.id.action_ranking})
    void sort(MenuItem item)
    {
        boolean popularity;
        popularity = item.getItemId() != R.id.action_ranking;
        getMovies(popularity);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(movies != null && !movies.isEmpty())
        {
            outState.putParcelableArrayList(MOVIES, movies);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIES))
        {
            movies = savedInstanceState.getParcelableArrayList(MOVIES);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onMovieChosenListener = (OnMovieChosenListener)getActivity();
    }
}
