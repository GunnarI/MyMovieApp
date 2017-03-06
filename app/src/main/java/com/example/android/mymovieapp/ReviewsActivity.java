package com.example.android.mymovieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by gunnaringi on 2017-03-05.
 */

public class ReviewsActivity extends AppCompatActivity {
    private String movieId;
    private String movieTitle;

    private TextView mReviewAuthor;
    private TextView mReviewContent;

    private TextView mMovieTitle;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private ReviewsAdapter mReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Intent intentThatStartedThisActivity = getIntent();
        mMovieTitle = (TextView) findViewById(R.id.movie_title_reviews);
        mReviewAuthor = (TextView) findViewById(R.id.review_author);
        mReviewContent = (TextView) findViewById(R.id.review_content);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_trailer_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.reviews_error_message_display);

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                String[] movieData =
                        intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);
                movieId = movieData[0];
                movieTitle = movieData[1];

                LinearLayoutManager layoutManager =
                        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layoutManager);
                mReviewsAdapter = new ReviewsAdapter();
                mRecyclerView.setAdapter(mReviewsAdapter);


            }
        }
    }
}
