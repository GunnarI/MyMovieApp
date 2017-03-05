package com.example.android.mymovieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gunnaringi on 2017-03-05.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ArrayList<String[]> mTrailersData;
    private TrailerAdapterOnClickHandler mClickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(String trailerClicked);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView mTrailerThumbnail;
        public final TextView mTrailerName;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            mTrailerThumbnail = (ImageView) view.findViewById(R.id.trailer_thumbnail);
            mTrailerName = (TextView) view.findViewById(R.id.trailer_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String trailerYoutubeRef = mTrailersData.get(adapterPosition)[1];

            mClickHandler.onClick(trailerYoutubeRef);
        }
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForTrailerItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForTrailerItem,
                parent, shouldAttachToParentImmediately);
        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder trailerAdapterViewHolder, int position) {
        String url = "http://img.youtube.com/vi/"
                + mTrailersData.get(position)[1]
                + "/0.jpg";
        Picasso.with((Context) mClickHandler)
                .load(url)
                .placeholder(R.drawable.play_button)
                .into(trailerAdapterViewHolder.mTrailerThumbnail);
        trailerAdapterViewHolder.mTrailerName.setText(mTrailersData.get(position)[0]);
    }

    @Override
    public int getItemCount() {
        if (mTrailersData == null) return 0;
        return mTrailersData.size();
    }

    public void setTrailersData(ArrayList<String[]> trailersData) {
        mTrailersData = new ArrayList(trailersData);
        notifyDataSetChanged();
    }
}
