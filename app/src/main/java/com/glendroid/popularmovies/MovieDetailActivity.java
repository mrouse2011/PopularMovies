package com.glendroid.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * Created by marlonrouse on 16/10/15.
 */
public class MovieDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Movie movie = (Movie)intent.getSerializableExtra("movie");
        Bundle arguments = new Bundle();
        // Pass the selected Golfcourse object to the DetailFragment
        arguments.putSerializable("movie", movie);
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        movieDetailFragment.setContext(getApplicationContext());
        movieDetailFragment.setArguments(arguments);
        setContentView(R.layout.activity_detail);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_detail2, movieDetailFragment, "Detail")
                .commit();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getApplicationContext().getResources().getBoolean(R.bool.isTablet)==false) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
