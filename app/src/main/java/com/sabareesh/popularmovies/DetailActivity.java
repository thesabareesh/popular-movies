package com.sabareesh.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        Movies movieDetail= intent.getParcelableExtra("movieDetail");
        DetailActivityFragment detailFragment=(DetailActivityFragment)getFragmentManager().findFragmentById(R.id.frag_detail);
        detailFragment.setMovieDetail(movieDetail);


    }
}
