package com.sabareesh.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sabareesh.popularmovies.provider.MoviesProvider;
import com.sabareesh.popularmovies.provider.MoviesSQLiteHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private PosterAdapter posterAdapter = null;
    static interface MovieListListener{
        public void itemClicked(Movies movieDetail);
    }
    MovieListListener listener;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        GridView gridView = (GridView)rootView.findViewById(R.id.grid_posters);
        posterAdapter = new PosterAdapter(getActivity(),new ArrayList<Movies>());
        gridView.setAdapter(posterAdapter);
        loadMovieList(getString(R.string.sortby_popularity_id));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listener!=null){
                    Movies movieDetail = posterAdapter.getItem(position);
                    listener.itemClicked(movieDetail);
                }


            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        posterAdapter.clear();
        switch (item.getItemId()){
            case R.id.action_sort_popularity:

                loadMovieList(getString(R.string.sortby_popularity_id));
                return true;
            case R.id.action_sort_rating:

                loadMovieList(getString(R.string.sortby_rating_id));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void loadMovieList(String sortType){
        FetchMovieData fetchMovieData=new FetchMovieData(getActivity(),posterAdapter);
        ConnectivityManager connManager= (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(connManager!=null && networkInfo!=null){
            fetchMovieData.execute(sortType);

        }else {
            //Snackbar.make(getView(), R.string.no_connectivity, Snackbar.LENGTH_LONG)
             //           .show();

        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.listener=(MovieListListener)activity;
    }

    public static List<Movies> getFavouriteMovies(Context context)
    {
        List<Movies> movies = new ArrayList<>();
        String URL = MoviesProvider.URL;
        Uri movie = Uri.parse(URL);
        Cursor cursor = null;
        cursor = context.getContentResolver().query(movie, null, null, null, MoviesSQLiteHelper.ROW_ID);
        if (cursor != null) {
            while (cursor.moveToNext())
            {

                int id = cursor.getInt(cursor.getColumnIndex(MoviesSQLiteHelper.ID));
                String releaseDate = cursor.getString(cursor.getColumnIndex(MoviesSQLiteHelper.RELEASE_DATE));
                String posterPath = cursor.getString(cursor.getColumnIndex(MoviesSQLiteHelper.POSTERPATH_SQUARE));
                String backdropPath = cursor.getString(cursor.getColumnIndex(MoviesSQLiteHelper.POSTERPATH_WIDE));
                float usersRating = (float) cursor.getFloat(cursor.getColumnIndex(MoviesSQLiteHelper.VOTE_AVG));
                String originalTitle = cursor.getString(cursor.getColumnIndex(MoviesSQLiteHelper.TITLE));
                String synopsys = cursor.getString(cursor.getColumnIndex(MoviesSQLiteHelper.OVERVIEW));


                Movies movieDetails = new Movies(
                        id,
                        originalTitle,
                        synopsys,
                        releaseDate,
                        posterPath,
                        backdropPath,
                        usersRating);


            }
        }
        if(movies.size()==0) {
            Toast.makeText(context,context.getResources().getString(R.string.no_favourites),Toast.LENGTH_LONG).show();
        }
        return movies;
    }


}
