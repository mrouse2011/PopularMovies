package com.glendroid.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marlonrouse on 16/10/15.
 */
public class DbOperations {

    // Database fields
    private DbWrapper dbHelper;
    private String[] STUDENT_TABLE_COLUMNS = { DbWrapper.MOVIE_ID, DbWrapper.MOVIE_POSTER_PATH, DbWrapper.ORIGINAL_TITLE, DbWrapper.OVERVIEW,
        DbWrapper.VOTE_AVERAGE, DbWrapper.RELEASE_DATE};
    private SQLiteDatabase db;

    public DbOperations(Context context) {
        dbHelper = new DbWrapper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void dropDb() {
        dbHelper.onUpgrade(db, 1, 2);
    }

    public void addMovie(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(DbWrapper.MOVIE_ID, movie.getMovieId());
        values.put(DbWrapper.MOVIE_POSTER_PATH, movie.getMoviePosterPath());
        values.put(DbWrapper.ORIGINAL_TITLE, movie.getOriginalTitle());
        values.put(DbWrapper.OVERVIEW, movie.getOverview());
        values.put(DbWrapper.VOTE_AVERAGE, movie.getVoteAverage());
        values.put(DbWrapper.RELEASE_DATE, movie.getReleaseDate());

        db.insert(DbWrapper.MOVIES, null, values);
    }

    public void deleteMovie(Movie movie) {
        long id = Long.parseLong(movie.getMovieId());
        db.delete(DbWrapper.MOVIES, DbWrapper.MOVIE_ID + " = " + id, null);
    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();

        String[] columns = { DbWrapper.MOVIE_ID, DbWrapper.MOVIE_POSTER_PATH, DbWrapper.ORIGINAL_TITLE, DbWrapper.OVERVIEW,
            DbWrapper.VOTE_AVERAGE, DbWrapper.RELEASE_DATE };

        Cursor cursor = db.query(DbWrapper.MOVIES, columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Movie movie = parseMovie(cursor);
            movies.add(movie);
            cursor.moveToNext();
        }

        cursor.close();
        return movies;
    }

    private Movie parseMovie(Cursor cursor) {
        Movie movie = new Movie(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        return movie;
    }
}
