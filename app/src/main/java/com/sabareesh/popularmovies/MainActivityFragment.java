package com.sabareesh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private PosterAdapter posterAdapter = null;

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
                Movies movieDetail = posterAdapter.getItem(position);
                Intent detailActivity= new Intent(getActivity(),DetailActivity.class);
                detailActivity.putExtra("movieDetail",movieDetail);
                startActivity(detailActivity);

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
        ConnectivityManager connManager= (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(connManager!=null && networkInfo!=null){
            new FetchMovieData().execute(sortType);
        }else {
            Snackbar.make(getView(), R.string.no_connectivity, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private class FetchMovieData extends AsyncTask<String, Void,List<Movies>>{

        private List<Movies> parseDataFromJson(String moviesRawJSON) throws JSONException{

            //JSON object keys
            final String JSON_RESULTS = getString(R.string.json_key_results);
            final String JSON_ID = getString(R.string.json_key_id);
            final String JSON_ORIGINAL_TITLE = getString(R.string.json_key_title);
            final String JSON_OVERVIEW = getString(R.string.json_key_overview);
            final String JSON_RELEASE_DATE = getString(R.string.json_key_release_date);
            final String JSON_POSTER_PATH = getString(R.string.json_key_poster_path);
            final String JSON_BACKDROP_PATH=getString(R.string.json_key_backdrop_path);
            final String JSON_VOTE_AVERAGE = getString(R.string.json_key_vote_average);

            JSONObject movieJson = new JSONObject(moviesRawJSON);
            JSONArray movieArray = movieJson.getJSONArray(JSON_RESULTS);
            List<Movies> moviesList = new ArrayList<>(movieArray.length());
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieToAdd = movieArray.getJSONObject(i);
                int id = movieToAdd.getInt(JSON_ID);
                String releaseDate = movieToAdd.getString(JSON_RELEASE_DATE);
                String posterPath = movieToAdd.getString(JSON_POSTER_PATH);
                String backdropPath = movieToAdd.getString(JSON_BACKDROP_PATH);
                float usersRating = (float) movieToAdd.getDouble(JSON_VOTE_AVERAGE);
                String originalTitle = movieToAdd.getString(JSON_ORIGINAL_TITLE);
                String synopsys = movieToAdd.getString(JSON_OVERVIEW);


                Movies movieDetails = new Movies(
                        id,
                        originalTitle,
                        synopsys,
                        releaseDate,
                        posterPath,
                        backdropPath,
                        usersRating);
                moviesList.add(movieDetails);
            }
            return moviesList;
        }

        @Override
        protected List<Movies> doInBackground(String... params){
            String moviesRawJSON=null;
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            final String MOVIES_BASE_URL = getString(R.string.base_url);
            final String QUERY_PARAM = getString(R.string.query_param);
            final String APPKEY_PARAM = getString(R.string.api_key);

            try {
                Uri uri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(APPKEY_PARAM, getString(R.string.api_value))
                        .build();

                URL url = new URL(uri.toString());
                Log.i(LOG_TAG, "URI built " + uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                // Connection start
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging easier
                    buffer.append(line);
                    buffer.append("\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                moviesRawJSON = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return parseDataFromJson(moviesRawJSON);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movies> movieDataList) {
            super.onPostExecute(movieDataList);
            if (movieDataList != null) {
               posterAdapter.clear();
                for (Movies movie : movieDataList) {
                    posterAdapter.add(movie);
                }
            }
        }

    }
}
