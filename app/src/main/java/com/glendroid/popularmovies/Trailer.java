package com.glendroid.popularmovies;

/**
 * Created by marlonrouse on 13/10/15.
 */
public class Trailer {
    private String youtubeKey;
    private String trailerName;

    public String getYoutubeKey() {
        return youtubeKey;
    }

    public void setYoutubeKey(String youtubeKey) {
        this.youtubeKey = youtubeKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }

    public Trailer(String trailerName, String youtubeKey) {
        this.youtubeKey = youtubeKey;
        this.trailerName = trailerName;
    }
}