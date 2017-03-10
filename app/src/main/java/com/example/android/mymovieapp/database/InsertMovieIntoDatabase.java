package com.example.android.mymovieapp.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.mymovieapp.MovieData;
import com.example.android.mymovieapp.ReviewData;
import com.example.android.mymovieapp.TrailerData;
import com.example.android.mymovieapp.database.FavoriteContract.FavoriteEntry;

import java.util.ArrayList;

/**
 * Created by gunnaringi on 2017-03-10.
 */

public class InsertMovieIntoDatabase extends AsyncTaskLoader<Boolean> {

    private final String LOG_TAG = InsertMovieIntoDatabase.class.getSimpleName();
    private Context context;
    private MovieData mMovieData;
    private Boolean mDatabaseResult;

    private SQLiteDatabase mDb;

    public InsertMovieIntoDatabase(Context context, MovieData movieData) {
        super(context);
        this.context = context;
        this.mMovieData = movieData;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Boolean loadInBackground() {

        try {
            FavoriteDbHelper dbHelper = new FavoriteDbHelper(context);
            mDb = dbHelper.getWritableDatabase();

            mDb.beginTransaction();

            ContentValues movieDataToInsert = new ContentValues();
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_ID, mMovieData.getId());
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_POSTER_URL, mMovieData.getImgUrl());
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_OVERVIEW, mMovieData.getOverview());
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_RATING, mMovieData.getRating());
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE, mMovieData.getRelDate());

            mDb.insert(FavoriteEntry.MOVIE_TABLE_NAME, null, movieDataToInsert);

            ArrayList<TrailerData> mTrailerData = mMovieData.getTrailers();
            if (mTrailerData != null) {
                for(int i = 0; i < mTrailerData.size(); i++) {
                    ContentValues trailerDataToInsert = new ContentValues();
                    trailerDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_ID, mMovieData.getId());
                    trailerDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_TRAILER_TITLE,
                            mTrailerData.get(i).getTrailerTitle());
                    trailerDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_TRAILER_URL,
                            mTrailerData.get(i).getTrailerUrl());
                    trailerDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_TRAILER_TYPE,
                            mTrailerData.get(i).getTrailerType());

                    mDb.insert(FavoriteEntry.TRAILER_TABLE_NAME, null, trailerDataToInsert);
                }
            }

            ArrayList<ReviewData> mReviewData = mMovieData.getReviews();
            if (mTrailerData != null) {
                for(int j = 0; j < mReviewData.size(); j++) {
                    ContentValues reviewDataToInsert = new ContentValues();
                    reviewDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_ID, mMovieData.getId());
                    reviewDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_REVIEW_AUTHOR,
                            mReviewData.get(j).getReviewAuthor());
                    reviewDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_REVIEW_CONTENT,
                            mReviewData.get(j).getReviewContent());

                    mDb.insert(FavoriteEntry.REVIEW_TABLE_NAME, null, reviewDataToInsert);
                }
            }

            mDb.setTransactionSuccessful();
            return true;
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            mDb.endTransaction();
            mDb.close();
        }

        return false;
    }

    @Override
    public void deliverResult(Boolean data) {
        mDatabaseResult = data;
        super.deliverResult(data);
    }
}
