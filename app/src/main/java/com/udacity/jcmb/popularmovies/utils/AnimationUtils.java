package com.udacity.jcmb.popularmovies.utils;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

import io.codetail.animation.SupportAnimator;

/**
 * @author Julio Mendoza on 7/15/15.
 */
public class AnimationUtils {

    /**
     * Creates a circular reveal animation starting from the point specified by the x and y
     * parameters.
     * @param view The view the circular reveal effect will be applied to.
     * @param x The x coordinate for the effect to start from.
     * @param y The y coordinate for the effect to start from.
     * @param context The context the view is actually in.
     */
    public static void createCircularReveal(final View view, final int x, final int y,
                                            final Context context)
    {
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                int px = x;
                int py = y;
                if(x == 0)
                {
                    Point size = Utils.getScreenDimensions(context);
                    px = size.x / 2;
                    py = size.y / 2;
                }


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Animator anim = ViewAnimationUtils.createCircularReveal(view, x, y,
                            0, view.getWidth());
                    anim.setDuration(500);
                    view.setVisibility(View.VISIBLE);
                    anim.start();
                }
                else
                {
                    SupportAnimator anim = io.codetail.animation.ViewAnimationUtils.
                            createCircularReveal(view, px, py, 0, view.getWidth());
                    anim.setDuration(500);
                    view.setVisibility(View.VISIBLE);
                    anim.start();
                }

                view.removeOnLayoutChangeListener(this);
            }
        });
    }
}
