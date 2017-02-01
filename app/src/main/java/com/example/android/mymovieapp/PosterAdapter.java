package com.example.android.mymovieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by gunnaringi on 2017-02-01.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder> {

    private final PosterAdapterOnClickHandler mClickHandler;

    public interface PosterAdapterOnClickHandler {
        void onClick();
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
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
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
        posterAdapterViewHolder.mPosterImageView.setImageResource(R.drawable.android_icon);
    }

    @Override
    public int getItemCount() {
        /* TODO : Make dummy data with an array of movie titles to connect with each poster
            to get a limited number of elements.
        */
    }
}
