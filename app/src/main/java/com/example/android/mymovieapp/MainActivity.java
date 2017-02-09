package com.example.android.mymovieapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import com.example.android.mymovieapp.PosterAdapter.PosterAdapterOnClickHandler;

public class MainActivity extends AppCompatActivity
        implements PosterAdapterOnClickHandler {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private int spanCount = 2;
    private PosterAdapter mPosterAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mPosterAdapter = new PosterAdapter(this);
        mRecyclerView.setAdapter(mPosterAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.poster_error_message_display);

        loadMovieData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 1001);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            if(resultCode == Activity.RESULT_OK) {
                loadMovieData();
            }
        }
    }

    private void loadMovieData() {
        SharedPreferences prefsLoc = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String orderby = prefsLoc.getString(
                getString(R.string.orderby_key),
                getString(R.string.pref_orderby_pop));

        if (orderby.equals(getString(R.string.pref_orderby_rate))) {
            orderby = "top_rated";
        } else if (orderby.equals(getString(R.string.pref_orderby_pop))) {
            orderby = "popular";
        }
        showMoviePosterView();
        new FetchMoviesTask().execute(orderby);
    }

    @Override
    public void onClick(MovieData movieClicked) {
        Context context = this;
        Class destinationClass = DetailActivity.class;

        Intent detailIntent = new Intent(context, destinationClass);
        detailIntent.putExtra("MovieDetail",movieClicked);
        startActivity(detailIntent);
    }

    private void showMoviePosterView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieData>> {


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
                final String FILM_SOURCE = params[0] + "?";
                final String API = "api_key=" + BuildConfig.TMDB_API_KEY;

                String urlString = FILM_BASE_URL + FILM_SOURCE + API;
                URL url = new URL((String) urlString);

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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (result != null) {
                showMoviePosterView();
                mPosterAdapter.setMovieData(result);
                mPosterAdapter.notifyDataSetChanged();
            } else {
                showErrorMessage();
            }
        }
    }
}
