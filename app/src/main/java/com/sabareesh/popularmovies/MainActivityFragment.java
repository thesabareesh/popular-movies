package com.sabareesh.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.ArrayList;


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
            Snackbar.make(getView(), R.string.no_connectivity, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.listener=(MovieListListener)activity;
    }


}
