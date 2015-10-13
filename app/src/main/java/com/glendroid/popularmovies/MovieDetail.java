package com.glendroid.popularmovies;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marlonrouse on 9/10/15.
 */
public class MovieDetail extends FragmentActivity {

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_details);

        Intent intent = getIntent();
        Movie movie = (Movie)intent.getSerializableExtra("movie");

        TextView textViewTitle = (TextView)findViewById(R.id.textViewTitle);
        textViewTitle.setText(movie.getOriginalTitle());
        TextView textViewOverview = (TextView)findViewById(R.id.textViewOverview);
        textViewOverview.setText(movie.getOverview());
        TextView textViewUserRating = (TextView)findViewById(R.id.textViewUserRating);
        textViewUserRating.setText(movie.getVoteAverage());
        TextView textViewReleaseDate = (TextView)findViewById(R.id.textViewReleaseDate);
        textViewReleaseDate.setText(movie.getReleaseDate());
        ImageView imageViewThumbnail = (ImageView)findViewById(R.id.imageViewThumbnail);
        Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w185" +movie.getMoviePosterPath()).into(imageViewThumbnail);

        FetchMovieTrailersTask task = new FetchMovieTrailersTask();
        task.setFragmentActivity(this);
        task.execute(movie.getMovieId());
    }

    void setTrailers(final List<Trailer> trailers) {
        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);
        for (int i=0; i<trailers.size(); i++) {
            final Trailer trailer = trailers.get(i);
            TextView trailerTextView = new TextView(getApplicationContext());
            trailerTextView.setText(trailer.getTrailerName());
            trailerTextView.setTextColor(Color.parseColor("#000000"));
            trailerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getYoutubeKey())));
                }
            });
            linearLayout.addView(trailerTextView);
        }
    }

    class FetchMovieTrailersTask extends AsyncTask<String, Void, String> {

        MovieDetail fragmentActivity;

        void setFragmentActivity(MovieDetail movieDetail) {
            this.fragmentActivity = movieDetail;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer buffer = new StringBuffer();
            String movieId = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/movie/"+movieId+"/videos").buildUpon()
                        .appendQueryParameter("api_key", "c631978e6772cab470065dcf852b62d0").build();
                String myUrl = builtUri.toString();
                URL url = new URL(myUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                e.printStackTrace();
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject myJSONobject = new JSONObject(s);
                JSONArray myJSONarray = myJSONobject.getJSONArray("results");
                List<Trailer> trailers = new ArrayList<>();
                for (int i=0; i<myJSONarray.length(); i++) {
                    JSONObject el = myJSONarray.getJSONObject(i);
                    Trailer trailer = new Trailer(el.getString("name"), el.getString("key"));
                    trailers.add(trailer);
                }
                fragmentActivity.setTrailers(trailers);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
