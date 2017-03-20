package com.example.android.mymovieapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.mymovieapp.database.FavoriteContract.FavoriteEntry;

/**
 * Created by gunnaringi on 2017-03-19.
 */

public class FavoritesContentProvider extends ContentProvider {

    private FavoriteDbHelper mFavoriteDbHelper;

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_MOVIE_ID = 101;

    public static final int TRAILERS = 200;
    public static final int TRAILERS_WITH_MOVIE_ID = 201;

    public static final int REVIEWS = 300;
    public static final int REVIEWS_WITH_MOVIE_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoriteDbHelper = new FavoriteDbHelper(context);

        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri = null;

        switch (match) {
            case MOVIES:
                long movieId = db.insert(FavoriteEntry.MOVIE_TABLE_NAME, null, contentValues);
                if ( movieId > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavoriteEntry.MOVIE_CONTENT_URI, movieId);
                }

                break;

            case TRAILERS:
                long trailerId = db.insert(FavoriteEntry.TRAILER_TABLE_NAME, null, contentValues);
                if ( trailerId > 0 ) {
                    returnUri = ContentUris.withAppendedId(
                            FavoriteEntry.TRAILER_CONTENT_URI, trailerId);
                }

                break;

            case REVIEWS:
                long reviewId = db.insert(FavoriteEntry.REVIEW_TABLE_NAME, null, contentValues);
                if ( reviewId > 0 ) {
                    returnUri = ContentUris.withAppendedId(
                            FavoriteEntry.REVIEW_CONTENT_URI, reviewId);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (returnUri == null) {
            throw new SQLException("Failed to insert row into " + uri);
        }

        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mFavoriteDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match) {
            case MOVIES:
                retCursor = db.query(FavoriteEntry.MOVIE_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int itemsDeleted;

        String movieId;

        switch (match) {
            case MOVIES_WITH_MOVIE_ID:
                movieId = uri.getPathSegments().get(1);

                itemsDeleted = db.delete(FavoriteEntry.MOVIE_TABLE_NAME,
                        FavoriteEntry.COLUMN_MOVIE_ID + "=?", new String[]{movieId});

                break;

            case TRAILERS_WITH_MOVIE_ID:
                movieId = uri.getPathSegments().get(1);

                itemsDeleted = db.delete(FavoriteEntry.TRAILER_TABLE_NAME,
                        FavoriteEntry.COLUMN_MOVIE_ID + "=?", new String[]{movieId});

                break;

            case REVIEWS_WITH_MOVIE_ID:
                movieId = uri.getPathSegments().get(1);

                itemsDeleted = db.delete(FavoriteEntry.REVIEW_TABLE_NAME,
                        FavoriteEntry.COLUMN_MOVIE_ID + "=?", new String[]{movieId});

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return itemsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(FavoriteContract.AUTHORITY,
                FavoriteContract.PATH_MOVIES + "/*", MOVIES_WITH_MOVIE_ID);

        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(FavoriteContract.AUTHORITY,
                FavoriteContract.PATH_TRAILERS + "/*", TRAILERS_WITH_MOVIE_ID);

        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(FavoriteContract.AUTHORITY,
                FavoriteContract.PATH_REVIEWS + "/*", REVIEWS_WITH_MOVIE_ID);

        return uriMatcher;
    }
}
