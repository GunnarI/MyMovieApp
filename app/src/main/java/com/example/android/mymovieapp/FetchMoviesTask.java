package com.example.android.mymovieapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.mymovieapp.utilities.AsyncTaskCompleteListener;

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
 * Created by gunnaringi on 2017-02-10.
 */

public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieData>> {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private Context context;
    private AsyncTaskCompleteListener<ArrayList<MovieData>> listener;
    private ProgressBar mLoadingIndicator;

    public FetchMoviesTask(Context context,
                           AsyncTaskCompleteListener<ArrayList<MovieData>> listener,
                           ProgressBar mLoadingIndicator)
    {
        this.context = context;
        this.listener = listener;
        this.mLoadingIndicator = mLoadingIndicator;
    }

    private ArrayList<MovieData> getMovieDataFromJson(String movieJsonStr) throws JSONException {
        // Things to get from TMDb:
        final String FILM_LIST = "results";
        final String FILM_POSTER = "poster_path";
        final String FILM_DESC = "overview";
        final String FILM_RELEASE_DATE = "release_date";
        final String FILM_GENRE = "genre_ids";
        final String FILM_TITLE = "title";
        final String FILM_POPULAR_GRADE = "popularity";
        final String FILM_RATING = "vote_average";
        final String FILM_ID = "id";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(FILM_LIST);

        ArrayList<MovieData> movieDatas = new ArrayList<MovieData>();
        for(int i=0; i < movieArray.length(); i++) {
            MovieData movieData = new MovieData();
            movieData.setImgUrl(movieArray.getJSONObject(i).getString(FILM_POSTER));
            movieData.setTitle(movieArray.getJSONObject(i).getString(FILM_TITLE));
            movieData.setOverview(movieArray.getJSONObject(i).getString(FILM_DESC));
            movieData.setRating(movieArray.getJSONObject(i).getString(FILM_RATING));
            movieData.setRelDate(movieArray.getJSONObject(i).getString(FILM_RELEASE_DATE));
            movieData.setId(movieArray.getJSONObject(i).getString(FILM_ID));

            movieDatas.add(movieData);
        }

        return movieDatas;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<MovieData> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;

        try {
            final String FILM_BASE_URL = "http://api.themoviedb.org/3/movie/";

            Uri builtUri = Uri.parse(FILM_BASE_URL).buildUpon()
                    .appendPath(params[0])
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

            movieJsonStr = buffer.toString();
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
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieData> result) {
        super.onPostExecute(result);
        listener.onTaskComplete(result);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }
}
