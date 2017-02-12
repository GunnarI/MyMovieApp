package com.example.android.mymovieapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.android.mymovieapp.PosterAdapter.PosterAdapterOnClickHandler;
import com.example.android.mymovieapp.utilities.AsyncTaskCompleteListener;

public class MainActivity extends AppCompatActivity implements
        PosterAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderCallbacks<ArrayList<MovieData>> {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private int spanCount = 2;
    private PosterAdapter mPosterAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final int POSTER_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        GridLayoutManager layoutManager
                = new GridLayoutManager(
                    this,
                    calculateNoOfColumns(getBaseContext()), // TODO : Check for better ways to calculate so that each row is filled
                    GridLayoutManager.VERTICAL,
                    false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mPosterAdapter = new PosterAdapter(this);
        mRecyclerView.setAdapter(mPosterAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.poster_error_message_display);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        loadMovieData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.orderby_key))) {
            loadMovieData();
            int loaderId = POSTER_LOADER_ID;

            getSupportLoaderManager().restartLoader(loaderId, null, this);
        }
    }

    private void loadMovieData() {
        int loaderId = POSTER_LOADER_ID;
        LoaderCallbacks<ArrayList<MovieData>> callbacks = MainActivity.this;
        Bundle bundleForLoader = null;

        showMoviePosterView();

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callbacks);
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

    @Override
    public Loader<ArrayList<MovieData>> onCreateLoader(int id, Bundle args) {
        SharedPreferences prefsLoc = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String orderby = prefsLoc.getString(
                getString(R.string.orderby_key),
                getString(R.string.pref_orderby_pop));

        if (orderby.equals(getString(R.string.pref_orderby_rate))) {
            orderby = "top_rated";
        } else if (orderby.equals(getString(R.string.pref_orderby_pop))) {
            orderby = "popular";
        }

        return new FetchMoviesTask(this, mLoadingIndicator, orderby);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieData>> loader, ArrayList<MovieData> data) {
        if (data != null) {
            showMoviePosterView();
            mPosterAdapter.setMovieData(data);
            mPosterAdapter.notifyDataSetChanged();
        } else {
            showErrorMessage();
        }
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieData>> loader) {

    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
