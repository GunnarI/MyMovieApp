package com.example.android.mymovieapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.Toast;

import com.example.android.mymovieapp.adapters.TrailerAdapter;
import com.example.android.mymovieapp.database.DeleteMovieFromDatabase;
import com.example.android.mymovieapp.database.InsertMovieIntoDatabase;
import com.example.android.mymovieapp.loaders.FetchMovieTrailers;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.example.android.mymovieapp.adapters.TrailerAdapter.TrailerAdapterOnClickHandler;
import com.squareup.picasso.Target;

/**
 * Created by gunnaringi on 2017-02-02.
 */

// TODO : activity_detail.xml has a field with movie duration but to get this we need to make
    //      a new call to the api for a specific movie:
    //      http://api.themoviedb.org/3/movie/movie_id?api_key=my_api_key
    //      Need to decide wheather we should delete the field or make the call

public class DetailActivity extends AppCompatActivity implements
        TrailerAdapterOnClickHandler {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    private MovieData mMovieData;
    private TextView mMovieTitle;

    private ImageView mMovieThumbnail;
    private TextView mMovieDate;
    private ImageView mMovieRatingStar1;
    private ImageView mMovieRatingStar2;
    private ImageView mMovieRatingStar3;
    private ImageView mMovieRatingStar4;
    private ImageView mMovieRatingStar5;
    private TextView mMovieDescription;

    private TrailerAdapter mTrailerAdapter;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicatorForDb;

    private static final int TRAILER_LOADER_ID = 1;
    private static final int INSERT_INTO_DATABASE_LOADER_ID = 2;
    private static final int DELETE_FROM_DATABASE_LOADER_ID = 3;
    private static final int REVIEWS_ACTIVITY_REQUEST_CODE = 101;

    private static final String MOVIE_DETAIL_EXTRA = "MovieDetail";
    private static final String TRAILER_DETAIL_EXTRA = "TrailerDetail";
    private static final String REVIEW_DETAIL_EXTRA = "ReviewDetail";
    private static final String IS_FAVORITE_EXTRA = "IsFavorite";
    private static final String IMG_STORAGE_DIR_EXTRA = "StorageDir";

    private static final String MOVIE_ID_EXTRA = "MovieId";
    private static final String MOVIE_TITLE_EXTRA = "MovieTitle";

    private LoaderCallbacks<ArrayList<TrailerData>> trailersLoaderListener
            = new LoaderCallbacks<ArrayList<TrailerData>>() {
        @Override
        public Loader<ArrayList<TrailerData>> onCreateLoader(int id, Bundle args) {
            return new FetchMovieTrailers(DetailActivity.this,
                    mLoadingIndicator, mMovieData.getId(), mMovieData.getIsFavorite());
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<TrailerData>> loader, ArrayList<TrailerData> data) {
            if (data != null) {
                showTrailersView();
                mTrailerAdapter.setTrailersData(data);
                mTrailerAdapter.notifyDataSetChanged();
                mMovieData.setTrailers(data);
            } else {
                showErrorMessage();
            }
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<TrailerData>> loader) {

        }
    };
    private LoaderCallbacks<Boolean> insertIntoDbLoaderListener
            = new LoaderCallbacks<Boolean>() {
        @Override
        public Loader<Boolean> onCreateLoader(int id, Bundle args) {
            return new InsertMovieIntoDatabase(DetailActivity.this,
                    mLoadingIndicatorForDb, mMovieData);
        }

        @Override
        public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
            mLoadingIndicatorForDb.setVisibility(View.INVISIBLE);
            if (data) {
                Toast.makeText(DetailActivity.this,
                        mMovieData.getTitle() + " has been added to favorites",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DetailActivity.this,
                        mMovieData.getTitle() + " could not be added to favorites",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<Boolean> loader) {

        }
    };
    private LoaderCallbacks<Boolean> deleteFromDbLoaderListener
            = new LoaderCallbacks<Boolean>() {
        @Override
        public Loader<Boolean> onCreateLoader(int id, Bundle args) {
            return new DeleteMovieFromDatabase(DetailActivity.this,
                    mLoadingIndicatorForDb, mMovieData.getId());
        }

        @Override
        public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
            mLoadingIndicatorForDb.setVisibility(View.INVISIBLE);
            if (data) {
                Toast.makeText(DetailActivity.this,
                        mMovieData.getTitle() + " has been removed from favorites",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DetailActivity.this,
                        mMovieData.getTitle() + " could not be removed from favorites",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<Boolean> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentThatStartedThisActivity = getIntent();

        mMovieTitle = (TextView) findViewById(R.id.movie_title);
        mMovieThumbnail = (ImageView) findViewById(R.id.movie_thumbnail);
        mMovieDate = (TextView) findViewById(R.id.movie_rel_date);
        mMovieRatingStar1 = (ImageView) findViewById(R.id.star_icon_1);
        mMovieRatingStar2 = (ImageView) findViewById(R.id.star_icon_2);
        mMovieRatingStar3 = (ImageView) findViewById(R.id.star_icon_3);
        mMovieRatingStar4 = (ImageView) findViewById(R.id.star_icon_4);
        mMovieRatingStar5 = (ImageView) findViewById(R.id.star_icon_5);
        mMovieDescription = (TextView) findViewById(R.id.movie_description);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_trailer_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.trailers_error_message_display);
        mLoadingIndicatorForDb = (ProgressBar) findViewById(R.id.pb_database_loading_indicator);

        if (intentThatStartedThisActivity != null) {
            Bundle extras = intentThatStartedThisActivity.getExtras();

            if (extras.containsKey(MOVIE_DETAIL_EXTRA)) {
                mMovieData = extras.getParcelable(MOVIE_DETAIL_EXTRA);
                mMovieData.setIsFavorite(extras.getBoolean(IS_FAVORITE_EXTRA));

                mMovieTitle.setText(mMovieData.getTitle());

                mMovieDate.setText(mMovieData.getRelYear());
                Double rating = Double.parseDouble(mMovieData.getRating());
                int[] ratingStars = getRatingStars(rating);

                switch (ratingStars[0]) {
                    case 5:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 4:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar5);
                        break;
                    case 3:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 2:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 1:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 0:
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                }

                mMovieDescription.setText(mMovieData.getOverview());

                final FloatingActionButton mFavButton =
                        (FloatingActionButton) findViewById(R.id.favorite_button);

                LinearLayoutManager layoutManager =
                        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layoutManager);
                mTrailerAdapter = new TrailerAdapter(this);

                if (mMovieData.getIsFavorite()) {
                    if (extras.containsKey(TRAILER_DETAIL_EXTRA)) {
                        ArrayList<TrailerData> mTrailerData
                                = extras.getParcelableArrayList(TRAILER_DETAIL_EXTRA);
                        mMovieData.setTrailers(mTrailerData);
                    }
                    if (extras.containsKey(REVIEW_DETAIL_EXTRA)) {
                        ArrayList<ReviewData> mReviewData
                                = extras.getParcelableArrayList(REVIEW_DETAIL_EXTRA);
                        mMovieData.setReviews(mReviewData);
                    }

                    mMovieData.setImgStorageDir(extras.getString(IMG_STORAGE_DIR_EXTRA));

                    Picasso.with(this)
                            .load(new File(mMovieData.getImgStorageDir(),
                                    mMovieData.getImgUrl()))
                            .into(mMovieThumbnail);

                    final Button reviewButton = (Button) findViewById(R.id.review_button);
                    reviewButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Context context = DetailActivity.this;
                            Class destinationClass = ReviewsActivity.class;

                            Intent reviewsIntent = new Intent(context, destinationClass);

                            Bundle reviewExtras = new Bundle();
                            reviewExtras.putString(MOVIE_ID_EXTRA, mMovieData.getId());
                            reviewExtras.putString(MOVIE_TITLE_EXTRA, mMovieData.getTitle());
                            reviewExtras.putParcelableArrayList(REVIEW_DETAIL_EXTRA,
                                    mMovieData.getReviews());

                            reviewsIntent.putExtras(reviewExtras);
                            startActivity(reviewsIntent);
                        }
                    });

                    mFavButton.setImageResource(R.drawable.full_star);

                    mTrailerAdapter.setImgStorageDir(mMovieData.getImgStorageDir());
                } else {
                    Picasso.with(this)
                            .load("http://image.tmdb.org/t/p/w500" + mMovieData.getImgUrl())
                            .into(mMovieThumbnail);

                    final Button reviewButton = (Button) findViewById(R.id.review_button);
                    reviewButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Context context = DetailActivity.this;
                            Class destinationClass = ReviewsActivity.class;

                            Intent reviewsIntent = new Intent(context, destinationClass);

                            Bundle reviewExtras = new Bundle();
                            reviewExtras.putString(MOVIE_ID_EXTRA, mMovieData.getId());
                            reviewExtras.putString(MOVIE_TITLE_EXTRA, mMovieData.getTitle());

                            reviewsIntent.putExtras(reviewExtras);
                            startActivityForResult(reviewsIntent, REVIEWS_ACTIVITY_REQUEST_CODE);
                        }
                    });
                }

                mFavButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (mMovieData.getIsFavorite()) {
                            mMovieData.setIsFavorite(false);
                            mFavButton.setImageResource(R.drawable.empty_star);

                            deleteMovieFromDatabase();
                        } else {
                            mMovieData.setIsFavorite(true);
                            mFavButton.setImageResource(R.drawable.full_star);

                            insertMovieIntoDatabase();
                        }
                    }
                });


                mRecyclerView.setAdapter(mTrailerAdapter);

                loadTrailerData();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == REVIEWS_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ArrayList<ReviewData> reviewsData =
                        data.getParcelableArrayListExtra("ReviewExtra");

                mMovieData.setReviews(reviewsData);
            }
        }
    }

    @Override
    public void onClick(String trailerClicked) {
        Uri youtubeUrl = Uri.parse("http://www.youtube.com/watch?v=" + trailerClicked);
        Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUrl);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void loadTrailerData() {
        int loaderId = TRAILER_LOADER_ID;
        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, trailersLoaderListener);
    }

    private void showTrailersView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public int[] getRatingStars(Double rating) {
        int[] ratingStars = new int[2];
        Double ratingRem = (rating % 2.0) / 2;
        Double ratingBase = rating / 2 - ratingRem;
        ratingStars[0] = ratingBase.intValue();

        Double ratingRemRem = ratingRem % 0.25;
        Double ratingRemBase = ratingRem / 0.25 - ratingRemRem;
        int partialStar = ratingRemBase.intValue();

        switch (partialStar) {
            case 0:
                ratingStars[1] = R.drawable.empty_star;
                break;
            case 1:
                ratingStars[1] = R.drawable.quarter_star;
                break;
            case 2:
                ratingStars[1] = R.drawable.half_star;
                break;
            case 3:
                ratingStars[1] = R.drawable.three_quarter_star;
                break;
            default:
                break;
        }

        return ratingStars;
    }

    public void insertMovieIntoDatabase() {

        final String posterUrl = "http://image.tmdb.org/t/p/w500"
                + mMovieData.getImgUrl();

        Picasso.with(DetailActivity.this)
                .load(posterUrl)
                .into(saveImageToStorage(mMovieData.getImgUrl()));
        if (mMovieData.getTrailers() != null) {
            ArrayList<TrailerData> trailerDatas = mMovieData.getTrailers();
            for (int t = 0; t < trailerDatas.size(); t++) {
                String trailerUrl = "http://img.youtube.com/vi/"
                        + trailerDatas.get(t).getTrailerUrl()
                        + "/0.jpg";
                Picasso.with(DetailActivity.this)
                        .load(trailerUrl)
                        .into(saveImageToStorage("/" +
                                trailerDatas.get(t).getTrailerUrl() + ".jpg"));
            }
        }

        int loaderId = INSERT_INTO_DATABASE_LOADER_ID;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, insertIntoDbLoaderListener);
    }

    public void deleteMovieFromDatabase() {
        File posterImg = new File(mMovieData.getImgStorageDir(), mMovieData.getImgUrl());
        boolean posterImgDeleted = posterImg.delete();
        boolean trailerImgDeleted = true;

        if (mMovieData.getTrailers() != null) {
            ArrayList<TrailerData> trailerDatas = mMovieData.getTrailers();
            for (int t = 0; t < trailerDatas.size(); t++) {
                File trailerImg = new File(mMovieData.getImgStorageDir(),
                        trailerDatas.get(t).getTrailerUrl() + ".jpg");

                if (!trailerImg.delete()) {
                    trailerImgDeleted = false;
                }
            }
        }
        if (posterImgDeleted && trailerImgDeleted) {
            Log.i(LOG_TAG, "Images successfully deleted");
        } else {
            Log.i(LOG_TAG, "Images not deleted for some reason");
        }

        int loaderId = DELETE_FROM_DATABASE_LOADER_ID;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, deleteFromDbLoaderListener);
    }

    private Target saveImageToStorage(final String url) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ContextWrapper cw = new ContextWrapper(DetailActivity.this);
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File mypath=new File(directory, url);

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                mMovieData.setImgStorageDir(directory.getAbsolutePath());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }
}
