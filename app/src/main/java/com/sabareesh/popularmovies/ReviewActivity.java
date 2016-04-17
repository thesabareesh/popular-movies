package com.sabareesh.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.sabareesh.popularmovies.utils.MovieUtils;

import butterknife.Bind;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        final View view=findViewById(android.R.id.content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final TextView txtViewDetail= (TextView)findViewById(R.id.review_detail);
        Intent intent=getIntent();
        String author= intent.getStringExtra("author");
        String body= intent.getStringExtra("body");
        txtViewDetail.setText(body);
        getSupportActionBar().setTitle("Review by " + author);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


}
