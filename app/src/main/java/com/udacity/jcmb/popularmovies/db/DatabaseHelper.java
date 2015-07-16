package com.udacity.jcmb.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.jcmb.popularmovies.db.contracts.PopularMoviesContract;

/**
 * @author Julio Mendoza on 7/16/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "_id";
    private static final String DATABASE_NAME = "popmovies.db";
    private static final int DATABASE_VERSION = 1;
    private static final String MOVIE_TABLE_CREATE =
            "CREATE TABLE " + PopularMoviesContract.MoviesEntry.TABLE_NAME
                + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + PopularMoviesContract.MoviesEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + PopularMoviesContract.MoviesEntry.COLUMN_IMAGE_FILE_NAME + " TEXT NOT NULL, "
                + PopularMoviesContract.MoviesEntry.COLUMN_BACKDROP_FILE_NAME + " TEXT NOT NULL, "
                + PopularMoviesContract.MoviesEntry.COLUMN_AVERAGE + " REAL NOT NULL, "
                + PopularMoviesContract.MoviesEntry.COLUMN_SYNOPSIS + " TEXT, "
                + PopularMoviesContract.MoviesEntry.COLUMN_YEAR + " INTEGER, "
                + PopularMoviesContract.MoviesEntry.COLUMN_DURATION + " INTEGER)";
    private static final String TRAILER_TABLE_CREATE =
            "CREATE TABLE " + PopularMoviesContract.TrailersEntry.TABLE_NAME
                    + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PopularMoviesContract.TrailersEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, "
                    + PopularMoviesContract.TrailersEntry.COLUMN_MOV_KEY + " INTEGER, "
                    + "FOREIGN KEY(" + PopularMoviesContract.TrailersEntry.COLUMN_MOV_KEY + ") "
                    + "REFERENCES " + PopularMoviesContract.MoviesEntry.TABLE_NAME
                    + " (" + COLUMN_ID + ") ON DELETE CASCADE)";
    private static final String REVIEW_TABLE_CREATE =
            "CREATE TABLE " + PopularMoviesContract.ReviewsEntry.TABLE_NAME
                    + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PopularMoviesContract.ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
                    + PopularMoviesContract.ReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL, "
                    + PopularMoviesContract.ReviewsEntry.COLUMN_MOV_KEY + " INTEGER, "
                    + "FOREIGN KEY(" + PopularMoviesContract.TrailersEntry.COLUMN_MOV_KEY + ") "
                    + "REFERENCES " + PopularMoviesContract.MoviesEntry.TABLE_NAME
                    + " (" + COLUMN_ID + ")  ON DELETE CASCADE)";
    private static final String DROP_TABLE_FORMAT =
            "DROP TABLE IF EXISTS %s";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MOVIE_TABLE_CREATE);
        db.execSQL(TRAILER_TABLE_CREATE);
        db.execSQL(REVIEW_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format(DROP_TABLE_FORMAT, PopularMoviesContract.ReviewsEntry.TABLE_NAME));
        db.execSQL(String.format(DROP_TABLE_FORMAT, PopularMoviesContract.TrailersEntry.TABLE_NAME));
        db.execSQL(String.format(DROP_TABLE_FORMAT, PopularMoviesContract.MoviesEntry.TABLE_NAME));
        onCreate(db);
    }
}
