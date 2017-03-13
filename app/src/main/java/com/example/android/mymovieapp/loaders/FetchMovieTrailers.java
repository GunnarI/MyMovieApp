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
import com.example.android.mymovieapp.DetailActivity;
import com.example.android.mymovieapp.MovieData;
import com.example.android.mymovieapp.ReviewData;
import com.example.android.mymovieapp.TrailerData;
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
 * Created by gunnaringi on 2017-03-05.
 */

public class FetchMovieTrailers extends AsyncTaskLoader<ArrayList<TrailerData>>{
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    ArrayList<TrailerData> mTrailerData = null;
    private ProgressBar mLoadingIndicator;
    private Context context;
    private String movieId;
    private Boolean isFavorite;

    private SQLiteDatabase mDb;

    public FetchMovieTrailers(Context context, ProgressBar mLoadingIndicator, String movieId,
                              Boolean isFavorite) {
        super(context);
        this.context = context;
        this.mLoadingIndicator = mLoadingIndicator;
        this.movieId = movieId;
        this.isFavorite = isFavorite;
    }

    @Override
    protected void onStartLoading() {
        if (mTrailerData != null) {
            deliverResult(mTrailerData);
        } else {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            forceLoad();
        }
    }

    @Override
    public void deliverResult(ArrayList<TrailerData> data) {
        mTrailerData = data;
        super.deliverResult(data);
    }

    @Override
    public ArrayList<TrailerData> loadInBackground() {
        if (isFavorite) {
            Cursor trailerCursor = null;
            try {
                FavoriteDbHelper dbHelper = new FavoriteDbHelper(context);
                mDb = dbHelper.getReadableDatabase();
                String trailerQuery = "SELECT * FROM " +
                        FavoriteContract.FavoriteEntry.TRAILER_TABLE_NAME +
                        " WHERE " + FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID +
                        "=" + movieId;

                trailerCursor = mDb.rawQuery(trailerQuery, null);

                ArrayList<TrailerData> trailerDatas = new ArrayList<>();

                while (trailerCursor.moveToNext()) {
                    TrailerData trailerData = new TrailerData(
                            trailerCursor.getString(trailerCursor.getColumnIndex(
                                    FavoriteContract.FavoriteEntry.COLUMN_MOVIE_TRAILER_TITLE)),
                            trailerCursor.getString(trailerCursor.getColumnIndex(
                                    FavoriteContract.FavoriteEntry.COLUMN_MOVIE_TRAILER_URL)),
                            trailerCursor.getString(trailerCursor.getColumnIndex(
                                    FavoriteContract.FavoriteEntry.COLUMN_MOVIE_TRAILER_TYPE))
                    );

                    trailerDatas.add(trailerData);
                }

                return trailerDatas;
            } catch (SQLiteException e) {
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                if (trailerCursor != null) {
                    trailerCursor.close();
                }
                if (mDb != null) {
                    mDb.close();
                }
            }
        } else {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String trailerJsonStr = null;

            try {
                final String FILM_BASE_URL = "http://api.themoviedb.org/3/movie/";

                Uri builtUri = Uri.parse(FILM_BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath("trailers")
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

                trailerJsonStr = buffer.toString();
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
                return getTrailerDataFromJson(trailerJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }

        return null;
    }

    private ArrayList<TrailerData> getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
        final String TRAILER_LIST = "youtube";
        final String TRAILER_NAME = "name";
        final String TRAILER_SOURCE = "source";
        final String TRAILER_TYPE = "type";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_LIST);

        ArrayList<TrailerData> trailersData = new ArrayList<>();
        for(int i = 0; i < trailerArray.length(); i++) {
            TrailerData trailerData = new TrailerData(
                    trailerArray.getJSONObject(i).getString(TRAILER_NAME),
                    trailerArray.getJSONObject(i).getString(TRAILER_SOURCE),
                    trailerArray.getJSONObject(i).getString(TRAILER_TYPE)
            );
            trailersData.add(trailerData);
        }

        return trailersData;
    }
}
