package com.example.android.mymovieapp.database;

import android.database.sqlite.SQLiteException;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
        try {
            FavoriteDbHelper dbHelper = new FavoriteDbHelper(context);
            mDb = dbHelper.getWritableDatabase();

            mDb.beginTransaction();
            mDb.delete(FavoriteEntry.TRAILER_TABLE_NAME,
                    FavoriteEntry.COLUMN_MOVIE_ID + "=" + mMovieId, null);
            mDb.delete(FavoriteEntry.REVIEW_TABLE_NAME,
                    FavoriteEntry.COLUMN_MOVIE_ID + "=" + mMovieId, null);
            int rowsDeleted = mDb.delete(FavoriteEntry.MOVIE_TABLE_NAME,
                    FavoriteEntry.COLUMN_MOVIE_ID + "=" + mMovieId, null);
            if (rowsDeleted > 0) {
                mDb.setTransactionSuccessful();
                return true;
            }
            return false;
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            mDb.endTransaction();
            mDb.close();
        }
        return null;
    }

    @Override
    public void deliverResult(Boolean data) {
        mDatabaseResult = data;
        super.deliverResult(data);
    }
}
