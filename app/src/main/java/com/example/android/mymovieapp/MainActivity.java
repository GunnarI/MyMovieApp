package com.example.android.mymovieapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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

public class MainActivity extends AppCompatActivity
        implements PosterAdapter.PosterAdapterOnClickHandler {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private int spanCount = 2; // TODO : Check how it's best to implement the spancount
    private PosterAdapter mPosterAdapter;

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

        loadMovieData();
    }

    private void loadMovieData() {
        showMoviePosterView();
        //setMovieDummyDataToAdapter();
        new FetchMoviesTask().execute("popular");
    }

    private void showMoviePosterView() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String movieClicked) {
        // TODO: put intent with movie title as extra info to setup the detailed view
        Context context = this;
        Class destinationClass = DetailActivity.class;
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
            if (result != null) {
                mPosterAdapter.setMovieData(result);
                mPosterAdapter.notifyDataSetChanged();
            }
        }
    }
}
