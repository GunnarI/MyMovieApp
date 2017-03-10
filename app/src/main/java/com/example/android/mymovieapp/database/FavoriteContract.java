package com.example.android.mymovieapp.database;

import android.provider.BaseColumns;

/**
 * Created by gunnaringi on 2017-03-08.
 */

public class FavoriteContract {
    public static final class FavoriteEntry implements BaseColumns {
        public static final String MOVIE_TABLE_NAME = "favoriteMovies";
        public static final String TRAILER_TABLE_NAME = "favoriteTrailers";
        public static final String REVIEW_TABLE_NAME = "favoriteReviews";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_POSTER_URL = "moviePoster";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_MOVIE_TRAILER_URL = "trailersUrl";
        public static final String COLUMN_MOVIE_TRAILER_TITLE = "trailersTitle";
        public static final String COLUMN_MOVIE_TRAILER_TYPE = "trailersType";
        public static final String COLUMN_MOVIE_REVIEW_AUTHOR = "reviewsAuthor";
        public static final String COLUMN_MOVIE_REVIEW_CONTENT = "reviewsContent";
    }
}
