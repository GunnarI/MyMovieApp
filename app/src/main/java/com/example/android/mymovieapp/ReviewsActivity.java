package com.example.android.mymovieapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.android.mymovieapp.adapters.ReviewsAdapter;
import com.example.android.mymovieapp.loaders.FetchMovieReviews;

import java.util.ArrayList;

/**
 * Created by gunnaringi on 2017-03-05.
 */

public class ReviewsActivity extends AppCompatActivity
        implements LoaderCallbacks<ArrayList<ReviewData>> {

    private String movieId;
    private String movieTitle;
    private ArrayList<ReviewData> reviewsData;

    private TextView mMovieTitle;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private ReviewsAdapter mReviewsAdapter;

    private static final int REVIEWS_LOADER_ID = 2;

    private static final String REVIEW_DETAIL_EXTRA = "ReviewDetail";
    private static final String MOVIE_ID_EXTRA = "MovieId";
    private static final String MOVIE_TITLE_EXTRA = "MovieTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Intent intentThatStartedThisActivity = getIntent();
        mMovieTitle = (TextView) findViewById(R.id.movie_title_reviews);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_reviews_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.reviews_error_message_display);

        if (intentThatStartedThisActivity != null) {
            Bundle extras = intentThatStartedThisActivity.getExtras();

            if (extras.containsKey(MOVIE_ID_EXTRA)) {
                movieId = extras.getString(MOVIE_ID_EXTRA);
                movieTitle = extras.getString(MOVIE_TITLE_EXTRA);

                mMovieTitle.setText(movieTitle);

                LinearLayoutManager layoutManager =
                        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layoutManager);
                mReviewsAdapter = new ReviewsAdapter();
                mRecyclerView.setAdapter(mReviewsAdapter);

                if (extras.containsKey(REVIEW_DETAIL_EXTRA)) {
                    reviewsData = extras.getParcelableArrayList(REVIEW_DETAIL_EXTRA);
                    if (reviewsData.size() > 1) {
                        showReviewsView();
                        mReviewsAdapter.setReviewData(reviewsData);
                        mReviewsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "No reviews have been written for this movie",
                                Toast.LENGTH_LONG).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putParcelableArrayListExtra("ReviewExtra", reviewsData);

                        setResult(Activity.RESULT_OK, resultIntent);
                        this.finish();
                    }
                } else {
                    loadReviewData();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("ReviewExtra", reviewsData);

        setResult(Activity.RESULT_OK, resultIntent);

        this.finish();
        return true;
    }

    public void loadReviewData() {
        int loaderId = REVIEWS_LOADER_ID;
        LoaderCallbacks<ArrayList<ReviewData>> callbacks = ReviewsActivity.this;
        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callbacks);
    }

    @Override
    public Loader<ArrayList<ReviewData>> onCreateLoader(int id, Bundle args) {
        return new FetchMovieReviews(this, mLoadingIndicator, movieId);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ReviewData>> loader, ArrayList<ReviewData> data) {
        if (data != null && data.size() > 1) {
            showReviewsView();
            mReviewsAdapter.setReviewData(data);
            mReviewsAdapter.notifyDataSetChanged();
            reviewsData = data;
        } else if (data.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "No reviews have been written for this movie",
                    Toast.LENGTH_LONG).show();
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra("ReviewExtra", data);

            setResult(Activity.RESULT_OK, resultIntent);
            this.finish();
        } else {
            showErrorMessage();
        }
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ReviewData>> loader) {

    }

    private void showReviewsView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
