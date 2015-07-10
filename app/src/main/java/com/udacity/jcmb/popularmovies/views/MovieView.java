package com.udacity.jcmb.popularmovies.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.connection.Requests;
import com.udacity.jcmb.popularmovies.interfaces.OnMovieChosenListener;
import com.udacity.jcmb.popularmovies.model.Movie;
import com.udacity.jcmb.popularmovies.utils.Utils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.concurrent.ExecutionException;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@EViewGroup(R.layout.view_movie)
public class MovieView extends LinearLayout{

    @ViewById
    ImageView ivMovie;

    private Context context;
    private int color;

    public MovieView(Context context) {
        super(context);
        init();
    }

    public MovieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        context = getContext().getApplicationContext();
        int height = (int) Utils.convertDpToPixel(240, context);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(params);
    }

    public void bind(final Movie movie, final OnMovieChosenListener onMovieChosenListener)
    {
        Glide.with(context).load(Requests.IMAGES_URL + movie.getImageFileName())
                .centerCrop().into(ivMovie);
        loadBitmap(movie.getImageFileName());

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onMovieChosenListener.onMovieChosen(movie, 0, 0, color);
            }
        });

//        final CustomGestureDetector detector = new CustomGestureDetector(movie,
//                onMovieChosenListener);
//        setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                detector.onSingleTapUp(motionEvent);
//                return true;
//            }
//        });

    }

    @Background
    void loadBitmap(String imageFileName)
    {
        Bitmap resource = null;
        try {
            resource = Glide.with(context)
                    .load(Requests.SMALL_IMAGES_URL + imageFileName)
                    .asBitmap()
                    .into(120, 120)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        color = Utils.getAverageColorOfImage(resource);
    }

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener
    {
        private Movie movie;

        private OnMovieChosenListener onMovieChosenListener;

        public CustomGestureDetector(Movie movie, OnMovieChosenListener onMovieChosenListener)
        {
            this.movie = movie;
            this.onMovieChosenListener = onMovieChosenListener;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
                int x = (int) e.getRawX();

                int y = (int) e.getRawY();

                onMovieChosenListener.onMovieChosen(movie, x, y, color);

                return true;
        }

    }
}
