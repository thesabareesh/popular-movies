package com.sabareesh.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabareesh.popularmovies.provider.MoviesProvider;
import com.sabareesh.popularmovies.provider.MoviesSQLiteHelper;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    View.OnClickListener mOnClickListener;
    private String titleBarName;
    private Movies movieDetail;
    @Bind(R.id.original_title) TextView txtViewTitle;
    @Bind(R.id.release_date) TextView txtViewReleaseDate;
    @Bind(R.id.user_rating) TextView txtViewRating;
    @Bind(R.id.synopsys) TextView txtViewSynopsys;
    @Bind(R.id.squarePoster)ImageView imgViewSquarePoster;
    @Bind(R.id.widePoster) ImageView imgViewWidePoster;
    @Bind(R.id.favorite) FloatingActionButton favBtn;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView= inflater.inflate(R.layout.fragment_detail, container, false);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favBtn.setImageResource(R.drawable.ic_star_border_white_24dp);
            }
        };
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

    @OnClick(R.id.favorite)
    public void onClick(View view){
        if (view.getId() == R.id.favorite && movieDetail != null) {

            favBtn.setImageResource(R.drawable.ic_star_white_24dp);
            toggleFavourite(getActivity(), movieDetail);
            Snackbar.make(view,"Added to favorites",Snackbar.LENGTH_LONG)
                    .setAction("Undo",mOnClickListener)
                    .show();


        }
    }

    public static int toggleFavourite(Context context, Movies movie)
    {

        Uri.Builder uriBuilder = MoviesProvider.CONTENT_URI.buildUpon();

       /* if(isFavourite(context, movie))
            context.getContentResolver().delete(uriBuilder.build(),String.valueOf(movie.getId()),null);
        else
        {*/
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesSQLiteHelper.ID, movie.mPosterId);
            contentValues.put(MoviesSQLiteHelper.TITLE, movie.mTitle);
            contentValues.put(MoviesSQLiteHelper.POSTERPATH_WIDE, movie.mBackdropPath);
            contentValues.put(MoviesSQLiteHelper.VOTE_AVG, movie.mUsersRating);
            contentValues.put(MoviesSQLiteHelper.OVERVIEW, movie.mSynopsys);
            contentValues.put(MoviesSQLiteHelper.RELEASE_DATE, movie.mReleaseDate);
            contentValues.put(MoviesSQLiteHelper.POSTERPATH_SQUARE, movie.mPosterPath);

            context.getContentResolver().insert(MoviesProvider.CONTENT_URI, contentValues);
       // }
        return 0;
    }


}
