package com.sabareesh.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;


import com.sabareesh.popularmovies.provider.MoviesProvider;
import com.sabareesh.popularmovies.provider.MoviesSQLiteHelper;
import com.sabareesh.popularmovies.utils.MovieUtils;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivityFragment extends Fragment implements FetchTrailersTask.Event,FetchReviewsTask.Event,View.OnClickListener {
    View.OnClickListener mOnClickListener;
    private Extras extras;
    private Movies movieDetail;
    @Bind(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.original_title)      TextView txtViewTitle;
    @Bind(R.id.release_date)        TextView txtViewReleaseDate;
    @Bind(R.id.user_rating)         TextView txtViewRating;
    @Bind(R.id.rating_denominator)  TextView txtViewDenominator;
    @Bind(R.id.synopsys)            TextView txtViewSynopsys;
    @Bind(R.id.squarePoster)        ImageView imgViewSquarePoster;
    @Bind(R.id.widePoster)          ImageView imgViewWidePoster;
    @Bind(R.id.favorite)            FloatingActionButton favBtn;
    @Bind(R.id.share)               FloatingActionButton favShare;
    @Bind(R.id.trailers_header)     TextView txtViewTrailersHeader;
    @Bind(R.id.trailers_container)  HorizontalScrollView scrollViewTrailers;
    @Bind(R.id.trailers)            ViewGroup viewTrailers;
    @Bind(R.id.reviews_header)      TextView txtViewReviewsHeader;
    @Bind(R.id.reviews)             ViewGroup viewReviews;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view= inflater.inflate(R.layout.fragment_detail, container, false);

       // final View view=getView();
        if(view!=null && movieDetail!=null) {
            Movies movieDetails= movieDetail;
            //set title bar name
            //getActivity().setTitle(movieDetails.mTitle);
            Toolbar toolbar = (Toolbar)view.findViewById(R.id.app_bar);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)view.findViewById(R.id.collapsing_toolbar_layout);
            collapsingToolbarLayout.setTitle(movieDetails.mTitle);
            ButterKnife.bind(this, view);

            txtViewTitle.setText(movieDetails.mTitle);
            String[] yearOfRelease = (movieDetails.mReleaseDate).split("-");
            txtViewReleaseDate.setText(yearOfRelease[0]);
            DecimalFormat df = new DecimalFormat("#");
            txtViewRating.setText(df.format(movieDetails.mUsersRating));
            txtViewSynopsys.setText(movieDetails.mSynopsys);
            favBtn.setImageResource(isFavourite(getActivity(), movieDetails) ? R.drawable.ic_star_white_24dp : R.drawable.ic_star_border_white_24dp);
            Picasso.with(getActivity())
                    .load(movieDetails.getPosterPath())
                    .placeholder(R.drawable.square_placeholder)
                    .into(imgViewSquarePoster);

            Picasso.with(getActivity())
                    .load(movieDetails.getBackdropPath())
                    .placeholder(R.drawable.wide_placeholder)
                    .into(imgViewWidePoster);


            FetchTrailersTask fetchTrailersTask= new FetchTrailersTask(getActivity(),this);
            FetchReviewsTask fetchReviewsTask= new FetchReviewsTask(getActivity(),this);
            fetchTrailersTask.execute(movieDetails.mPosterId);
            fetchReviewsTask.execute(movieDetails.mPosterId);
        }

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavourite(getActivity(), movieDetail);
            }
        };

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                MovieUtils utils = new MovieUtils();
                View[] animatedViews = new View[]{
                        imgViewSquarePoster, txtViewTitle, txtViewReleaseDate,
                        txtViewRating, txtViewDenominator, txtViewSynopsys, txtViewTrailersHeader, scrollViewTrailers};
                utils.staggerContent(animatedViews);
                return true;
            }
        });
        return view;
    }


    public void setMovieDetail(Movies movieDetail){
        this.movieDetail=movieDetail;
    }

    @OnClick(R.id.favorite)
    public void onClick(View view){
        if (view.getId() == R.id.favorite && movieDetail != null) {
            toggleFavourite(getActivity(), movieDetail);
        }
        else if(view.getId() == R.id.share){
            String trailer_key = extras.getTrailerAtIndex((int)0).getSource();
            String TRAILER_BASE_URL=getActivity().getString(R.string.trailer_base_url);
            String SHARE_TRAILER_TEXT=getActivity().getString(R.string.trailer_share_text);
            String APP_HASHTAG=getActivity().getString(R.string.app_hashtag);
            String trailer_url= TRAILER_BASE_URL+trailer_key;
            String movieName = movieDetail.mTitle;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_TRAILER_TEXT + " " + movieName + " | " + trailer_url + " "+APP_HASHTAG);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_trailer)));
        }
        else if (view.getId() == R.id.video_thumb) {
            String videoUrl = (String) view.getTag();
            try{
                Intent playVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                startActivity(playVideoIntent);
            }catch (ActivityNotFoundException ex){
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                startActivity(intent);
            }
        }
        else if (view.getId() == R.id.review) {
            Review review = (Review) view.getTag();
            Intent reviewIntent = new Intent(getActivity(), ReviewActivity.class);
            reviewIntent.putExtra("body",review.getBody().replace("\n\n", " ").replace("\n", " "));
            reviewIntent.putExtra("author",review.getAuthor());
            startActivity(reviewIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }
        return true;
    }

    public int toggleFavourite(Context context, Movies movie) {

        Uri.Builder uriBuilder = MoviesProvider.CONTENT_URI.buildUpon();

        if (isFavourite(context, movie)){
            context.getContentResolver().delete(uriBuilder.build(), String.valueOf(movie.mPosterId), null);
            favBtn.setImageResource(R.drawable.ic_star_border_white_24dp);
            
         }
        else
        {
            favBtn.setImageResource(R.drawable.ic_star_white_24dp);
            Snackbar.make(getView(),"Added to favorites",Snackbar.LENGTH_LONG)
                    .setAction("UNDO", mOnClickListener)
                    .show();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesSQLiteHelper.ID, movie.mPosterId);
            contentValues.put(MoviesSQLiteHelper.TITLE, movie.mTitle);
            contentValues.put(MoviesSQLiteHelper.POSTERPATH_SQUARE, movie.mPosterPath);
            contentValues.put(MoviesSQLiteHelper.POSTERPATH_WIDE, movie.mBackdropPath);
           // contentValues.put(MoviesSQLiteHelper.FILEPATH_WIDE_CACHE, backdropFilePath);
           // contentValues.put(MoviesSQLiteHelper.FILEPATH_SQUARE_CACHE, getResources().getString(R.string.cache_thumbnails_path)+"Fav"+movie.mPosterId+".jpg");
            contentValues.put(MoviesSQLiteHelper.VOTE_AVG, movie.mUsersRating);
            contentValues.put(MoviesSQLiteHelper.OVERVIEW, movie.mSynopsys);
            contentValues.put(MoviesSQLiteHelper.RELEASE_DATE, movie.mReleaseDate);


            context.getContentResolver().insert(MoviesProvider.CONTENT_URI, contentValues);
        }
        return 0;
    }

    public static boolean isFavourite(Context context,Movies movie) {
        String URL = MoviesProvider.URL;
        Uri movies = Uri.parse(URL);
        Cursor cursor = null;
        cursor = context.getContentResolver().query(movies, null, MoviesSQLiteHelper.ID+" = "+movie.mPosterId, null, MoviesSQLiteHelper.ROW_ID);
        if (cursor != null&&cursor.moveToNext()) {
            return true;
        } else {
            return false;
        }
    }

    public void onTrailersFetch(Extras extras) {
        showTrailers(extras);
    }

    public void onReviewsFetch(Extras extras) {
        showReviews(extras);
    }

    private void showTrailers(Extras extras){
        int numTrailers = extras.getTrailersNum();
        boolean hasTrailers = numTrailers>0;
        //mToolbar.getMenu().findItem(R.id.share_trailer).setVisible(hasTrailers);
        favShare.setVisibility(hasTrailers ? View.VISIBLE : View.GONE);
        txtViewTrailersHeader.setVisibility(hasTrailers ? View.VISIBLE : View.GONE);
        scrollViewTrailers.setVisibility(hasTrailers ? View.VISIBLE : View.GONE);
        if (hasTrailers) {
            addTrailers(extras);
        }
    }

    private void showReviews(Extras extras){
        int numReviews = extras.getReviewsNum();
        boolean hasReviews = numReviews>0;
        txtViewReviewsHeader.setVisibility(hasReviews ? View.VISIBLE : View.GONE);
        viewReviews.setVisibility(hasReviews ? View.VISIBLE : View.GONE);
        if (hasReviews) {
            addReviews(extras);
        }
    }

    public void addTrailers(final Extras extras){
        this.extras=extras;
        int numTrailers = extras.getTrailersNum();
        viewTrailers.removeAllViews();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Picasso picasso = Picasso.with(getActivity());
        for(int i=0; i<numTrailers;i++) {
            String TRAILER_THUMB_BASE_URL= getActivity().getString(R.string.trailer_thumb_base_url);
            String TRAILER_BASE_URL=getActivity().getString(R.string.trailer_base_url);
            String trailer_key=extras.getTrailerAtIndex((int) i).getSource();
            String trailer_url= TRAILER_BASE_URL + trailer_key;
            ViewGroup thumbContainer = (ViewGroup) inflater.inflate(R.layout.video, viewTrailers,
                    false);
            ImageView thumbView = (ImageView) thumbContainer.findViewById(R.id.video_thumb);
            thumbView.setTag(trailer_url);
            thumbView.setOnClickListener(this);

            picasso
                    .load(TRAILER_THUMB_BASE_URL + trailer_key + "/0.jpg")
                    .resizeDimen(R.dimen.video_width, R.dimen.video_height)
                    .placeholder(R.drawable.square_placeholder)
                    .centerCrop()
                    .into(thumbView);
            viewTrailers.addView(thumbContainer);
            favShare.setOnClickListener(this);
        }
    }

    public void addReviews(final Extras extras){
        int numReviews = extras.getReviewsNum();
        viewReviews.removeAllViews();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        for(int i=0; i<numReviews;i++) {
            ViewGroup reviewContainer = (ViewGroup) inflater.inflate(R.layout.review, viewReviews,
                    false);
            TextView reviewAuthor = (TextView) reviewContainer.findViewById(R.id.review_author);
            TextView reviewContent = (TextView) reviewContainer.findViewById(R.id.review_content);
            reviewAuthor.setText(extras.getReviewAtIndex(i).getAuthor());
            reviewContent.setText(extras.getReviewAtIndex(i).getBody().replace("\n\n", " ").replace("\n", " "));
            reviewContainer.setOnClickListener(this);
            reviewContainer.setTag(extras.getReviewAtIndex(i));
            viewReviews.addView(reviewContainer);

        }

    }

}
