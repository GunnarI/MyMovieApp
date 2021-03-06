package com.example.android.mymovieapp.database;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.mymovieapp.BuildConfig;
import com.example.android.mymovieapp.MovieData;
import com.example.android.mymovieapp.ReviewData;
import com.example.android.mymovieapp.TrailerData;
import com.example.android.mymovieapp.database.FavoriteContract.FavoriteEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by gunnaringi on 2017-03-10.
 */

public class InsertMovieIntoDatabase extends AsyncTaskLoader<Boolean> {

    private final String LOG_TAG = InsertMovieIntoDatabase.class.getSimpleName();
    private Context context;
    private MovieData mMovieData;
    private Boolean mDatabaseResult;
    private ProgressBar mLoadingIndicator;

    public InsertMovieIntoDatabase(Context context,
                                   ProgressBar mLoadingIndicator, MovieData movieData) {
        super(context);
        this.context = context;
        this.mMovieData = movieData;
        this.mLoadingIndicator = mLoadingIndicator;
    }

    @Override
    protected void onStartLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        forceLoad();
    }

    @Override
    public Boolean loadInBackground() {
        if (mMovieData.getReviews() == null) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewJsonStr = null;

            try {
                final String FILM_BASE_URL = "http://api.themoviedb.org/3/movie/";

                Uri builtUri = Uri.parse(FILM_BASE_URL).buildUpon()
                        .appendPath(mMovieData.getId())
                        .appendPath("reviews")
                        .appendQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                reviewJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                mMovieData.setReviews(getReviewDataFromJson(reviewJsonStr));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }

        try {
            ContentValues movieDataToInsert = new ContentValues();
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_ID, mMovieData.getId());
            movieDataToInsert.put(FavoriteEntry.COLUMN_IMAGE_STORAGE_DIR,
                    mMovieData.getImgStorageDir());
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_POSTER_URL, mMovieData.getImgUrl());
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_TITLE, mMovieData.getTitle());
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_OVERVIEW, mMovieData.getOverview());
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_RATING, mMovieData.getRating());
            movieDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE, mMovieData.getRelDate());

            Uri movieUri = context.getContentResolver()
                    .insert(FavoriteEntry.MOVIE_CONTENT_URI, movieDataToInsert);

            Uri trailerUri = null;
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

                    trailerUri = context.getContentResolver()
                            .insert(FavoriteEntry.TRAILER_CONTENT_URI, trailerDataToInsert);
                }
            }

            Uri reviewUri = null;
            ArrayList<ReviewData> mReviewData = mMovieData.getReviews();
            if (mTrailerData != null) {
                for(int j = 0; j < mReviewData.size(); j++) {
                    ContentValues reviewDataToInsert = new ContentValues();
                    reviewDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_ID, mMovieData.getId());
                    reviewDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_REVIEW_AUTHOR,
                            mReviewData.get(j).getReviewAuthor());
                    reviewDataToInsert.put(FavoriteEntry.COLUMN_MOVIE_REVIEW_CONTENT,
                            mReviewData.get(j).getReviewContent());

                    reviewUri = context.getContentResolver()
                            .insert(FavoriteEntry.REVIEW_CONTENT_URI, reviewDataToInsert);
                }
            }

            if (movieUri != null && trailerUri != null && reviewUri != null) {
                return true;
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        return false;
    }

    @Override
    public void deliverResult(Boolean data) {
        mDatabaseResult = data;
        super.deliverResult(data);
    }

    public ArrayList<ReviewData> getReviewDataFromJson(String reviewJsonStr) throws JSONException {
        final String REVIEWS_LIST = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        JSONObject reviewsJson = new JSONObject(reviewJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(REVIEWS_LIST);

        ArrayList<ReviewData> reviewsData = new ArrayList<>();
        for (int i = 0; i < reviewsArray.length(); i++) {
            ReviewData reviewData = new ReviewData(
                    reviewsArray.getJSONObject(i).getString(REVIEW_AUTHOR),
                    reviewsArray.getJSONObject(i).getString(REVIEW_CONTENT)
            );
            reviewsData.add(reviewData);
        }

        return reviewsData;
    }
}
