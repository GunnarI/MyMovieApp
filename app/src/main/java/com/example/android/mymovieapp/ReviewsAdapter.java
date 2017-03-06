package com.example.android.mymovieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gunnaringi on 2017-03-06.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private ArrayList<String[]> mReviewData;

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder reviewsAdapterViewHolder, int position) {
        reviewsAdapterViewHolder.mReviewAuthor.setText(mReviewData.get(position)[0]);
        reviewsAdapterViewHolder.mReviewContent.setText(mReviewData.get(position)[1]);
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForReviewItem = R.layout.reviews_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForReviewItem,
                parent, shouldAttachToParentImmediately);
        return new ReviewsAdapter.ReviewsAdapterViewHolder(view);
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mReviewAuthor;
        public final TextView mReviewContent;

        public ReviewsAdapterViewHolder(View view) {
            super(view);
            mReviewAuthor = (TextView) view.findViewById(R.id.review_author);
            mReviewContent = (TextView) view.findViewById(R.id.review_content);
        }
    }

    @Override
    public int getItemCount() {
        if (mReviewData == null) return 0;
        return mReviewData.size();
    }

    public void setReviewData(ArrayList<String[]> reviewData) {
        mReviewData = new ArrayList(reviewData);
        notifyDataSetChanged();
    }
}
