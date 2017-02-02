package com.example.android.mymovieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by gunnaringi on 2017-02-01.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder> {

    private String[] movieTitles;
    private final PosterAdapterOnClickHandler mClickHandler;

    public interface PosterAdapterOnClickHandler {
        void onClick(String movieClicked);
    }

    public PosterAdapter(PosterAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public class PosterAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView mPosterImageView;
        public final TextView mPosterTextView;

        public PosterAdapterViewHolder(View view) {
            super(view);
            mPosterImageView = (ImageView) view.findViewById(R.id.poster_image);
            mPosterTextView = (TextView) view.findViewById(R.id.movie_poster_title);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String movieClicked = movieTitles[adapterPosition];
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
        String thisMovieTitle = movieTitles[position];
        posterAdapterViewHolder.mPosterImageView.setImageResource(R.drawable.android_icon);
        posterAdapterViewHolder.mPosterTextView.setText(thisMovieTitle);
    }

    @Override
    public int getItemCount() {
        if (movieTitles == null) return 0;
        return movieTitles.length;
    }

    public void setMovieData(String[] titles) {
        movieTitles = titles;
        notifyDataSetChanged();
    }
}
