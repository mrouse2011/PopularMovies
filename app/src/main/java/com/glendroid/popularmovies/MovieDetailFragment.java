package com.glendroid.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class MovieDetailFragment extends Fragment {

    String name;
    LinearLayout linearLayout;

    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    View rootView;

    Movie movie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If intent arguments have a course object, get it
        if (getArguments() != null && getArguments().containsKey("movie")) {
            movie = (Movie)getArguments().getSerializable("movie");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.movie_details, container, false);

        if (movie!=null) {
            TextView textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
            textViewTitle.setText(movie.getOriginalTitle());
            TextView textViewOverview = (TextView) rootView.findViewById(R.id.textViewOverview);
            textViewOverview.setText(movie.getOverview());
            TextView textViewUserRating = (TextView) rootView.findViewById(R.id.textViewUserRating);
            textViewUserRating.setText(movie.getVoteAverage());
            TextView textViewReleaseDate = (TextView) rootView.findViewById(R.id.textViewReleaseDate);
            textViewReleaseDate.setText(movie.getReleaseDate());
            ImageView imageViewThumbnail = (ImageView) rootView.findViewById(R.id.imageViewThumbnail);
            Picasso.with(context).load("http://image.tmdb.org/t/p/w185" + movie.getMoviePosterPath()).into(imageViewThumbnail);

            TextView setAsFav = (TextView) rootView.findViewById(R.id.textViewFav);
            setAsFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageViewFav = (ImageView) rootView.findViewById(R.id.imageViewFav);
                    imageViewFav.setVisibility(View.VISIBLE);
                    MainActivity.dbOperations.addMovie(movie);
                }
            });

            for (Movie m : MainActivity.dbOperations.getAllMovies()) {
                if (m.getMovieId().equals(movie.getMovieId())) {
                    ImageView imageViewFav = (ImageView) rootView.findViewById(R.id.imageViewFav);
                    imageViewFav.setVisibility(View.VISIBLE);
                }
            }

            FetchMovieTrailersTask task = new FetchMovieTrailersTask();
            task.setFragment(this);
            task.execute(movie.getMovieId());

        }

        return this.rootView;
    }

    void setTrailers(final List<Trailer> trailers) {
        ScrollView scrollView = (ScrollView)rootView.findViewById(R.id.scrollView);
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);
        TextView trailerHeading = new TextView(context);
        trailerHeading.setText("Trailers:");
        trailerHeading.setTextColor(Color.parseColor("#000000"));
        linearLayout.addView(trailerHeading);
        for (int i=0; i<trailers.size(); i++) {
            final Trailer trailer = trailers.get(i);
            TextView trailerTextView = new TextView(context);
            trailerTextView.setText(trailer.getTrailerName());
            trailerTextView.setTextColor(Color.parseColor("#888888"));
            trailerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getYoutubeKey())));
                }
            });
            linearLayout.addView(trailerTextView);
        }
    }

    void setReviews(final List<Review> reviews) {
        TextView reviewHeading = new TextView(context);
        reviewHeading.setText("Reviews:");
        reviewHeading.setTextColor(Color.parseColor("#000000"));
        linearLayout.addView(reviewHeading);
        for (int i=0; i<reviews.size(); i++) {
            final Review review = reviews.get(i);
            TextView reviewTextView = new TextView(context);
            reviewTextView.setText(review.getAuthor() + " - " + review.getContent().substring(0, 22) + "...");
            reviewTextView.setTextColor(Color.parseColor("#888888"));
            reviewTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle(review.getAuthor());
                    alert.setMessage(review.getContent());
                    alert.setPositiveButton("OK",null);
                    alert.show();
                }
            });
            linearLayout.addView(reviewTextView);
        }
    }


    class FetchMovieTrailersTask extends AsyncTask<String, Void, String> {

        MovieDetailFragment fragment;
        String movieId;

        void setFragment(MovieDetailFragment movieDetailFragment) {
            this.fragment = movieDetailFragment;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer buffer = new StringBuffer();
            movieId = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/movie/"+movieId+"/videos").buildUpon()
                        .appendQueryParameter("api_key", "").build();
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
                fragment.setTrailers(trailers);
                FetchMovieReviewsTask task = new FetchMovieReviewsTask();
                task.setFragment(fragment);
                task.execute(this.movieId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class FetchMovieReviewsTask extends AsyncTask<String, Void, String> {

        MovieDetailFragment fragment;

        void setFragment(MovieDetailFragment movieDetailFragment) {
            this.fragment = movieDetailFragment;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer buffer = new StringBuffer();
            String movieId = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/movie/"+movieId+"/reviews").buildUpon()
                        .appendQueryParameter("api_key", "").build();
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
                List<Review> reviews = new ArrayList<>();
                for (int i=0; i<myJSONarray.length(); i++) {
                    JSONObject el = myJSONarray.getJSONObject(i);
                    Review review = new Review(el.getString("author"), el.getString("content"));
                    reviews.add(review);
                }
                fragment.setReviews(reviews);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
