package com.sabareesh.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private String titleBarName;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent=getActivity().getIntent();
        Movies movieDetails= intent.getParcelableExtra("movieDetail");
        //set title bar name
        getActivity().setTitle(movieDetails.mTitle);

        //Get all UI ids
        TextView txtViewTitle= (TextView)rootView.findViewById(R.id.original_title);
        TextView txtViewReleaseDate=(TextView)rootView.findViewById(R.id.release_date);
        TextView txtViewRating=(TextView)rootView.findViewById(R.id.user_rating);
        TextView txtViewSynopsys=(TextView)rootView.findViewById(R.id.synopsys);
        ImageView imgViewSquarePoster=(ImageView)rootView.findViewById(R.id.squarePoster);
        ImageView imgViewWidePoster=(ImageView)rootView.findViewById(R.id.widePoster);

        txtViewTitle.setText(movieDetails.mTitle);
        String[] yearOfRelease = (movieDetails.mReleaseDate).split("-");
        txtViewReleaseDate.setText(yearOfRelease[0]);
        DecimalFormat df = new DecimalFormat("#");
        txtViewRating.setText(df.format(movieDetails.mUsersRating));
        txtViewSynopsys.setText(movieDetails.mSynopsys);

        Picasso.with(getContext())
                .load(movieDetails.getPosterPath())
                .into(imgViewSquarePoster);

        Picasso.with(getContext())
                .load(movieDetails.getBackdropPath())
                .into(imgViewWidePoster);

        return rootView;
    }
}
