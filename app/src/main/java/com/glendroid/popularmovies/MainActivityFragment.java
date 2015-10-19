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

    String searchType;
    class SearchType {
        public static final String POPULARITY = "popularity.desc";
        public static final String RATING = "vote_average.desc";
        public static final String FAVES = "faves";
    }

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

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
            fetch.execute(SearchType.POPULARITY);
            return true;
        }
        if(id==R.id.action_rating) {
            FetchPopularMoviesTask fetch = new FetchPopularMoviesTask();
            fetch.setFragment(this);
            fetch.execute(SearchType.RATING);
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
            gridView.setAdapter(adapter);
            searchType = SearchType.FAVES;
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

        if (getActivity().getApplicationContext().getResources().getBoolean(R.bool.isTablet)) {
            gridView.setNumColumns(3);
        } else {
            gridView.setNumColumns(2);
        }
        adapter = new MovieListAdaptor(movies, getActivity().getApplicationContext(), this);
        gridView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchPopularMoviesTask fetch = new FetchPopularMoviesTask();
        fetch.setFragment(this);
        if (this.searchType==null || this.searchType.equals(SearchType.POPULARITY)) {
            fetch.execute(SearchType.POPULARITY);
        } else if (this.searchType.equals(SearchType.RATING)) {
            fetch.execute(SearchType.RATING);
        } else if (this.searchType.equals(SearchType.FAVES)) {
            List<Movie> faves = MainActivity.dbOperations.getAllMovies();
            adapter = new MovieListAdaptor(faves, getActivity().getApplicationContext(), this);
            gridView.setAdapter(adapter);
        } else {
            Log.e("ERROR", "Invalid SearchType");
        }
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

            if (params[0].equals(SearchType.POPULARITY)) {
                searchType = SearchType.POPULARITY;
            } else {
                searchType = SearchType.RATING;
            }

            try {
                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                        .appendQueryParameter("sort_by", searchType)
                        .appendQueryParameter("api_key", Constants.API_KEY).build();
                String myUrl = builtUri.toString();
                URL url = new URL(myUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error ", e);
                e.printStackTrace();
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivityFragment", "Error closing stream", e);
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
