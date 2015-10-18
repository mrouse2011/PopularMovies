package com.glendroid.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by marlonrouse on 16/10/15.
 */
public class MovieDetailActivity extends AppCompatActivity{

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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_detail2, movieDetailFragment, "Detail")
                .commit();

    }
}
