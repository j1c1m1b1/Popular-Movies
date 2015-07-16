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
import com.udacity.jcmb.popularmovies.model.Trailer;
import com.udacity.jcmb.popularmovies.utils.AnimationUtils;
import com.udacity.jcmb.popularmovies.utils.BlurUtils;
import com.udacity.jcmb.popularmovies.views.ReviewView;
import com.udacity.jcmb.popularmovies.views.ReviewView_;
import com.udacity.jcmb.popularmovies.views.TrailerView;
import com.udacity.jcmb.popularmovies.views.TrailerView_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
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

    private static final String MOVIE = "movie";
    private static final String TRAILERS = "trailers";
    private static final String REVIEWS = "reviews";
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

    private Movie movie;

    private ArrayList<Trailer> trailers;

    private ArrayList<Review> reviews;

    private boolean isFavorite;

    private CompoundButton.OnCheckedChangeListener checkedChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE))
        {
            movie = savedInstanceState.getParcelable(MOVIE);
            if(savedInstanceState.containsKey(TRAILERS))
            {
                trailers = savedInstanceState.getParcelableArrayList(TRAILERS);
            }
            if(savedInstanceState.containsKey(REVIEWS))
            {
                reviews = savedInstanceState.getParcelableArrayList(REVIEWS);
            }
        }
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
        getMovie();
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

        checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
        };

        tvAverage.setText(average + "/10");
        AnimationUtils.createCircularReveal(coordinator, x, y, this);
    }

    @Background
    void saveMovie()
    {
        app.saveMovie(movie, trailers, reviews);
    }

    @Background
    void removeMovie()
    {
        app.removeMovie(movie);
    }

    @UiThread
    void refreshCheckView(boolean isFavorite)
    {
        chkFavorite.setChecked(isFavorite);
        if(isFavorite)
        {
            chkFavorite.setText(R.string.is_favorite);
        }
        chkFavorite.setOnCheckedChangeListener(checkedChangeListener);
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

    @Background
    void getMovie()
    {
        isFavorite = app.isFavorite(id);
        if(movie == null)
        {
            if(isFavorite)
            {
                movie = app.getMovie(id);
                refreshMovieInfo();
            }
            else
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
        }
        else
        {
            refreshMovieInfo();
        }
    }

    @Background
    void getTrailers()
    {
        if(isFavorite)
        {
            trailers = app.getTrailersOfMovie(movie);
            refreshTrailers();
        }
        else
        {
            ConnectionEventsListener connectionEventsListener = new ConnectionEventsListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    trailers = ContentSolver.parseTrailers(response, movie);
                    refreshTrailers();
                }

                @Override
                public void onFail() {

                }
            };

            Requests.getMovieTrailers(id, connectionEventsListener);
        }
    }

    @Background
    void getReviews()
    {
        if(isFavorite)
        {
            reviews = app.getReviewsOfMovie(movie);
            refreshReviews();
        }
        else
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
    }

    @UiThread
    void refreshMovieInfo()
    {
        tvYear.setText("" + movie.getYear());
        tvDuration.setText("" + movie.getDuration());
        tvSynopsis.setText(movie.getSynopsis());
        refreshCheckView(isFavorite);
        getTrailers();
        getReviews();
    }

    @UiThread
    void refreshTrailers()
    {
        TrailerView trailerView;
        String pattern = "Trailer %d";
        String title, videoId;
        Trailer trailer;
        for(int i = 0; i < trailers.size(); i++)
        {
            title = String.format(pattern, i + 1);
            trailer = trailers.get(i);
            videoId = trailer.getTrailerId();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE, movie);
        outState.putParcelableArrayList(TRAILERS, trailers);
        outState.putParcelableArrayList(REVIEWS, reviews);
    }
}
