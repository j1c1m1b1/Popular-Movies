package com.udacity.jcmb.popularmovies.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
public class MovieView extends FrameLayout{

    @ViewById
    ImageView ivMovie;

    @ViewById
    View selectedView;

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

    public void bind(final Movie movie, final OnMovieChosenListener onMovieChosenListener,
                     final boolean selected, final int position, boolean singleChoice)
    {
        Glide.with(context).load(Requests.IMAGES_URL + movie.getImageFileName())
                .centerCrop().into(ivMovie);
        loadBitmap(movie.getImageFileName());

        int[] location = Utils.getLocationInWindow(this, getContext());

        final int x = location[0] + getWidth()/2;

        final int y = location[1] + getHeight()/2;

        if(singleChoice)
        {
            if(selected)
            {
                selectedView.setVisibility(VISIBLE);
            }
            else if(selectedView.getVisibility() == VISIBLE)
            {
                selectedView.setVisibility(GONE);
            }
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selected)
                {
                    onMovieChosenListener.onMovieChosen(movie, x, y, color, position);
                }
            }
        });
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
}
