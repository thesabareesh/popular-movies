package com.sabareesh.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by SABAREESH on 06-Feb-16.
 */
public class PosterAdapter extends ArrayAdapter<Movies> {

    public PosterAdapter(Activity context, List<Movies> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent){
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.poster_single, parent, false);
        }
        Movies movie = getItem(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.poster_imgview);
        //Picasso API
        Picasso.with(getContext()).load(movie.getPosterPath()).into(imageView);
        return convertView;
    }
}
