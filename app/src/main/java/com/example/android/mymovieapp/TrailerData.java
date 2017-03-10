package com.example.android.mymovieapp;

/**
 * Created by gunnaringi on 2017-03-10.
 */

public class TrailerData {

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
}
