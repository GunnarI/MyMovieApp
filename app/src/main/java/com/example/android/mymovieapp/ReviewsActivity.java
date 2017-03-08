package com.example.android.mymovieapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by gunnaringi on 2017-03-05.
 */

public class ReviewsActivity extends AppCompatActivity
        implements LoaderCallbacks<ArrayList<String[]>> {

    private String movieId;
    private String movieTitle;

    //private TextView mReviewAuthor;
    //private TextView mReviewContent;

    private TextView mMovieTitle;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private ReviewsAdapter mReviewsAdapter;

    private static final int REVIEWS_LOADER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Intent intentThatStartedThisActivity = getIntent();
        mMovieTitle = (TextView) findViewById(R.id.movie_title_reviews);
        //mReviewAuthor = (TextView) findViewById(R.id.review_author);
        //mReviewContent = (TextView) findViewById(R.id.review_content);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_reviews_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.reviews_error_message_display);

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                String[] movieData =
                        intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);
                movieId = movieData[0];
                movieTitle = movieData[1];

                mMovieTitle.setText(movieTitle);

                LinearLayoutManager layoutManager =
                        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layoutManager);
                mReviewsAdapter = new ReviewsAdapter();
                mRecyclerView.setAdapter(mReviewsAdapter);

                loadReviewData();
            }
        }

        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return true;
    }

    public void loadReviewData() {
        int loaderId = REVIEWS_LOADER_ID;
        LoaderCallbacks<ArrayList<String[]>> callbacks = ReviewsActivity.this;
        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callbacks);
    }

    @Override
    public Loader<ArrayList<String[]>> onCreateLoader(int id, Bundle args) {
        return new FetchMovieReviews(this, mLoadingIndicator, movieId);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String[]>> loader, ArrayList<String[]> data) {
        if (data != null && data.size() > 1) {
            showReviewsView();
            mReviewsAdapter.setReviewData(data);
            mReviewsAdapter.notifyDataSetChanged();
        } else if (data.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "No reviews have been written for this movie",
                    Toast.LENGTH_LONG).show();
            this.finish();
            //showErrorMessage(); // TODO : replace with another specific message?
        } else {
            showErrorMessage();
        }
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String[]>> loader) {

    }

    private void showReviewsView() {
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
}
