package com.sabareesh.popularmovies;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;


import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.MovieListListener {

    private PosterAdapter posterAdapter = null;
    private boolean mTwoPane = false;
    private Movies mSelectedMovie = null;
    private static final String KEY_SELECTION = "selection";
    private static final String KEY_SORT_ORDER = "sort_order";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View fragContainer=findViewById(R.id.frag_detail_container);
        if(fragContainer!=null){
            mTwoPane=true;
            if (savedInstanceState != null) {
                Movies movieParcelable = savedInstanceState.getParcelable(KEY_SELECTION);
                showDetails(movieParcelable);
            }
        }

    }

    @Override
    public void itemClicked(Movies movieDetail) {

        if(mTwoPane){
            showDetails(movieDetail);
        }
        else{
            Intent detailActivity= new Intent(this,DetailActivity.class);
            detailActivity.putExtra("movieDetail",movieDetail);
            startActivity(detailActivity);
        }
    }

    public void showDetails(@Nullable Movies movieDetail) {
        if (! mTwoPane) {
            return;
        }
        if (movieDetail == null) {
            return;
        }
        mSelectedMovie = movieDetail;
        DetailActivityFragment detailFragment=new DetailActivityFragment();
        detailFragment.setMovieDetail(movieDetail);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frag_detail_container, detailFragment, DetailActivityFragment.class.getSimpleName())
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MainActivityFragment mainActivityFragment=new MainActivityFragment();
        outState.putParcelable(KEY_SELECTION, mSelectedMovie);
        outState.putString(KEY_SORT_ORDER, mainActivityFragment.mSortCriteria);
    }
}

