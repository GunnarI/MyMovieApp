package com.example.android.mymovieapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.mymovieapp.database.FavoriteContract.FavoriteEntry;

/**
 * Created by gunnaringi on 2017-03-08.
 */

public class FavoriteDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorite_movies.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " +
                FavoriteEntry.MOVIE_TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL UNIQUE, " +
                FavoriteEntry.COLUMN_IMAGE_STORAGE_DIR + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_POSTER_URL + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_OVERVIEW + " TEXT, " +
                FavoriteEntry.COLUMN_MOVIE_RATING + " TEXT, " +
                FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT" +
                "); ";

        final String SQL_CREATE_FAVORITE_TRAILER_TABLE = "CREATE TABLE " +
                FavoriteEntry.TRAILER_TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_TRAILER_TITLE + " TEXT, " +
                FavoriteEntry.COLUMN_MOVIE_TRAILER_URL + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_TRAILER_TYPE + " TEXT, " +
                "UNIQUE (" + FavoriteEntry.COLUMN_MOVIE_ID + ", " +
                FavoriteEntry.COLUMN_MOVIE_TRAILER_URL + "), " +
                "FOREIGN KEY (" + FavoriteEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                FavoriteEntry.MOVIE_TABLE_NAME + "(" + FavoriteEntry.COLUMN_MOVIE_ID + ") " +
                "); ";

        final String SQL_CREATE_FAVORITE_REVIEW_TABLE = "CREATE TABLE " +
                FavoriteEntry.REVIEW_TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_REVIEW_AUTHOR + " TEXT, " +
                FavoriteEntry.COLUMN_MOVIE_REVIEW_CONTENT + " TEXT, " +
                "UNIQUE (" + FavoriteEntry.COLUMN_MOVIE_ID + ", " +
                FavoriteEntry.COLUMN_MOVIE_REVIEW_CONTENT + "), " +
                "FOREIGN KEY (" + FavoriteEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                FavoriteEntry.MOVIE_TABLE_NAME + "(" + FavoriteEntry.COLUMN_MOVIE_ID + ")" +
                "); ";

        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_REVIEW_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
