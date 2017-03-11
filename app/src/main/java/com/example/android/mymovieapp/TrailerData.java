package com.example.android.mymovieapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gunnaringi on 2017-03-10.
 */

public class TrailerData implements Parcelable {

    private String trailerTitle;
    private String trailerUrl;
    private String trailerType;

    public TrailerData(String trailerTitle, String trailerUrl, String trailerType) {
        this.trailerTitle = trailerTitle;
        this.trailerUrl = trailerUrl;
        this.trailerType = trailerType;
    }

    public void setTrailerTitle(String trailerTitle) {
        this.trailerTitle = trailerTitle;
    }
    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }
    public void setTrailerType(String trailerType) {
        this.trailerType = trailerType;
    }

    public String getTrailerTitle() {
        return this.trailerTitle;
    }
    public String getTrailerUrl() {
        return this.trailerUrl;
    }
    public String getTrailerType() {
        return this.trailerType;
    }

    public TrailerData(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);

        this.trailerTitle = data[0];
        this.trailerUrl = data[1];
        this.trailerType = data[2];
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {
                this.trailerTitle,
                this.trailerUrl,
                this.trailerType
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TrailerData createFromParcel(Parcel in) {
            return new TrailerData(in);
        }

        public TrailerData[] newArray(int size) {
            return new TrailerData[size];
        }
    };
}
