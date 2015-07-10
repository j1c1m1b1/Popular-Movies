package com.udacity.jcmb.popularmovies.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.model.Review;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@EViewGroup(R.layout.view_review)
public class ReviewView extends LinearLayout {

    @ViewById
    TextView tvAuthor;

    @ViewById
    TextView tvContent;

    public ReviewView(Context context) {
        super(context);
        init();
    }

    public ReviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        setOrientation(VERTICAL);
    }

    public void bind(Review review)
    {
        tvAuthor.setText(review.getAuthor());
        tvContent.setText(review.getContent());
    }
}
