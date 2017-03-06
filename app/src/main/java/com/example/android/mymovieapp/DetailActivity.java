package com.example.android.mymovieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;

import com.example.android.mymovieapp.TrailerAdapter.TrailerAdapterOnClickHandler;

/**
 * Created by gunnaringi on 2017-02-02.
 */

public class DetailActivity extends AppCompatActivity implements
        TrailerAdapterOnClickHandler,
        LoaderCallbacks<ArrayList<String[]>> {

    private MovieData mMovieData;
    private TextView mMovieTitle;

    private ImageView mMovieThumbnail;
    private TextView mMovieDate;
    private ImageView mMovieRatingStar1;
    private ImageView mMovieRatingStar2;
    private ImageView mMovieRatingStar3;
    private ImageView mMovieRatingStar4;
    private ImageView mMovieRatingStar5;
    private TextView mMovieDescription;

    private TrailerAdapter mTrailerAdapter;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;

    private static final int TRAILER_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentThatStartedThisActivity = getIntent();

        mMovieTitle = (TextView) findViewById(R.id.movie_title);
        mMovieThumbnail = (ImageView) findViewById(R.id.movie_thumbnail);
        mMovieDate = (TextView) findViewById(R.id.movie_rel_date);
        mMovieRatingStar1 = (ImageView) findViewById(R.id.star_icon_1);
        mMovieRatingStar2 = (ImageView) findViewById(R.id.star_icon_2);
        mMovieRatingStar3 = (ImageView) findViewById(R.id.star_icon_3);
        mMovieRatingStar4 = (ImageView) findViewById(R.id.star_icon_4);
        mMovieRatingStar5 = (ImageView) findViewById(R.id.star_icon_5);
        mMovieDescription = (TextView) findViewById(R.id.movie_description);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_trailer_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.trailers_error_message_display);

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("MovieDetail")) {
                mMovieData = intentThatStartedThisActivity
                        .getParcelableExtra("MovieDetail");

                mMovieTitle.setText(mMovieData.getTitle());
                Picasso.with(this)
                        .load("http://image.tmdb.org/t/p/w500" + mMovieData.getImgUrl())
                        .into(mMovieThumbnail);
                mMovieDate.setText(mMovieData.getRelYear());
                Double rating = Double.parseDouble(mMovieData.getRating());
                int[] ratingStars = getRatingStars(rating);

                switch (ratingStars[0]) {
                    case 5:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 4:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar5);
                        break;
                    case 3:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 2:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 1:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 0:
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                }

                final Button button = (Button) findViewById(R.id.review_button);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Context context = DetailActivity.this;
                        Class destinationClass = ReviewsActivity.class;

                        Intent reviewsIntent = new Intent(context, destinationClass);
                        reviewsIntent.putExtra(Intent.EXTRA_TEXT,
                                new String[]{mMovieData.getId(), mMovieData.getTitle()});
                        startActivity(reviewsIntent);
                    }
                });

                mMovieDescription.setText(mMovieData.getOverview());
            }
        }


        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mTrailerAdapter = new TrailerAdapter(this);
        mRecyclerView.setAdapter(mTrailerAdapter);

        loadTrailerData();
    }

    @Override
    public void onClick(String trailerClicked) {
        Uri youtubeUrl = Uri.parse("http://www.youtube.com/watch?v=" + trailerClicked);
        Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUrl);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void loadTrailerData() {
        int loaderId = TRAILER_LOADER_ID;
        LoaderManager.LoaderCallbacks<ArrayList<String[]>> callbacks = DetailActivity.this;
        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callbacks);
    }

    @Override
    public Loader<ArrayList<String[]>> onCreateLoader(int id, Bundle args) {
        return new FetchMovieTrailers(this, mLoadingIndicator, mMovieData.getId());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String[]>> loader, ArrayList<String[]> data) {
        if (data != null) {
            showTrailersView();
            mTrailerAdapter.setTrailersData(data);
            mTrailerAdapter.notifyDataSetChanged();
        } else {
            showErrorMessage();
        }
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String[]>> loader) {

    }

    private void showTrailersView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public int[] getRatingStars(Double rating) {
        int[] ratingStars = new int[2];
        Double ratingRem = (rating % 2.0) / 2;
        Double ratingBase = rating / 2 - ratingRem;
        ratingStars[0] = ratingBase.intValue();

        Double ratingRemRem = ratingRem % 0.25;
        Double ratingRemBase = ratingRem / 0.25 - ratingRemRem;
        int partialStar = ratingRemBase.intValue();

        switch (partialStar) {
            case 0:
                ratingStars[1] = R.drawable.empty_star;
                break;
            case 1:
                ratingStars[1] = R.drawable.quarter_star;
                break;
            case 2:
                ratingStars[1] = R.drawable.half_star;
                break;
            case 3:
                ratingStars[1] = R.drawable.three_quarter_star;
                break;
            default:
                break;
        }

        return ratingStars;
    }
}
