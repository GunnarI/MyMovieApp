package com.example.android.mymovieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.example.android.mymovieapp.adapters.PosterAdapter;
import com.example.android.mymovieapp.adapters.PosterAdapter.PosterAdapterOnClickHandler;
import com.example.android.mymovieapp.database.FavoriteContract;
import com.example.android.mymovieapp.database.FavoriteDbHelper;
import com.example.android.mymovieapp.loaders.FetchMoviesTask;

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

    private Boolean mIsFavorite = false;

    private static final int POSTER_LOADER_ID = 0;

    private static final String MOVIE_DETAIL_EXTRA = "MovieDetail";
    private static final String TRAILER_DETAIL_EXTRA = "TrailerDetail";
    private static final String REVIEW_DETAIL_EXTRA = "ReviewDetail";
    private static final String IS_FAVORITE_EXTRA = "IsFavorite";
    private static final String IMG_STORAGE_DIR_EXTRA = "StorageDir";

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
            int loaderId = POSTER_LOADER_ID;

            getSupportLoaderManager().restartLoader(loaderId, null, this);
        }
    }

    private void loadMovieData() {
        int loaderId = POSTER_LOADER_ID;
        Bundle bundleForLoader = null;
        LoaderCallbacks<ArrayList<MovieData>> callbacks = MainActivity.this;

        showMoviePosterView();

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callbacks);
    }

    @Override
    public Loader<ArrayList<MovieData>> onCreateLoader(int id, Bundle args) {
        SharedPreferences prefsLoc = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String orderby = prefsLoc.getString(
                getString(R.string.orderby_key),
                getString(R.string.pref_orderby_pop));

        ArrayList<MovieData> mMovieData = null;

        if (orderby.equals(getString(R.string.pref_orderby_rate))) {
            orderby = "top_rated";
        } else if (orderby.equals(getString(R.string.pref_orderby_pop))) {
            orderby = "popular";
        } else if (orderby.equals(getString(R.string.pref_orderby_fav))) {
            orderby = "my_favorite";
        }

        return new FetchMoviesTask(MainActivity.this, mLoadingIndicator, orderby);
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

    @Override
    public void onClick(MovieData movieClicked) {
        Context context = this;
        Class destinationClass = DetailActivity.class;

        Intent detailIntent = new Intent(context, destinationClass);

        // If we are using top rated or popular as orderby then the movie is not marked as favorite
        // so we will have to search the db to see if it is actually favorited.
        if (!movieClicked.getIsFavorite()) {
            String imgSource = checkIfInDb(movieClicked.getId());
            if(imgSource != null) {
                movieClicked.setIsFavorite(true);
                movieClicked.setImgStorageDir(imgSource);
            }
        }

        Bundle extras = new Bundle();
        extras.putParcelable(MOVIE_DETAIL_EXTRA, movieClicked);
        extras.putBoolean(IS_FAVORITE_EXTRA, movieClicked.getIsFavorite());

        // If the movie is a favorite then we send the extra data needed to setup the detailed view
        if (movieClicked.getIsFavorite()) {
            extras.putString(IMG_STORAGE_DIR_EXTRA, movieClicked.getImgStorageDir());
        }

        detailIntent.putExtras(extras);

        startActivity(detailIntent);
    }

    private void showMoviePosterView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    public String checkIfInDb(String movieId) {
        SQLiteDatabase mDb = null;
        Cursor cursor = null;

        try {
            FavoriteDbHelper dbHelper = new FavoriteDbHelper(this);
            mDb = dbHelper.getReadableDatabase();

            String query = "SELECT * FROM " + FavoriteContract.FavoriteEntry.MOVIE_TABLE_NAME +
                    " WHERE " + FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID +
                    " = " + movieId + ";";
            cursor = mDb.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            }

            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(
                                FavoriteContract.FavoriteEntry.COLUMN_IMAGE_STORAGE_DIR));
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            cursor.close();
            mDb.close();
        }

        return null;
    }
}
