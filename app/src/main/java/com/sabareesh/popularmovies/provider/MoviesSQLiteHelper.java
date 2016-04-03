package com.sabareesh.popularmovies.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by SABAREESH on 03-Apr-16.
 */
public class MoviesSQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME="PopularMovies";
    static final String TABLE_NAME="FavoriteMovies";
    static final int DB_VERSION=1;

    //DB columns
    public static final String ROW_ID="id";
    public static final String ID="movieId";
    public static final String TITLE="title";
    public static final String POSTERPATH_SQUARE="posterPathSquare";
    public static final String POSTERPATH_WIDE="posterPathWide";
    public static final String VOTE_AVG="voteAverage";
    public static final String RELEASE_DATE="releaseDate";
    public static final String OVERVIEW="overview";

    static final String CREATE_TABLE=" CREATE TABLE " + TABLE_NAME +
            " ( "+ROW_ID+
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + ID+
            " INTEGER NOT NULL, " + "" + TITLE+
            " TEXT NOT NULL, " + POSTERPATH_SQUARE+
            " TEXT NOT NULL, " + POSTERPATH_WIDE+
            " TEXT NOT NULL, " + RELEASE_DATE+
            " TEXT NOT NULL, " + VOTE_AVG+
            " TEXT NOT NULL, " + OVERVIEW+
            " TEXT NOT NULL); ";



    public MoviesSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Creating a table", "" + CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
        onCreate(db);
    }
}
