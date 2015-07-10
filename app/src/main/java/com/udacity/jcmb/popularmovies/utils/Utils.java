package com.udacity.jcmb.popularmovies.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Julio Mendoza on 7/9/15.
 */
@SuppressWarnings("unused")
public class Utils {

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static Drawable getDrawable(Context context, int placeholder) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            return context.getDrawable(placeholder);
        }
        else
        {
            //noinspection deprecation
            return context.getResources().getDrawable(placeholder);
        }
    }

    @SuppressWarnings("deprecation")
    public static void setBackgroundDrawable(View view, int resId, Context context)
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        {
            view.setBackgroundDrawable(context.getResources().getDrawable(resId));
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            view.setBackground(context.getResources().getDrawable(resId));
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            view.setBackground(context.getDrawable(resId));
        }
    }

    public static Point getScreenDimensions(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }

    public static String readFilesFromRaw(Context context, int resId)
    {
        InputStream stream = context.getResources().openRawResource(resId);
        Scanner scanner = new Scanner(stream);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNext())
        {
            builder.append(scanner.nextLine());
        }

        return builder.toString();
    }

    public static int getListItemPreferredHeight(Context context)
    {
        TypedValue value = new TypedValue();
        context.getTheme()
                .resolveAttribute(android.R.attr.listPreferredItemHeight, value, true);
        TypedValue.coerceToString(value.type, value.data);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int)value.getDimension(metrics);
    }

    public static int getAverageColorOfImage(Bitmap bitmap)
    {
        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++)
        {
            for (int x = 0; x < bitmap.getWidth(); x++)
            {
                int c = bitmap.getPixel(x, y);

                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
                // does alpha matter?
            }
        }


        int red = redBucket/pixelCount;
        int green = greenBucket/pixelCount;
        int blue = blueBucket/pixelCount;

        return Color.rgb(red, green, blue);
    }
}
