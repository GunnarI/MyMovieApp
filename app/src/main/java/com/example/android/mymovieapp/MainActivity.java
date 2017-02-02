package com.example.android.mymovieapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MainActivity extends AppCompatActivity
        implements PosterAdapter.PosterAdapterOnClickHandler {

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
        setMovieDummyDataToAdapter();
    }

    private void showMoviePosterView() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String movieClicked) {
        // TODO: put intent with movie title as extra info to setup the detailed view
    }

    // TODO: This function should be replaced with an AsyncTask that gets real data from the web
    public void setMovieDummyDataToAdapter() {
        String[] dummyMovieTitles = new String[20];
        for (int i = 0; i < dummyMovieTitles.length; i++) {
            dummyMovieTitles[i] = "Title " + i;
        }
        mPosterAdapter.setMovieData(dummyMovieTitles);
    }
}
