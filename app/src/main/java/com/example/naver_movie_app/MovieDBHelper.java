package com.example.naver_movie_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDBHelper extends SQLiteOpenHelper {
    public MovieDBHelper(Context context) {
        super(context, "movie", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE movie (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, imageSrc VARCHAR(100) , title VARCHAR(30), director VARCHAR(30), actors VARCHAR(80), rating INTEGER, link VARCHAR(200));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS movie;");
        onCreate(db);
    }
}
