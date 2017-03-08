package com.example.android.mymovieapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gunnaringi on 2017-03-08.
 */

class ReviewData implements Parcelable {

    private String reviewAuthor;
    private String reviewContent;

    public ReviewData(String reviewAuthor, String reviewContent) {
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewAuthor() {
        return this.reviewAuthor;
    }

    public String getReviewContent() {
        return this.reviewContent;
    }

    public ReviewData(Parcel in){
        String[] data = new String[2];

        in.readStringArray(data);

        this.reviewAuthor = data[0];
        this.reviewContent = data[1];
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {
                this.reviewAuthor,
                this.reviewContent
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ReviewData createFromParcel(Parcel in) {
            return new ReviewData(in);
        }

        public ReviewData[] newArray(int size) {
            return new ReviewData[size];
        }
    };
}
