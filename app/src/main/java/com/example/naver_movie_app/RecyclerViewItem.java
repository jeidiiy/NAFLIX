package com.example.naver_movie_app;

import java.io.Serializable;

public class RecyclerViewItem implements Serializable {
    private final String imageSrc;
    private final String title;
    private final String director;
    private final String actors;
    private final int rating;
    private final String link;

    public RecyclerViewItem(String imageSrc, String title, String director, String actors, int rating, String link) {
        this.imageSrc = imageSrc;
        this.title = title;
        this.director = director;
        this.actors = actors;
        this.rating = rating;
        this.link = link;
    }

    public String getTitle() {
        return this.title;
    }

    public String getImageSrc() {
        return this.imageSrc;
    }

    public String getDirector() {
        return this.director;
    }

    public String getActors() {
        return this.actors;
    }

    public int getRating() {
        return this.rating;
    }

    public String getLink() {
        return this.link;
    }
}
