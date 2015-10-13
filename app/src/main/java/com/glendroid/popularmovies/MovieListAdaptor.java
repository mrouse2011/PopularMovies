package com.glendroid.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
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
    private FragmentActivity fragmentActivity;

    public MovieListAdaptor(List movies, Context c, FragmentActivity activity) {
        this.movies = movies;
        this.context = c;
        this.fragmentActivity = activity;
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
        View rowView = inflater.inflate(R.layout.list_item_movie, null);
        if (position%2==0 && position<movies.size()-1) {

            ImageView img1 = (ImageView) rowView.findViewById(R.id.imageView1);
            img1.setTag(R.string.a_tag, movies.get(position));
            img1.setOnClickListener(new MyOnClickListener());
            Picasso.with(context).load("http://image.tmdb.org/t/p/w185" + (movies.get(position)).getMoviePosterPath()).into(img1);

            ImageView img2 = (ImageView) rowView.findViewById(R.id.imageView2);
            img2.setTag(R.string.a_tag, movies.get(position + 1));
            img2.setOnClickListener(new MyOnClickListener());
            Picasso.with(context).load("http://image.tmdb.org/t/p/w185" + (movies.get(position + 1)).getMoviePosterPath()).into(img2);

            return rowView;

        } else {

            return new View(context);
        }
    }

    void showDetail(Movie movie) {
        Intent myIntent = new Intent(fragmentActivity, MovieDetail.class);
        myIntent.putExtra("movie", movie); //Optional parameters
        fragmentActivity.startActivity(myIntent);
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showDetail((Movie)v.getTag(R.string.a_tag));
        }
    }
}