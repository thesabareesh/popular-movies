package com.sabareesh.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.sabareesh.popularmovies.utils.MovieUtils;

import icepick.Icepick;
import icepick.State;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    @State String mSortCriteria="popular";

    GridView gridView;
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private PosterAdapter posterAdapter = null;
    MovieListListener listener;
    ViewGroup rootView;

    static interface MovieListListener{
        public void itemClicked(Movies movieDetail);
    }

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       rootView  =(ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        gridView = (GridView) rootView.findViewById(R.id.grid_posters);
        posterAdapter = new PosterAdapter(getActivity(), new ArrayList<Movies>());
        gridView.setAdapter(posterAdapter);
        Icepick.restoreInstanceState(this, savedInstanceState);
        if(savedInstanceState!=null){
            String SORT_BY_FAV = "favorites";
            if(mSortCriteria.equals(SORT_BY_FAV)){
                loadFavoriteMovies(getActivity(), posterAdapter);
            }else{
                loadMovieList(mSortCriteria);

            }
        }
        else{
            loadMovieList(getString(R.string.sortby_popularity_id));
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    Movies movieDetail = posterAdapter.getItem(position);
                    listener.itemClicked(movieDetail);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        posterAdapter.clear();
        switch (item.getItemId()){
            case R.id.action_sort_popularity:
                mSortCriteria=getString(R.string.sortby_popularity_id);
                loadMovieList(mSortCriteria);
                return true;

            case R.id.action_sort_rating:
                mSortCriteria=getString(R.string.sortby_rating_id);
                loadMovieList(mSortCriteria);
                return true;

            case R.id.action_sort_favorites:
                mSortCriteria=getString(R.string.sortby_favorites_id);
                loadFavoriteMovies(getActivity(), posterAdapter);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void loadMovieList(String sortType){
        FetchMovieTask fetchMovieTask=new FetchMovieTask(getActivity(),posterAdapter);
        if(MovieUtils.checkInternetConnection(getActivity())){
            fetchMovieTask.execute(sortType);
        }else {
            Toast.makeText(getContext(),R.string.no_connectivity,Toast.LENGTH_SHORT).show();
        }
    }

    protected void loadFavoriteMovies(Context context,PosterAdapter adapter){
        List<Movies> movieDataList=getFavoriteMovies(getActivity());
        if (movieDataList != null) {
            adapter.clear();
            for (Movies movie : movieDataList) {
                adapter.add(movie);
            }
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.listener=(MovieListListener)activity;
    }

    public  List<Movies> getFavoriteMovies(Context context) {
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

                movies.add(movieDetails);
            }
        }
        if(movies.size()==0) {
            Toast.makeText(context,context.getResources().getString(R.string.no_favourites),Toast.LENGTH_SHORT).show();
        }
            return movies;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);

    }
}
