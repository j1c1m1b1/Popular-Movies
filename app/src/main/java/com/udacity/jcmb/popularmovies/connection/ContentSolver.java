package com.udacity.jcmb.popularmovies.connection;

import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.model.Review;
import com.udacity.jcmb.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Julio Mendoza on 7/9/15.
 */
public class ContentSolver {

    private static final String RESULTS = "results";
    private static final String ID = "id";
    private static final String NAME = "title";
    private static final String SYNOPSIS = "overview";
    private static final String AVERAGE = "vote_average";
    private static final String IMAGE_NAME = "poster_path";
    private static final String BACKDROP = "backdrop_path";
    private static final String DATE = "release_date";
    private static final String DURATION = "runtime";
    private static final String VIDEO_ID = "key";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";


    public static ArrayList<Movie> parseMoviesFromResponse(JSONObject response)
    {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONArray resultsArray = response.getJSONArray(RESULTS);
            JSONObject jsonMovie;
            Movie movie;
            String id, name, imageFileName, backdropFileName;
            double average;

            for(int i = 0; i < resultsArray.length(); i++)
            {
                jsonMovie = resultsArray.getJSONObject(i);
                id = jsonMovie.getString(ID);
                name = jsonMovie.getString(NAME);
                average = jsonMovie.getDouble(AVERAGE);
                imageFileName = jsonMovie.getString(IMAGE_NAME);
                backdropFileName = jsonMovie.getString(BACKDROP);

                movie = new Movie(id,name, imageFileName, backdropFileName, average);
                movies.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static Movie parseMovie(JSONObject response)
    {
        Movie movie = null;
        String id, name, imageFileName, backdropFileName, synopsis, yearString;
        int year, duration;
        double average;
        try {
            id = response.getString(ID);
            name = response.getString(NAME);
            average = response.getDouble(AVERAGE);
            imageFileName = response.getString(IMAGE_NAME);
            backdropFileName = response.getString(BACKDROP);
            synopsis = response.getString(SYNOPSIS);
            yearString = response.getString(DATE);
            yearString = yearString.substring(0, yearString.indexOf("-"));
            year = Integer.parseInt(yearString);
            duration = Integer.parseInt(response.getString(DURATION));
            movie = new Movie(id,name, imageFileName, backdropFileName, average);
            movie.setSynopsis(synopsis);
            movie.setYear(year);
            movie.setDuration(duration);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movie;
    }

    public static ArrayList<Trailer> parseTrailers(JSONObject response, Movie movie) {
        ArrayList<Trailer> trailers = new ArrayList<>();
        try {
            JSONArray trailersArray = response.getJSONArray(RESULTS);
            JSONObject jsonTrailer;
            String videoId;
            Trailer trailer;
            for(int i = 0; i < trailersArray.length(); i++)
            {
                jsonTrailer = trailersArray.getJSONObject(i);
                videoId = jsonTrailer.getString(VIDEO_ID);
                trailer = new Trailer(videoId, movie);
                trailers.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    public static ArrayList<Review> parseReviews(JSONObject response, Movie movie) {
        ArrayList<Review> reviews = new ArrayList<>();
        try {
            JSONArray jsonReviews = response.getJSONArray(RESULTS);
            JSONObject jsonReview;
            String author, content;
            Review review;

            for (int i = 0; i < jsonReviews.length(); i ++)
            {
                jsonReview = jsonReviews.getJSONObject(i);
                author = jsonReview.getString(AUTHOR);
                content = jsonReview.getString(CONTENT);
                review = new Review(author, content, movie);
                reviews.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
