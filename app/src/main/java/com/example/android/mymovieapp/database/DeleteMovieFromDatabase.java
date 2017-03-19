package com.example.android.mymovieapp.database;

import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.mymovieapp.database.FavoriteContract.FavoriteEntry;

/**
 * Created by gunnaringi on 2017-03-11.
 */

public class DeleteMovieFromDatabase extends AsyncTaskLoader<Boolean> {

    private final String LOG_TAG = DeleteMovieFromDatabase.class.getSimpleName();
    private Context context;
    private String mMovieId;
    private Boolean mDatabaseResult;
    private ProgressBar mLoadingIndicator;

    public DeleteMovieFromDatabase(Context context, ProgressBar mLoadingIndicator, String movieId) {
        super(context);
        this.context = context;
        this.mLoadingIndicator = mLoadingIndicator;
        this.mMovieId = movieId;
    }

    @Override
    protected void onStartLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        forceLoad();
    }

    @Override
    public Boolean loadInBackground() {
        try {
            Uri trailerUri = FavoriteEntry.TRAILER_CONTENT_URI;
            trailerUri = trailerUri.buildUpon().appendPath(mMovieId).build();
            int trailersDeleted = context.getContentResolver().delete(trailerUri, null, null);

            Uri reviewUri = FavoriteEntry.REVIEW_CONTENT_URI;
            reviewUri = reviewUri.buildUpon().appendPath(mMovieId).build();
            int reviewsDeleted = context.getContentResolver().delete(reviewUri, null, null);

            Uri movieUri = FavoriteEntry.MOVIE_CONTENT_URI;
            movieUri = movieUri.buildUpon().appendPath(mMovieId).build();
            int moviesDeleted = context.getContentResolver().delete(movieUri, null, null);

            if (moviesDeleted > 0) {
                if (trailersDeleted == 0) {
                    Log.w(LOG_TAG, "No trailers deleted with movie id " + mMovieId);
                }
                if (reviewsDeleted == 0) {
                    Log.w(LOG_TAG, "No reviews deleted with movie id " + mMovieId);
                }
                return true;
            }

            return false;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        return null;
    }

    @Override
    public void deliverResult(Boolean data) {
        mDatabaseResult = data;
        super.deliverResult(data);
    }
}
