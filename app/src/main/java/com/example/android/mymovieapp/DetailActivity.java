package com.example.android.mymovieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by gunnaringi on 2017-02-02.
 */

public class DetailActivity extends AppCompatActivity {

    private MovieData mMovieData;
    private TextView mMovieTitle;
    private ImageView mMovieThumbnail;
    private TextView mMovieDate;
    private TextView mMovieRating;
    private TextView mMovieDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentThatStartedThisActivity = getIntent();

        mMovieTitle = (TextView) findViewById(R.id.movie_title);
        mMovieThumbnail = (ImageView) findViewById(R.id.movie_thumbnail);
        mMovieDate = (TextView) findViewById(R.id.movie_rel_date);
        mMovieRating = (TextView) findViewById(R.id.movie_rating);
        mMovieDescription = (TextView) findViewById(R.id.movie_description);

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("MovieDetail")) {
                mMovieData = (MovieData) intentThatStartedThisActivity.getSerializableExtra("MovieDetail");
                mMovieTitle.setText(mMovieData.getTitle());
                Picasso.with(this)
                        .load("http://image.tmdb.org/t/p/w342" + mMovieData.getImgUrl())
                        .placeholder(R.drawable.imagenotfound_icon)
                        .into(mMovieThumbnail);
                mMovieDate.setText(mMovieData.getRelDate());
                mMovieRating.setText(mMovieData.getRating());
                mMovieDescription.setText(mMovieData.getOverview());
            }
        }
    }
}
