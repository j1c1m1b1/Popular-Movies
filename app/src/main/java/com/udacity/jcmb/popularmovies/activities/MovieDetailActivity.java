package com.udacity.jcmb.popularmovies.activities;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.connection.ContentSolver;
import com.udacity.jcmb.popularmovies.connection.Requests;
import com.udacity.jcmb.popularmovies.interfaces.ConnectionEventsListener;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.model.Review;
import com.udacity.jcmb.popularmovies.utils.BlurUtils;
import com.udacity.jcmb.popularmovies.utils.Utils;
import com.udacity.jcmb.popularmovies.views.ReviewView;
import com.udacity.jcmb.popularmovies.views.ReviewView_;
import com.udacity.jcmb.popularmovies.views.TrailerView;
import com.udacity.jcmb.popularmovies.views.TrailerView_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.codetail.animation.SupportAnimator;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@EActivity(R.layout.activity_movie_detail)
public class MovieDetailActivity extends AppCompatActivity
{

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
        createCircularReveal();
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

    void createCircularReveal()
    {
        coordinator.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom)
            {

                if(x == 0)
                {
                    Point size = Utils.getScreenDimensions(MovieDetailActivity.this);
                    x = size.x / 2;
                    y = size.y / 2;
                }


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Animator anim = ViewAnimationUtils.createCircularReveal(coordinator, x, y,
                            0, coordinator.getWidth());
                    anim.setDuration(500);
                    coordinator.setVisibility(View.VISIBLE);
                    anim.start();
                }
                else
                {
                    SupportAnimator anim = io.codetail.animation.ViewAnimationUtils.
                            createCircularReveal(coordinator, x, y, 0, coordinator.getWidth());
                    anim.setDuration(500);
                    coordinator.setVisibility(View.VISIBLE);
                    anim.start();
                }

                coordinator.removeOnLayoutChangeListener(this);
            }
        });
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
