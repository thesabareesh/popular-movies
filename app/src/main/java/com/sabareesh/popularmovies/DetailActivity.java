package com.sabareesh.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent=getIntent();
        Movies movieDetail= intent.getParcelableExtra("movieDetail");
        DetailActivityFragment detailFragment=new DetailActivityFragment();
        detailFragment.setMovieDetail(movieDetail);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frag_container, detailFragment,
                        DetailActivityFragment.class.getSimpleName())
                .commit();

    }
}
