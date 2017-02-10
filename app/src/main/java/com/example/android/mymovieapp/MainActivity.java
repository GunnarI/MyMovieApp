package com.example.android.mymovieapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

import com.example.android.mymovieapp.PosterAdapter.PosterAdapterOnClickHandler;
import com.example.android.mymovieapp.utilities.AsyncTaskCompleteListener;

public class MainActivity extends AppCompatActivity
        implements PosterAdapterOnClickHandler {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private int spanCount = 2;
    private PosterAdapter mPosterAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

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
        new FetchMoviesTask(this,
                new FetchMoviesTaskCompleteListener(),
                mLoadingIndicator).execute(orderby);
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

    public class FetchMoviesTaskCompleteListener
            implements AsyncTaskCompleteListener<ArrayList<MovieData>> {
        @Override
        public void onTaskComplete(ArrayList<MovieData> result) {
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
