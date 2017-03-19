package com.example.android.mymovieapp.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gunnaringi on 2017-03-08.
 */

public class FavoriteContract {

    public static final String AUTHORITY = "com.example.android.mymovieapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "favoriteMovies";
    public static final String PATH_TRAILERS = "favoriteTrailers";
    public static final String PATH_REVIEWS = "favoriteReviews";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri MOVIE_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES).build();
        public static final Uri TRAILER_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILERS).build();
        public static final Uri REVIEW_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEWS).build();

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
        public static final String COLUMN_IMAGE_STORAGE_DIR = "storageDir";
    }
}
