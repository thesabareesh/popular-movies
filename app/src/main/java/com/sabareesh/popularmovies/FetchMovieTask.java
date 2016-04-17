package com.sabareesh.popularmovies;

/**
 * Created by SABAREESH on 3/20/2016.
 */

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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


    public class FetchMovieTask extends AsyncTask<String, Void,List<Movies>> {

        ArrayList<Extras> extrasArray;
        private List<Movies> moviesList;
        private ArrayAdapter<Movies> adapter;
        private final Context mContext;
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        public FetchMovieTask(Context context, ArrayAdapter<Movies> moviesArrayAdapter) {
            mContext = context;
            adapter = moviesArrayAdapter;
        }

        private List<Movies> parseDataFromJson(String moviesRawJSON) throws JSONException {

            //JSON object keys
            final String JSON_RESULTS = mContext.getResources().getString(R.string.json_key_results);
            final String JSON_ID = mContext.getString(R.string.json_key_id);
            final String JSON_ORIGINAL_TITLE = mContext.getString(R.string.json_key_title);
            final String JSON_OVERVIEW = mContext.getString(R.string.json_key_overview);
            final String JSON_RELEASE_DATE = mContext.getString(R.string.json_key_release_date);
            final String JSON_POSTER_PATH = mContext.getString(R.string.json_key_poster_path);
            final String JSON_BACKDROP_PATH = mContext.getString(R.string.json_key_backdrop_path);
            final String JSON_VOTE_AVERAGE = mContext.getString(R.string.json_key_vote_average);

            JSONObject movieJson = new JSONObject(moviesRawJSON);
            JSONArray movieArray = movieJson.getJSONArray(JSON_RESULTS);
            moviesList = new ArrayList<>(movieArray.length());

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
        protected List<Movies> doInBackground(String... params) {
            String moviesRawJSON = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String DOMAIN = mContext.getString(R.string.base_url);
            final String MOVIE_BASE_URL = DOMAIN+"/"+params[0];
            final String QUERY_PARAM = mContext.getString(R.string.query_param);
            final String APPKEY_PARAM = mContext.getString(R.string.api_key);

            try {
                Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(APPKEY_PARAM, mContext.getString(R.string.api_value))
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
                adapter.clear();
                for (Movies movie : movieDataList) {
                    adapter.add(movie);
                }
            }
        }
    }



