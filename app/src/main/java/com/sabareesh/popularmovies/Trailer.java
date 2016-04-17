package com.sabareesh.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SABAREESH on 04-Apr-16.
 */
public class Trailer implements Parcelable{
    private String title;
    private String source;

     Trailer(String title,String source){
        this.title = title;
        this.source = source;
    }

    public String getTitle(){return title;}
    public String getSource(){return source;}

    @Override
    public int describeContents() {
        return 0;
    }

    public Trailer(Parcel in){
        title = in.readString();
        source = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(source);
    }
    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }

    };
}
