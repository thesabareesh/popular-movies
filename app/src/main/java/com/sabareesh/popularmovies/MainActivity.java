package com.sabareesh.popularmovies;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.MovieListListener {

    private PosterAdapter posterAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void itemClicked(Movies movieDetail) {

        View fragContainer=findViewById(R.id.frag_detail_container);
        if(fragContainer!=null){
            DetailActivityFragment frag_detail=new DetailActivityFragment();
            FragmentTransaction txn=getFragmentManager().beginTransaction();
            frag_detail.setMovieDetail(movieDetail);
            txn.replace(R.id.frag_detail_container, frag_detail);
            txn.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            txn.commit();
        }
        else{

            Intent detailActivity= new Intent(this,DetailActivity.class);
            detailActivity.putExtra("movieDetail",movieDetail);
            startActivity(detailActivity);
        }
    }

}

