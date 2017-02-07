package com.example.android.mymovieapp;

import java.io.Serializable;

/**
 * Created by gunnaringi on 2017-02-02.
 */

public class MovieData implements Serializable {
    private String imgUrl;
    private String title;
    private String overview;
    private String rating;
    private String relDate;
    private String id;

    //Getters
    public String getImgUrl() { return imgUrl; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getRating() { return rating; }
    public String getRelDate() { return relDate; }
    public String getId() { return id; }

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
}
