package com.udacity.jcmb.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.udacity.jcmb.popularmovies.model.Movie;

import java.sql.SQLException;

/**
 * @author Julio Mendoza on 7/15/15.
 * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
 * @see <a href="https://github.com/j256/ormlite-examples/blob/master/android/HelloAndroid/src/com/example/helloandroid/DatabaseHelper.java">
 *     ORMLite SQL Helper Example Class</a>
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "Database Helper";

    @SuppressWarnings("FieldCanBeLocal")
    private static String DATABASE_NAME = "popmovies.db";

    @SuppressWarnings("FieldCanBeLocal")
    private static int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Movie.class);
        } catch (SQLException e)
        {
            Log.e(TAG, "Can't create the database. " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Movie.class, true);
        } catch (SQLException e) {
            Log.e(TAG, "Can't upgrade database");
            e.printStackTrace();
        }
    }
}
