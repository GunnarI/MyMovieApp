package com.example.android.mymovieapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by gunnaringi on 2017-02-02.
 */

public class DetailActivity extends AppCompatActivity {

    private MovieData mMovieData;
    private TextView mMovieTitle;
    private ImageView mMovieThumbnail;
    private TextView mMovieDate;
    private TextView mMovieRating;
    private TextView mMovieDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentThatStartedThisActivity = getIntent();

        mMovieTitle = (TextView) findViewById(R.id.movie_title);
        mMovieThumbnail = (ImageView) findViewById(R.id.movie_thumbnail);
        mMovieDate = (TextView) findViewById(R.id.movie_rel_date);
        mMovieRating = (TextView) findViewById(R.id.movie_rating);
        mMovieDescription = (TextView) findViewById(R.id.movie_description);

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("MovieDetail")) {
                mMovieData = (MovieData) intentThatStartedThisActivity.getSerializableExtra("MovieDetail");
                mMovieTitle.setText(mMovieData.getTitle());
                new DownloadImageTask(mMovieThumbnail)
                        .execute("http://image.tmdb.org/t/p/w342" + mMovieData.getImgUrl());
                mMovieDate.setText(mMovieData.getRelDate());
                mMovieRating.setText(mMovieData.getRating());
                mMovieDescription.setText(mMovieData.getOverview());
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
