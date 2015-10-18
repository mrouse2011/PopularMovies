package com.glendroid.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    MovieListAdaptor adapter;
    GridView gridView;

    String JSONstring;
    ArrayList<Movie> movies = new ArrayList<>();

    View rootView;

    public MainActivityFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_popularity) {
            FetchPopularMoviesTask fetch = new FetchPopularMoviesTask();
            fetch.setFragment(this);
            fetch.execute("popularity");
            return true;
        }
        if(id==R.id.action_rating) {
            FetchPopularMoviesTask fetch = new FetchPopularMoviesTask();
            fetch.setFragment(this);
            fetch.execute("rating");
            return true;
        }
        if(id==R.id.action_favourites) {
            gridView = (GridView)this.rootView.findViewById(R.id.gridView);
            if (getActivity().getApplicationContext().getResources().getBoolean(R.bool.isTablet)) {
                gridView.setNumColumns(3);
            } else {
                gridView.setNumColumns(2);
            }
            List<Movie> faves = MainActivity.dbOperations.getAllMovies();
            adapter = new MovieListAdaptor(faves, getActivity().getApplicationContext(), this);
            adapter.notifyDataSetChanged();
            gridView.setAdapter(adapter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        return this.rootView;
    }

    public void setJSONstring(String s) {
        this.JSONstring = s;
        try {
            JSONObject myJSONobject = new JSONObject(s);
            JSONArray myJSONarray = myJSONobject.getJSONArray("results");
            if (movies.size()>0) {
                movies.clear();
            }
            for (int i=0; i<myJSONarray.length(); i++) {
                JSONObject el = myJSONarray.getJSONObject(i);
                movies.add(new Movie(el.getString("id"), el.getString("poster_path"), el.getString("original_title"), el.getString("overview")
                        , el.getString("vote_average"), el.getString("release_date")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gridView = (GridView)this.rootView.findViewById(R.id.gridView);
        //setnumcols for gridview
        if (getActivity().getApplicationContext().getResources().getBoolean(R.bool.isTablet)) {
            gridView.setNumColumns(3);
        } else {
            gridView.setNumColumns(2);
        }
        adapter = new MovieListAdaptor(movies, getActivity().getApplicationContext(), this);
        gridView.setAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchPopularMoviesTask fetch = new FetchPopularMoviesTask();
        fetch.setFragment(this);
        fetch.execute("popularity");
    }

    private class FetchPopularMoviesTask extends AsyncTask<String, Void, String> {

        StringBuffer buffer = new StringBuffer();
        MainActivityFragment f;

        void setFragment(MainActivityFragment f) {
            this.f = f;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String searchType = "popularity.desc";
            if (params[0].equals("popularity")) {
                searchType = "popularity.desc";
            } else {
                searchType = "vote_average.desc";
            }

            try {
                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                        .appendQueryParameter("sort_by", searchType)
                        .appendQueryParameter("api_key", "c631978e6772cab470065dcf852b62d0").build();
                String myUrl = builtUri.toString();
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(myUrl);

                // Create the request to OpenWeatherMap, and open the connection
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
            if (s!=null)
                f.setJSONstring(s);
        }
    }
}
