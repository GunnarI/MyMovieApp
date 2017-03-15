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

    private ArrayList<String> movieIdToRemoveFav = new ArrayList<>();
    private ArrayList<String> movieIdToToggleFav = new ArrayList<>();
    private String mOrderby;
    private static final String ORDER_BY_TOP_RATED = "top_rated";
    private static final String ORDER_BY_POPULAR = "popular";
    private static final String ORDER_BY_MY_FAVORITE = "my_favorite";

    private static final int POSTER_LOADER_ID = 0;

    private static final String MOVIE_DETAIL_EXTRA = "MovieDetail";
    private static final String IS_FAVORITE_EXTRA = "IsFavorite";
    private static final String IMG_STORAGE_DIR_EXTRA = "StorageDir";

    private static final String ORDER_BY_EXTRA = "orderByKey";
    private static final String MOVIE_CHANGED_EXTRA = "movieChanged";
    private int movieRemovedPosition = -1;
    private int movieUnfavoritedPosition = -1;

    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 11;

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
        LoaderCallbacks<ArrayList<MovieData>> callbacks = MainActivity.this;

        showMoviePosterView();

        getSupportLoaderManager().initLoader(loaderId, null, callbacks);
    }

    @Override
    public Loader<ArrayList<MovieData>> onCreateLoader(int id, Bundle args) {
        SharedPreferences prefsLoc = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mOrderby = prefsLoc.getString(
                getString(R.string.orderby_key),
                getString(R.string.pref_orderby_pop));

        if (mOrderby.equals(getString(R.string.pref_orderby_rate))) {
            mOrderby = ORDER_BY_TOP_RATED;
        } else if (mOrderby.equals(getString(R.string.pref_orderby_pop))) {
            mOrderby = ORDER_BY_POPULAR;
        } else if (mOrderby.equals(getString(R.string.pref_orderby_fav))) {
            mOrderby = ORDER_BY_MY_FAVORITE;
        }

        return new FetchMoviesTask(MainActivity.this, mLoadingIndicator, mOrderby);
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
        if (!mOrderby.equals(ORDER_BY_MY_FAVORITE)) {
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

        if (movieClicked.getIsFavorite()) {
            startActivityForResult(detailIntent, DETAIL_ACTIVITY_REQUEST_CODE);
        } else {
            startActivity(detailIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == DETAIL_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final String movieId = data.getStringExtra("DetailExtra");

                if (mOrderby.equals(ORDER_BY_MY_FAVORITE)) {
                    movieRemovedPosition = mPosterAdapter.removeMovieFromList(movieId);
                    if (movieRemovedPosition >= 0) {
                        movieIdToRemoveFav.add(movieId);
                        mPosterAdapter.notifyItemRemoved(movieRemovedPosition);
                    }
                } else {
                    movieUnfavoritedPosition = mPosterAdapter.setMovieFavValue(movieId, false);
                    if (movieUnfavoritedPosition >= 0) {
                        movieIdToToggleFav.add(movieId);
                        mPosterAdapter.notifyItemChanged(movieUnfavoritedPosition);
                    }
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOrderby = savedInstanceState.getString(ORDER_BY_EXTRA);
        if (mOrderby.equals(ORDER_BY_MY_FAVORITE)) {
            movieIdToRemoveFav = savedInstanceState.getStringArrayList(MOVIE_CHANGED_EXTRA);
            if (movieIdToRemoveFav != null) {
                for (String movieId : movieIdToRemoveFav) {
                    movieRemovedPosition = mPosterAdapter.removeMovieFromList(movieId);
                    if (movieRemovedPosition >= 0) {
                        mPosterAdapter.notifyItemRemoved(movieRemovedPosition);
                    }
                }
            }
        } else {
            movieIdToToggleFav = savedInstanceState.getStringArrayList(MOVIE_CHANGED_EXTRA);
            if (movieIdToToggleFav != null) {
                for (String movieId : movieIdToToggleFav) {
                    movieUnfavoritedPosition = mPosterAdapter.setMovieFavValue(movieId, false);
                    if (movieUnfavoritedPosition >= 0) {
                        mPosterAdapter.notifyItemChanged(movieUnfavoritedPosition);
                    }
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mOrderby.equals(ORDER_BY_MY_FAVORITE)) {
            outState.putStringArrayList(MOVIE_CHANGED_EXTRA, movieIdToRemoveFav);
        } else {
            outState.putStringArrayList(MOVIE_CHANGED_EXTRA, movieIdToToggleFav);
        }
        outState.putString(ORDER_BY_EXTRA, mOrderby);
        super.onSaveInstanceState(outState);
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
