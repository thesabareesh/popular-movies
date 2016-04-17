package com.sabareesh.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SABAREESH on 06-Feb-16.
 */
public class Movies implements Parcelable {
     int mPosterId;
     String mTitle;
     String mSynopsys;
     String mReleaseDate;
     String mPosterPath;
     String mBackdropPath;
     float mUsersRating;
    private Extras mExtra;
    transient boolean isFavorite = false;

    public Movies(int posterId, String title, String synopsis, String releaseDate, String posterPath,String backdropPath,float rating) {
        this.mPosterId = posterId;
        this.mTitle = title;
        this.mSynopsys = synopsis;
        this.mReleaseDate = releaseDate;
        this.mPosterPath = posterPath;
        this.mBackdropPath=backdropPath;
        this.mUsersRating = rating;
    }

    // to deparcel object
    public Movies(Parcel in) {
        mPosterId = in.readInt();
        mTitle = in.readString();
        mSynopsys = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
        mBackdropPath = in.readString();
        mUsersRating = in.readFloat();
        mExtra = in.readParcelable(Extras.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPosterId);
        dest.writeString(mTitle);
        dest.writeString(mSynopsys);
        dest.writeString(mReleaseDate);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdropPath);
        dest.writeFloat(mUsersRating);
        dest.writeParcelable(mExtra, 0);
    }


    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public String getPosterPath() {
        final String basePath = "http://image.tmdb.org/t/p/w185//";
        return basePath + mPosterPath;
    }
    public String getBackdropPath() {
        final String basePath = "http://image.tmdb.org/t/p/w500//";
        return basePath + mBackdropPath;
    }
    public void setExtras(Extras extra){
        this.mExtra = extra;

    }

}
