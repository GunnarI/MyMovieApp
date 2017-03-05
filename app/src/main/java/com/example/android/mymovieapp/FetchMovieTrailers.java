package com.example.android.mymovieapp;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
import java.util.ConcurrentModificationException;

/**
 * Created by gunnaringi on 2017-03-05.
 */

public class FetchMovieTrailers extends AsyncTaskLoader<ArrayList<String[]>>{
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    ArrayList<String[]> mTrailerData = null;
    private ProgressBar mLoadingIndicator;
    private Context context;
    private String movieId;

    public FetchMovieTrailers(Context context, ProgressBar mLoadingIndicator, String movieId) {
        super(context);
        this.context = context;
        this.mLoadingIndicator = mLoadingIndicator;
        this.movieId = movieId;
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
    public void deliverResult(ArrayList<String[]> data) {
        mTrailerData = data;
        super.deliverResult(data);
    }

    @Override
    public ArrayList<String[]> loadInBackground() {
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
        return null;
    }

    /**
     * @param trailerJsonStr contains the JSON string from TMDB API
     * @return ArrayList<String[]> each string array contains
     *          (trailer name, youtube source string, trailer type)
     * @throws JSONException
     */
    private ArrayList<String[]> getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
        final String TRAILER_LIST = "youtube";
        final String TRAILER_NAME = "name";
        final String TRAILER_SOURCE = "source";
        final String TRAILER_TYPE = "type";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_LIST);

        ArrayList<String[]> trailersData = new ArrayList<>();
        for(int i = 0; i < trailerArray.length(); i++) {
            String[] trailerData = new String[]{
                    trailerArray.getJSONObject(i).getString(TRAILER_NAME),
                    trailerArray.getJSONObject(i).getString(TRAILER_SOURCE),
                    trailerArray.getJSONObject(i).getString(TRAILER_TYPE)
            };
            trailersData.add(trailerData);
        }

        return trailersData;
    }
}
