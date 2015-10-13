package com.glendroid.popularmovies;

import java.io.Serializable;

/**
 * Created by marlonrouse on 7/10/15.
 */
public class Movie implements Serializable {

    private String movieId;
    private String moviePosterPath;
    private String originalTitle;
    private String overview;
    private String voteAverage;
    private String releaseDate;

    public Movie(String movieId, String moviePosterPath, String originalTitle, String overview, String voteAverage, String releaseDate) {
        this.movieId = movieId;
        this.moviePosterPath = moviePosterPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {

        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Movie(String movieId, String moviePosterPath) {
        this.movieId = movieId;
        this.moviePosterPath = moviePosterPath;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public void setMoviePosterPath(String moviePosterPath) {
        this.moviePosterPath = moviePosterPath;
    }

    public String getMovieId() {

        return movieId;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }
}
