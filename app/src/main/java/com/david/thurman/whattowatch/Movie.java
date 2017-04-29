package com.david.thurman.whattowatch;

import android.graphics.Bitmap;

/**
 * Created by davidthurman on 4/25/17.
 */

public class Movie {
    String title = "";
    String year = "";
    String director = "";
    String id = "";
    String poster = "";
    Bitmap bitmap;
    int[] genreIds = null;

    Movie(String title, String year, String director){
        this.title = title;
        this.year = year;
        this.director = director;
    }

    Movie(String title, String id){
        this.title = title;
        this.id = id;
    }
}
