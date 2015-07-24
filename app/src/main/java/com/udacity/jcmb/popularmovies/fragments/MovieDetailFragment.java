package com.udacity.jcmb.popularmovies.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.activities.HomeActivity;
import com.udacity.jcmb.popularmovies.activities.MovieDetailActivity;
import com.udacity.jcmb.popularmovies.application.PopularMovies;
import com.udacity.jcmb.popularmovies.connection.ContentSolver;
import com.udacity.jcmb.popularmovies.connection.Requests;
import com.udacity.jcmb.popularmovies.interfaces.ConnectionEventsListener;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.model.Review;
import com.udacity.jcmb.popularmovies.model.Trailer;
import com.udacity.jcmb.popularmovies.utils.BlurUtils;
import com.udacity.jcmb.popularmovies.utils.Utils;
import com.udacity.jcmb.popularmovies.views.ReviewView;
import com.udacity.jcmb.popularmovies.views.ReviewView_;
import com.udacity.jcmb.popularmovies.views.TrailerView;
import com.udacity.jcmb.popularmovies.views.TrailerView_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Julio Mendoza on 7/14/15.
 */
@EFragment(R.layout.fragment_movie_detail)
public class MovieDetailFragment extends Fragment {

    private static final String MOVIE = "movie";
    private static final String TRAILERS = "trailers";
    private static final String REVIEWS = "reviews";

    @App
    PopularMovies app;

    @ViewById
    ScrollView coordinator;

    @ViewById
    ImageView ivMovieBackground;

    @ViewById
    CircleImageView ivMovie;

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

    HomeActivity homeActivity;

    MovieDetailActivity detailActivity;

    @FragmentArg
    int id;

    @FragmentArg
    String name;

    @FragmentArg
    String imageFileName;

    @FragmentArg
    String backdropFileName;

    @FragmentArg
    int x;

    @FragmentArg
    int y;

    @FragmentArg
    double average;

    @FragmentArg
    int color;

    @FragmentArg
    int position;

    private Movie movie;

    private ArrayList<Trailer> trailers;

    private ArrayList<Review> reviews;

    private boolean isFavorite;

    private CompoundButton.OnCheckedChangeListener checkedChangeListener;

    private MenuItem share;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    }

    @AfterViews
    void init()
    {
        initMovie();
        ActionBar toolbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(toolbar != null)
        {
            toolbar.setTitle(name);
            toolbar.setBackgroundDrawable(new ColorDrawable(color));
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            int darkerColor = Utils.getDarkerColor(color);
            getActivity().getWindow().setStatusBarColor(darkerColor);
        }

        Glide.with(this).load(Requests.IMAGES_URL + imageFileName).centerCrop().into(ivMovie);
        ivMovieBackground.setAlpha(0.8f);
        blurImage();

        checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    chkFavorite.setText(R.string.is_favorite);
                    createSnackBar(R.string.movie_is_favorite);
                    saveMovie();
                }
                else
                {
                    chkFavorite.setText(R.string.save_favorite);
                    createSnackBar(R.string.removed_favorite);
                    removeMovie();
                }
            }
        };

        tvAverage.setText(average + "/10");
        createCircularReveal(x, y);
    }

    private void createCircularReveal(int x, int y)
    {
        if(homeActivity != null)
        {
            homeActivity.createCircularReveal(x, y);
        }
        else
        {
            detailActivity.createCircularReveal(x, y);
        }
    }

    private void createSnackBar(int resId)
    {
        if(homeActivity != null)
        {
            homeActivity.createSnackBar(resId);
        }
        else
        {
            detailActivity.createSnackBar(resId);
        }
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
    void initMovie()
    {
        isFavorite = app.isFavorite(id);
        if(movie == null)
        {
            loadMovie();
        }
        else
        {
            refreshMovieInfo();
        }
    }

    @Background
    void loadMovie()
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

    @Background
    void getTrailers()
    {
        if(trailers != null)
        {
            refreshTrailers();
        }
        else if(isFavorite)
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
        if(reviews != null)
        {
            refreshReviews();
        }
        else if(isFavorite)
        {
            reviews = app.getReviewsOfMovie(movie);
            refreshReviews();
        }
        else
        {
            reviews = new ArrayList<>();
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
            trailerView = TrailerView_.build(getActivity());
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
            reviewView = ReviewView_.build(getActivity());
            reviewView.bind(review);
            layoutReviews.addView(reviewView);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE, movie);
        outState.putParcelableArrayList(TRAILERS, trailers);
        outState.putParcelableArrayList(REVIEWS, reviews);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            homeActivity = (HomeActivity) getActivity();
        }
        catch (ClassCastException e)
        {
            detailActivity = (MovieDetailActivity)getActivity();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(homeActivity != null)
        {
            inflater.inflate(R.menu.menu_home, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        share = menu.findItem(R.id.share);
        if(share != null)
        {
            share.setVisible(true);
        }
    }

    @OptionsItem(R.id.share)
    void share()
    {
        if(!trailers.isEmpty())
        {
            String trailerPath = Trailer.YOUTUBE_URI + trailers.get(0).getTrailerId();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, movie.getName() + " Trailer");
            sendIntent.putExtra(Intent.EXTRA_TEXT, trailerPath);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getString(R.string.share_trailer)));
        }
        else
        {
            createSnackBar(R.string.no_trailers);
        }
    }


}
