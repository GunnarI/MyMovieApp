package com.example.android.mymovieapp.loaders;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.mymovieapp.BuildConfig;
import com.example.android.mymovieapp.MainActivity;
import com.example.android.mymovieapp.MovieData;
import com.example.android.mymovieapp.ReviewData;
import com.example.android.mymovieapp.TrailerData;
import com.example.android.mymovieapp.database.FavoriteContract.FavoriteEntry;
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
 * Created by gunnaringi on 2017-02-10.
 */

public class FetchMoviesTask extends AsyncTaskLoader<ArrayList<MovieData>> {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    ArrayList<MovieData> mMovieData = null;
    private ProgressBar mLoadingIndicator;
    private Context context;
    private String orderby;

    private SQLiteDatabase mDb;

    public FetchMoviesTask(Context context, ProgressBar mLoadingIndicator, String orderby) {
        super(context);
        this.context = context;
        this.mLoadingIndicator = mLoadingIndicator;
        this.orderby = orderby;
    }

    @Override
    protected void onStartLoading() {
        if (mMovieData != null) {
            deliverResult(mMovieData);
        } else {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            forceLoad();
        }
    }

    @Override
    public ArrayList<MovieData> loadInBackground() {
        if (orderby == "my_favorite") {
            try {
                return getmMovieDataFromDatabase(context.getContentResolver()
                        .query(FavoriteEntry.MOVIE_CONTENT_URI,
                                null,
                                null,
                                null,
                                FavoriteEntry._ID)
                );
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
            }

            return null;
        } else {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {
                final String FILM_BASE_URL = "http://api.themoviedb.org/3/movie/";

                Uri builtUri = Uri.parse(FILM_BASE_URL).buildUpon()
                        .appendPath(orderby)
                        .appendQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(10000);

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
    }

    @Override
    public void deliverResult(ArrayList<MovieData> data) {
        mMovieData = data;
        super.deliverResult(data);
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

        ArrayList<MovieData> movieDatas = new ArrayList<>();
        for(int i=0; i < movieArray.length(); i++) {
            MovieData movieData = new MovieData(
                    movieArray.getJSONObject(i).getString(FILM_POSTER),
                    movieArray.getJSONObject(i).getString(FILM_TITLE),
                    movieArray.getJSONObject(i).getString(FILM_DESC),
                    movieArray.getJSONObject(i).getString(FILM_RATING),
                    movieArray.getJSONObject(i).getString(FILM_RELEASE_DATE),
                    movieArray.getJSONObject(i).getString(FILM_ID)
            );

            movieDatas.add(movieData);
        }

        return movieDatas;
    }

    private ArrayList<MovieData> getmMovieDataFromDatabase(Cursor movieCursor)
            throws SQLiteException{

        ArrayList<MovieData> movieDatas = new ArrayList<>();

        try {
            while (movieCursor.moveToNext()) {
                MovieData movieData = new MovieData(
                        movieCursor.getString(movieCursor.getColumnIndex(
                                FavoriteEntry.COLUMN_MOVIE_POSTER_URL)),
                        movieCursor.getString(movieCursor.getColumnIndex(
                                FavoriteEntry.COLUMN_MOVIE_TITLE)),
                        movieCursor.getString(movieCursor.getColumnIndex(
                                FavoriteEntry.COLUMN_MOVIE_OVERVIEW)),
                        movieCursor.getString(movieCursor.getColumnIndex(
                                FavoriteEntry.COLUMN_MOVIE_RATING)),
                        movieCursor.getString(movieCursor.getColumnIndex(
                                FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE)),
                        movieCursor.getString(movieCursor.getColumnIndex(
                                FavoriteEntry.COLUMN_MOVIE_ID))
                );

                movieData.setImgStorageDir(movieCursor
                        .getString(movieCursor.getColumnIndex(
                                FavoriteEntry.COLUMN_IMAGE_STORAGE_DIR)));

                movieData.setIsFavorite(true);

                movieDatas.add(movieData);
            }
        } finally {
            movieCursor.close();
        }

        return movieDatas;
    }
}
