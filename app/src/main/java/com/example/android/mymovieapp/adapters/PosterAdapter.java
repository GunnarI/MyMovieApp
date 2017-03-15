package com.example.android.mymovieapp.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.mymovieapp.DetailActivity;
import com.example.android.mymovieapp.MovieData;
import com.example.android.mymovieapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by gunnaringi on 2017-02-01.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder> {

    private ArrayList<MovieData> mMoviesData;
    private final PosterAdapterOnClickHandler mClickHandler;
    private final String[] posterSizes = new String[]{
            "w92", "w154", "w185", "w342", "w500", "w780", "original"
    };

    public interface PosterAdapterOnClickHandler {
        void onClick(MovieData movieClicked);
    }

    public PosterAdapter(PosterAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public class PosterAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView mPosterImageView;

        public PosterAdapterViewHolder(View view) {
            super(view);
            mPosterImageView = (ImageView) view.findViewById(R.id.poster_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieData movieClicked = mMoviesData.get(adapterPosition);
            mClickHandler.onClick(movieClicked);
        }
    }

    @Override
    public PosterAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForGridItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem, viewGroup, shouldAttachToParentImmediately);
        return new PosterAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterAdapterViewHolder posterAdapterViewHolder, int position) {
        if (mMoviesData.get(position).getIsFavorite()) {
            File uri = new File(mMoviesData.get(position).getImgStorageDir(),
                    mMoviesData.get(position).getImgUrl());

            Picasso.with((Context) mClickHandler)
                    .load(uri)
                    .placeholder(R.drawable.imagenotfound_icon)
                    .into(posterAdapterViewHolder.mPosterImageView);
        } else {
            String uri = "http://image.tmdb.org/t/p/"
                    + posterSizes[4]
                    + mMoviesData.get(position).getImgUrl();
            
            Picasso.with((Context) mClickHandler)
                    .load(uri)
                    .placeholder(R.drawable.imagenotfound_icon)
                    .into(posterAdapterViewHolder.mPosterImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) return 0;
        return mMoviesData.size();
    }

    public void setMovieData(ArrayList<MovieData> moviesData) {
        mMoviesData = new ArrayList(moviesData);
        notifyDataSetChanged();
    }

    public int removeMovieFromList(String movieId) {
        for (int i = 0; i < mMoviesData.size(); i++) {
            if (mMoviesData.get(i).getId().equals(movieId)) {
                mMoviesData.remove(i);

                return i;
            }
        }

        return -1;
    }

    public int setMovieFavValue(String movieId, boolean favValue) {
        for (int i = 0; i < mMoviesData.size(); i++) {
            if (mMoviesData.get(i).getId().equals(movieId)) {
                mMoviesData.get(i).setIsFavorite(favValue);

                return i;
            }
        }

        return -1;
    }
}
