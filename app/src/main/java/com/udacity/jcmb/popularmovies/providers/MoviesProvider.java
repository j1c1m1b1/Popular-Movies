package com.udacity.jcmb.popularmovies.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.udacity.jcmb.popularmovies.db.DatabaseHelper;
import com.udacity.jcmb.popularmovies.db.contracts.PopularMoviesContract;

import org.androidannotations.annotations.EProvider;

/**
 * @author Julio Mendoza on 7/16/15.
 */
@EProvider
public class MoviesProvider extends ContentProvider {

    private static final int MOVIES = 100;

    private static final int MOVIE_BY_ID = 101;

    private static final int TRAILERS = 200;

    private static final int TRAILERS_BY_MOVIE = 201;

    private static final int REVIEWS = 300;

    private static final int REVIEWS_BY_MOVIE = 301;
    private static UriMatcher matcher = buildURiMatcher();
    private DatabaseHelper databaseHelper;

    //Query Methods

    private static UriMatcher buildURiMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopularMoviesContract.CONTENT_AUTHORITY;

        //Movies URIs
        matcher.addURI(authority, PopularMoviesContract.MOVIES_PATH, MOVIES);
        matcher.addURI(authority, PopularMoviesContract.MOVIES_PATH + "/#", MOVIE_BY_ID);

        //Trailers URIs
        matcher.addURI(authority, PopularMoviesContract.TRAILERS_PATH, TRAILERS);
        matcher.addURI(authority, PopularMoviesContract.TRAILERS_PATH + "?"
                + PopularMoviesContract.TrailersEntry.COLUMN_MOV_KEY + "=#", TRAILERS_BY_MOVIE);

        //Reviews URIs
        matcher.addURI(authority, PopularMoviesContract.REVIEWS_PATH, REVIEWS);
        matcher.addURI(authority, PopularMoviesContract.TRAILERS_PATH + "?"
                + PopularMoviesContract.ReviewsEntry.COLUMN_MOV_KEY + "=#", REVIEWS_BY_MOVIE);
        return matcher;
    }

    private Cursor getMovies()
    {
        Cursor cursor;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        cursor = db.query(PopularMoviesContract.MoviesEntry.TABLE_NAME, null, null, null, null,
                null, null);
        return cursor;
    }

    private Cursor getMovieById(Uri uri)

    {
        String path = uri.getPath();

        String movieId = path.substring(path.lastIndexOf('/') + 1);

        String selection = PopularMoviesContract.MoviesEntry._ID + " = ?";

        String[] selectionArgs = new String[]{movieId};

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        return db.query(PopularMoviesContract.MoviesEntry.TABLE_NAME, null, selection, selectionArgs,
                null, null, null);
    }

    private Cursor getTrailers(Uri uri, String[] projection)
    {
        String movieId = uri.getQueryParameter(PopularMoviesContract.TrailersEntry.COLUMN_MOV_KEY);

        String selection = PopularMoviesContract.TrailersEntry.COLUMN_MOV_KEY + " = ?";

        String[] selectionArgs = new String[]{movieId};
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        return db.query(PopularMoviesContract.TrailersEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);
    }

    private Cursor getReviews(Uri uri, String[] projection)
    {
        String movieId = uri.getQueryParameter(PopularMoviesContract.ReviewsEntry.COLUMN_MOV_KEY);

        String selection = PopularMoviesContract.ReviewsEntry.COLUMN_MOV_KEY + " = ?";

        String[] selectionArgs = new String[]{movieId};
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        return db.query(PopularMoviesContract.ReviewsEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        Cursor cursor;
        switch (matcher.match(uri))
        {
            case MOVIES:
                cursor = getMovies();
                break;
            case MOVIE_BY_ID:
                cursor = getMovieById(uri);
                break;
            case TRAILERS:
                cursor = getTrailers(uri, projection);
                break;
            case REVIEWS:
                cursor = getReviews(uri, projection);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(cursor != null)
        {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        String contentType;
        switch (matcher.match(uri))
        {
            case MOVIES:
                contentType = PopularMoviesContract.MoviesEntry.CONTENT_TYPE;
                break;
            case MOVIE_BY_ID:
                contentType = PopularMoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
                break;
            case TRAILERS:
                contentType = PopularMoviesContract.TrailersEntry.CONTENT_TYPE;
                break;
            case REVIEWS:
                contentType = PopularMoviesContract.ReviewsEntry.CONTENT_TYPE;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return contentType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri resultUri;
        long id;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        switch (matcher.match(uri))
        {
            case MOVIES:
                id = db.insert(PopularMoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if(id > -1)
                {
                    resultUri = PopularMoviesContract.MoviesEntry.buildMovieUri(id);
                }
                else
                {
                    throw new SQLiteException("Failed to insert row into URI " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        selection = selection == null ? "1" : selection;
        switch (matcher.match(uri))
        {
            case MOVIES:
                rowsDeleted = db.delete(PopularMoviesContract.MoviesEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = db.delete(PopularMoviesContract.TrailersEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(PopularMoviesContract.ReviewsEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI "+ uri);
        }
        if(rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsAffected;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        switch (matcher.match(uri))
        {
            case MOVIES:
                rowsAffected = db.update(PopularMoviesContract.MoviesEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI "+ uri);
        }
        if(rowsAffected != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsAffected;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int insertedRows = 0;
        long id;
        switch (matcher.match(uri))
        {
            case MOVIES:
                db.beginTransaction();
                try
                {
                    for(ContentValues v : values)
                    {
                        id = db.insert(PopularMoviesContract.MoviesEntry.TABLE_NAME, null, v);
                        if(id != -1)
                        {
                            insertedRows++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return insertedRows;
            case TRAILERS:
                db.beginTransaction();
                try
                {
                    for(ContentValues v : values)
                    {
                        id = db.insert(PopularMoviesContract.TrailersEntry.TABLE_NAME, null, v);
                        if(id != -1)
                        {
                            insertedRows++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return insertedRows;
            case REVIEWS:
                db.beginTransaction();
                try
                {
                    for(ContentValues v : values)
                    {
                        id = db.insert(PopularMoviesContract.ReviewsEntry.TABLE_NAME, null, v);
                        if(id != -1)
                        {
                            insertedRows++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return insertedRows;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
