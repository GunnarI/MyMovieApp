package com.example.android.mymovieapp.loaders;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.mymovieapp.BuildConfig;
import com.example.android.mymovieapp.ReviewData;
import com.example.android.mymovieapp.ReviewsActivity;
import com.example.android.mymovieapp.database.FavoriteContract;
import com.example.android.mymovieapp.database.FavoriteDbHelper;

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
 * Created by gunnaringi on 2017-03-07.
 */

public class FetchMovieReviews extends AsyncTaskLoader<ArrayList<ReviewData>> {
    private final String LOG_TAG = ReviewsActivity.class.getSimpleName();

    ArrayList<ReviewData> mReviewsData = null;
    private Context context;
    private ProgressBar mLoadingIndicator;
    private String movieId;
    private Boolean isFavorite;

    private SQLiteDatabase mDb;

    public FetchMovieReviews(Context context, ProgressBar mLoadingIndicator, String movieId,
                             Boolean isFavorite) {
        super(context);
        this.context = context;
        this.mLoadingIndicator = mLoadingIndicator;
        this.movieId = movieId;
        this.isFavorite = isFavorite;
    }

    @Override
    protected void onStartLoading() {
        if (mReviewsData != null) {
            deliverResult(mReviewsData);
        } else {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            forceLoad();
        }
    }

    @Override
    public ArrayList<ReviewData> loadInBackground() {
        if (isFavorite) {
            Cursor cursor = null;
            try {
                FavoriteDbHelper dbHelper = new FavoriteDbHelper(context);
                mDb = dbHelper.getReadableDatabase();

                String reviewQuery = "SELECT * FROM " +
                        FavoriteContract.FavoriteEntry.REVIEW_TABLE_NAME +
                        " WHERE " + FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID +
                        "=" + movieId;

                Cursor reviewCursor = mDb.rawQuery(reviewQuery, null);

                ArrayList<ReviewData> reviewDatas = new ArrayList<>();

                while (reviewCursor.moveToNext()) {
                    ReviewData reviewData = new ReviewData(
                            reviewCursor.getString(reviewCursor.getColumnIndex(
                                    FavoriteContract.FavoriteEntry.COLUMN_MOVIE_REVIEW_AUTHOR)),
                            reviewCursor.getString(reviewCursor.getColumnIndex(
                                    FavoriteContract.FavoriteEntry.COLUMN_MOVIE_REVIEW_CONTENT))
                    );

                    reviewDatas.add(reviewData);
                }

                return reviewDatas;
            } catch (SQLiteException e) {
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (mDb != null) {
                    mDb.close();
                }
            }
        } else {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewJsonStr = null;

            try {
                final String FILM_BASE_URL = "http://api.themoviedb.org/3/movie/";

                Uri builtUri = Uri.parse(FILM_BASE_URL).buildUpon()
                        .appendPath(movieId)
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
                return getReviewDataFromJson(reviewJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void deliverResult(ArrayList<ReviewData> data) {
        mReviewsData = data;
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
