package com.example.android.mymovieapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by gunnaringi on 2017-02-02.
 */

public class DetailActivity extends AppCompatActivity {

    private MovieData mMovieData;
    private TextView mMovieTitle;
    private ImageView mMovieThumbnail;
    private TextView mMovieDate;
    private ImageView mMovieRatingStar1;
    private ImageView mMovieRatingStar2;
    private ImageView mMovieRatingStar3;
    private ImageView mMovieRatingStar4;
    private ImageView mMovieRatingStar5;
    private TextView mMovieDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentThatStartedThisActivity = getIntent();

        mMovieTitle = (TextView) findViewById(R.id.movie_title);
        mMovieThumbnail = (ImageView) findViewById(R.id.movie_thumbnail);
        mMovieDate = (TextView) findViewById(R.id.movie_rel_date);
        mMovieRatingStar1 = (ImageView) findViewById(R.id.star_icon_1);
        mMovieRatingStar2 = (ImageView) findViewById(R.id.star_icon_2);
        mMovieRatingStar3 = (ImageView) findViewById(R.id.star_icon_3);
        mMovieRatingStar4 = (ImageView) findViewById(R.id.star_icon_4);
        mMovieRatingStar5 = (ImageView) findViewById(R.id.star_icon_5);
        mMovieDescription = (TextView) findViewById(R.id.movie_description);

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("MovieDetail")) {
                mMovieData = (MovieData) intentThatStartedThisActivity
                        .getParcelableExtra("MovieDetail");

                mMovieTitle.setText(mMovieData.getTitle());
                Picasso.with(this)
                        .load("http://image.tmdb.org/t/p/w500" + mMovieData.getImgUrl())
                        .into(mMovieThumbnail);
                mMovieDate.setText(mMovieData.getRelYear());
                Double rating = Double.parseDouble(mMovieData.getRating());
                int[] ratingStars = getRatingStars(rating);
                Log.i("The rating:",mMovieData.getRating());

                switch (ratingStars[0]) {
                    case 5:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 4:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar5);
                        break;
                    case 3:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 2:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 1:
                        Picasso.with(this)
                                .load(R.drawable.full_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(ratingStars[1])
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                    case 0:
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar1);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar2);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar3);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar4);
                        Picasso.with(this)
                                .load(R.drawable.empty_star)
                                .into(mMovieRatingStar5);
                        break;
                }

                mMovieDescription.setText(mMovieData.getOverview());
            }
        }
    }

    public int[] getRatingStars(Double rating) {
        int[] ratingStars = new int[2];
        Double ratingRem = (rating % 2.0) / 2;
        Double ratingBase = rating / 2 - ratingRem;
        ratingStars[0] = ratingBase.intValue();

        Double ratingRemRem = ratingRem % 0.25;
        Double ratingRemBase = ratingRem / 0.25 - ratingRemRem;
        int partialStar = ratingRemBase.intValue();

        switch (partialStar) {
            case 0:
                ratingStars[1] = R.drawable.empty_star;
                break;
            case 1:
                ratingStars[1] = R.drawable.quarter_star;
                break;
            case 2:
                ratingStars[1] = R.drawable.half_star;
                break;
            case 3:
                ratingStars[1] = R.drawable.three_quarter_star;
                break;
            default:
                break;
        }

        return ratingStars;
    }
}
