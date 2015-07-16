package com.udacity.jcmb.popularmovies.connection;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.udacity.jcmb.popularmovies.interfaces.ConnectionEventsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Julio Mendoza on 7/9/15.
 */
public class Requests {

    public static final String IMAGES_URL = "http://image.tmdb.org/t/p/w342";
    public static final String SMALL_IMAGES_URL = "http://image.tmdb.org/t/p/w185";
    private static final String API_KEY = "e651fa3187fcab2855a983e887bb50e1";
    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static final String DISCOVER_MOVIE = "discover/movie?sort_by=";
    private static final String GET_MOVIE = "movie/%d";
    private static final String TRAILERS = "/videos";
    private static final String REVIEWS = "/reviews";
    private static final String POPULARITY = "popularity.desc";
    private static final String RANKING = "vote_average.desc";
    private static final String VOTE_COUNT = "&vote_count.gte=200";
    private static final String DISCOVER_PATTERN = BASE_URL + DISCOVER_MOVIE + "%s&api_key=" +
            API_KEY;
    private static final String GET_MOVIE_PATTERN = BASE_URL + GET_MOVIE + "?api_key=" +
            API_KEY;
    private static final String GET_BASE_MOVIE_PATTERN = BASE_URL + GET_MOVIE;
    private static final String GET_TRAILERS_PATTERN = GET_BASE_MOVIE_PATTERN + TRAILERS + "?api_key=" +
            API_KEY;
    private static final String GET_REVIEWS_PATTERN = GET_BASE_MOVIE_PATTERN + REVIEWS + "?api_key=" +
            API_KEY;
    private static OkHttpClient client = new OkHttpClient();

    public static void sortByPopularity(ConnectionEventsListener connectionEventsListener)
    {
        String url = String.format(DISCOVER_PATTERN, POPULARITY);
        callApi(url, connectionEventsListener);
    }

    public static void sortByRanking(ConnectionEventsListener connectionEventsListener)
    {
        String url = String.format(DISCOVER_PATTERN + "%s", RANKING, VOTE_COUNT);
        callApi(url, connectionEventsListener);

    }

    public static void getMovie(int movieId, ConnectionEventsListener connectionEventsListener)
    {
        String url = String.format(GET_MOVIE_PATTERN, movieId);
        callApi(url, connectionEventsListener);
    }

    public static void getMovieTrailers(int movieId,
                                        ConnectionEventsListener connectionEventsListener)
    {
        String url = String.format(GET_TRAILERS_PATTERN, movieId);
        Log.d("url", url);
        callApi(url, connectionEventsListener);
    }

    public static void getMovieReviews(int movieId,
                                       ConnectionEventsListener connectionEventsListener)
    {
        String url = String.format(GET_REVIEWS_PATTERN, movieId);
        Log.d("url", url);
        callApi(url, connectionEventsListener);
    }

    private static void callApi(String url, final ConnectionEventsListener connectionEventsListener)
    {
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                connectionEventsListener.onFail();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseString = response.body().string();
                Log.d("Response", "" + responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    connectionEventsListener.onSuccess(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                    connectionEventsListener.onFail();
                }
            }
        });
    }
}
