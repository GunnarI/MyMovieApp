package com.example.android.mymovieapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by gunnaringi on 2017-02-02.
 */

public class MovieData implements Parcelable {
    private String imgUrl;
    private String title;
    private String overview;
    private String rating;
    private String relDate;
    private String id;
    private ArrayList<TrailerData> trailers;
    private ArrayList<ReviewData> reviews;
    private Boolean isFavorite = false;

    public MovieData(String imgUrl, String title, String overview,
                     String rating, String relDate, String id)
    {
        this.imgUrl = imgUrl;
        this.title = title;
        this.overview = overview;
        this.rating = rating;
        this.relDate = relDate;
        this.id = id;
    }

    //Getters
    public String getImgUrl() { return imgUrl; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getRating() { return rating; }
    public String getRelDate() { return relDate; }
    public String getRelYear() {
        String[] partedDate = relDate.split("-");
        return partedDate[0];
    }
    public String getId() { return id; }
    public ArrayList<TrailerData> getTrailers() { return trailers; }
    public ArrayList<ReviewData> getReviews() { return reviews; }
    public Boolean getIsFavorite() { return isFavorite; }

    //Setters
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public void setRelDate(String relDate) {
        this.relDate = relDate;
    }
    public void setId(String id) { this.id = id; }
    public void setTrailers(ArrayList<TrailerData> trailers) { this.trailers = trailers; }
    public void setReviews(ArrayList<ReviewData> reviews) { this.reviews = reviews; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }

    public MovieData(Parcel in){
        String[] data = new String[6];

        in.readStringArray(data);

        this.imgUrl = data[0];
        this.title = data[1];
        this.overview = data[2];
        this.rating = data[3];
        this.relDate = data[4];
        this.id = data[5];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.imgUrl,
                this.title,
                this.overview,
                this.rating,
                this.relDate,
                this.id
        });
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}
