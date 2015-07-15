package com.udacity.jcmb.popularmovies.activities;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.application.PopularMovies;
import com.udacity.jcmb.popularmovies.connection.ContentSolver;
import com.udacity.jcmb.popularmovies.connection.Requests;
import com.udacity.jcmb.popularmovies.interfaces.ConnectionEventsListener;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.model.Review;
import com.udacity.jcmb.popularmovies.utils.AnimationUtils;
import com.udacity.jcmb.popularmovies.utils.BlurUtils;
import com.udacity.jcmb.popularmovies.views.ReviewView;
import com.udacity.jcmb.popularmovies.views.ReviewView_;
import com.udacity.jcmb.popularmovies.views.TrailerView;
import com.udacity.jcmb.popularmovies.views.TrailerView_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@EActivity(R.layout.activity_movie_detail)
public class MovieDetailActivity extends AppCompatActivity
{

    @App
    PopularMovies app;

    @ViewById
    CoordinatorLayout coordinator;

    @ViewById
    ImageView ivMovieBackground;

    @ViewById
    CircleImageView ivMovie;

    @ViewById
    CollapsingToolbarLayout collapsingToolbarLayout;

    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView tvYear;

    @ViewById
    TextView tvDuration;

    @ViewById
    TextView tvSynopsis;

    @ViewById
    TextView tvAverage;

    @ViewById
    LinearLayout layoutTrailers;

    @ViewById
    LinearLayout layoutReviews;

    @ViewById
    AppCompatCheckBox chkFavorite;

    @Extra
    String id;

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

    private Movie movie;

    private ArrayList<String> trailers;

    private ArrayList<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            finish();
        }
    }

    @AfterViews
    void init()
    {
        trailers = new ArrayList<>();
        reviews = new ArrayList<>();
        getMovieInfo();
        setSupportActionBar(toolbar);
        toolbar.setTitle(name);
        collapsingToolbarLayout.setContentScrimColor(color);
        Glide.with(this).load(Requests.IMAGES_URL + imageFileName).centerCrop().into(ivMovie);
        ivMovieBackground.setAlpha(0.8f);
        blurImage();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        }
        collapsingToolbarLayout.setTitle(toolbar.getTitle());
        tvAverage.setText(average + "/10");
        AnimationUtils.createCircularReveal(coordinator, x, y, this);
    }

    @CheckedChange(R.id.chkFavorite)
    void onCheckedChanged(@SuppressWarnings("UnusedParameters") CompoundButton btn,
                          boolean isChecked)
    {
        if(isChecked)
        {
            chkFavorite.setText(R.string.is_favorite);
            Snackbar.make(coordinator, R.string.movie_is_favorite, Snackbar.LENGTH_LONG).show();
            saveMovie();
        }
        else
        {
            chkFavorite.setText(R.string.save_favorite);
            Snackbar.make(coordinator, R.string.removed_favorite, Snackbar.LENGTH_LONG).show();
            removeMovie();
        }
    }

    @Background
    void saveMovie()
    {
        app.saveMovie(movie);
    }

    @Background
    void removeMovie()
    {
        app.removeMovie(movie);
    }

    @Background
    void isMovieFavorite()
    {
        boolean isFavorite = app.isFavorite(movie);
        refreshCheckView(isFavorite);
    }

    @UiThread
    void refreshCheckView(boolean isFavorite)
    {
        chkFavorite.setChecked(isFavorite);
        if(isFavorite)
        {
            chkFavorite.setText(R.string.is_favorite);
        }
    }

    @Background
    void blurImage()
    {
        Bitmap bitmap;
        try {
            bitmap = Glide.with(this).load(Requests.IMAGES_URL + backdropFileName)
                    .asBitmap().into(640, 480).get();
            Bitmap blurred = BlurUtils.fastblur(bitmap, 5);
            applyBlurred(blurred);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @UiThread
    void applyBlurred(Bitmap bitmap)
    {
        ivMovieBackground.setImageBitmap(bitmap);
    }

    private void getMovieInfo()
    {
        getMovie();
        getTrailers();
        getReviews();
    }

    @Background
    void getMovie()
    {
        ConnectionEventsListener connectionEventsListener = new ConnectionEventsListener() {
            @Override
            public void onSuccess(JSONObject response) {
                movie = ContentSolver.parseMovie(response);
                refreshMovieInfo();
            }

            @Override
            public void onFail() {

            }
        };

        Requests.getMovie(id, connectionEventsListener);
    }

    @Background
    void getTrailers()
    {
        ConnectionEventsListener connectionEventsListener = new ConnectionEventsListener() {
            @Override
            public void onSuccess(JSONObject response) {
                trailers = ContentSolver.parseTrailers(response);
                refreshTrailers();
            }

            @Override
            public void onFail() {

            }
        };

        Requests.getMovieTrailers(id, connectionEventsListener);
    }

    @Background
    void getReviews()
    {
        ConnectionEventsListener connectionEventsListener = new ConnectionEventsListener() {
            @Override
            public void onSuccess(JSONObject response) {
                reviews = ContentSolver.parseReviews(response);
                refreshReviews();
            }

            @Override
            public void onFail() {

            }
        };
        Requests.getMovieReviews(id, connectionEventsListener);
    }

    @UiThread
    void refreshMovieInfo()
    {
        tvYear.setText("" + movie.getYear());
        tvDuration.setText("" + movie.getDuration());
        tvSynopsis.setText(movie.getSynopsis());
        isMovieFavorite();
    }

    @UiThread
    void refreshTrailers()
    {
        TrailerView trailerView;
        String pattern = "Trailer %d";
        String title, videoId;
        for(int i = 0; i < trailers.size(); i++)
        {
            title = String.format(pattern, i + 1);
            videoId = trailers.get(i);
            trailerView = TrailerView_.build(this);
            trailerView.bind(videoId, title);
            layoutTrailers.addView(trailerView);
        }

    }

    @UiThread
    void refreshReviews()
    {
        ReviewView reviewView;
        Review review;
        for(int i = 0; i < reviews.size(); i ++)
        {
            review = reviews.get(i);
            reviewView = ReviewView_.build(this);
            reviewView.bind(review);
            layoutReviews.addView(reviewView);
        }
    }

}
