package com.sabareesh.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private String titleBarName;
    private Movies movieDetail;
    @Bind(R.id.original_title) TextView txtViewTitle;
    @Bind(R.id.release_date) TextView txtViewReleaseDate;
    @Bind(R.id.user_rating) TextView txtViewRating;
    @Bind(R.id.synopsys) TextView txtViewSynopsys;
    @Bind(R.id.squarePoster)ImageView imgViewSquarePoster;
    @Bind(R.id.widePoster) ImageView imgViewWidePoster;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }


    @Override
    public void onStart(){
        super.onStart();
        View view=getView();
        if(view!=null) {
            Movies movieDetails= movieDetail;
            //set title bar name
            getActivity().setTitle(movieDetails.mTitle);

            ButterKnife.bind(this,getActivity());

            txtViewTitle.setText(movieDetails.mTitle);
            String[] yearOfRelease = (movieDetails.mReleaseDate).split("-");
            txtViewReleaseDate.setText(yearOfRelease[0]);
            DecimalFormat df = new DecimalFormat("#");
            txtViewRating.setText(df.format(movieDetails.mUsersRating));
            txtViewSynopsys.setText(movieDetails.mSynopsys);

            Picasso.with(getActivity())
                    .load(movieDetails.getPosterPath())
                    .into(imgViewSquarePoster);

            Picasso.with(getActivity())
                    .load(movieDetails.getBackdropPath())
                    .into(imgViewWidePoster);
        }
    }
    public void setMovieDetail(Movies movieDetail){
        this.movieDetail=movieDetail;
    }
}
