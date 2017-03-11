package com.example.android.mymovieapp.database;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by gunnaringi on 2017-03-11.
 */

public class DeleteMovieFromDatabase extends AsyncTaskLoader<Boolean> {

    private final String LOG_TAG = DeleteMovieFromDatabase.class.getSimpleName();
    private Context context;
    private String mMovieId;
    private Boolean mDatabaseResult;
    private ProgressBar mLoadingIndicator;

    private SQLiteDatabase mDb;

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
        return null;
    }

    @Override
    public void deliverResult(Boolean data) {
        mDatabaseResult = data;
        super.deliverResult(data);
    }
}
