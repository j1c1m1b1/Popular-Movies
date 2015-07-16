package com.udacity.jcmb.popularmovies.db.contracts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Julio Mendoza on 7/16/15.
 */
public class PopularMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.jcmb.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String MOVIES_PATH = "movies";

    public static final String TRAILERS_PATH = "trailers";

    public static final String REVIEWS_PATH = "reviews";


    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES_PATH;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES_PATH;

        public static final String TABLE_NAME = "favorite_movies";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_IMAGE_FILE_NAME = "image_file_name";

        public static final String COLUMN_BACKDROP_FILE_NAME = "backdrop_file_name";

        public static final String COLUMN_AVERAGE = "average";

        public static final String COLUMN_SYNOPSIS = "synopsis";

        public static final String COLUMN_YEAR = "year";

        public static final String COLUMN_DURATION = "duration";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TrailersEntry implements BaseColumns
    {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TRAILERS_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TRAILERS_PATH;

        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_TRAILER_ID = "trailer_id";

        public static final String COLUMN_MOV_KEY = "movie_id";

    }

    public static final class ReviewsEntry implements BaseColumns
    {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(REVIEWS_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + REVIEWS_PATH;

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_CONTENT = "content";

        public static final String COLUMN_MOV_KEY = "movie_id";

    }
}
