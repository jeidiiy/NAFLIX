package com.example.naver_movie_app;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageViewPoster;
    TextView textViewTitle;
    TextView textViewDirector;
    TextView textViewActors;
    TextView textViewRating;
    RatingBar ratingBar;

    public MyViewHolder(View itemView) {
        super(itemView);

        imageViewPoster = itemView.findViewById(R.id.moviePoster);
        textViewTitle = itemView.findViewById(R.id.movieTitle);
        textViewDirector = itemView.findViewById(R.id.movieDirector);
        textViewActors = itemView.findViewById(R.id.movieActors);
        textViewRating = itemView.findViewById(R.id.movieRatingText);
        ratingBar = itemView.findViewById(R.id.movieRatingBar);
    }
}
