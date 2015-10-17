package com.glendroid.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by marlonrouse on 8/10/15.
 */
public class MovieListAdaptor extends BaseAdapter {

    private List<Movie> movies;
    private Context context;
    private Fragment fragment;

    public MovieListAdaptor(List movies, Context c, Fragment fragment) {
        this.movies = movies;
        this.context = c;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ImageView imageView = null;

        if (convertView==null) {
            convertView = inflater.inflate(R.layout.grid_item_movie, null);
        }

        if (movies.get(position) != null) {
            imageView = (ImageView) convertView.findViewById(R.id.gridImageView);
            imageView.setTag(R.string.a_tag, movies.get(position));
            imageView.setOnClickListener(new MyOnClickListener());
            Picasso.with(context).load("http://image.tmdb.org/t/p/w185" + (movies.get(position)).getMoviePosterPath()).into(imageView);
        }

        return convertView;
    }

    void showDetail(Movie movie) {
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            Bundle arguments = new Bundle();
            arguments.putSerializable("movie", movie);
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(arguments);
            this.fragment.getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail, movieDetailFragment, "Detail")
                    .commit();
        } else {
            Intent myIntent = new Intent(this.fragment.getActivity(), MovieDetailActivity.class);
            myIntent.putExtra("movie", movie); //Optional parameters
            fragment.getActivity().startActivity(myIntent);
        }
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showDetail((Movie)v.getTag(R.string.a_tag));
        }
    }
}