package com.glendroid.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marlonrouse on 16/10/15.
 */
public class DbWrapper extends SQLiteOpenHelper {

    public static final String MOVIES = "Movies";
    public static final String MOVIE_ID = "movieId";
    public static final String MOVIE_POSTER_PATH = "moviePosterPath";
    public static final String ORIGINAL_TITLE = "originalTitle";
    public static final String OVERVIEW = "overview";
    public static final String VOTE_AVERAGE = "voteAverage";
    public static final String RELEASE_DATE = "releaseDate";

    private static final String DATABASE_NAME = "Movies.db";
    private static final int DATABASE_VERSION = 1;

    // creation SQLite statement
    private static final String DATABASE_CREATE = "create table " + MOVIES
            + "(" + MOVIE_ID + " integer primary key autoincrement, "
            + MOVIE_POSTER_PATH + " text, "
            + ORIGINAL_TITLE + " text, "
            + OVERVIEW + " text, "
            + VOTE_AVERAGE + " text, "
            + RELEASE_DATE + " text );";

    public DbWrapper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you should do some logging in here
        // ..

        db.execSQL("DROP TABLE IF EXISTS " + MOVIES);
        onCreate(db);
    }
}
