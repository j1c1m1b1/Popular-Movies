package com.udacity.jcmb.popularmovies.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.udacity.jcmb.popularmovies.R;
import com.udacity.jcmb.popularmovies.model.Trailer;
import com.udacity.jcmb.popularmovies.utils.Utils;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@EViewGroup(R.layout.view_trailer)
public class TrailerView extends LinearLayout {

    @ViewById
    TextView tvTitle;

    public TrailerView(Context context) {
        super(context);
        init();
    }

    public TrailerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrailerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        int height = Utils.getListItemPreferredHeight(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(params);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    public void bind(String videoId, String title)
    {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Trailer.YOUTUBE_URI
                + videoId));

        tvTitle.setText(title);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(intent);
            }
        });
    }
}
