package com.sabareesh.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SABAREESH on 04-Apr-16.
 */

    public class Extras implements Parcelable{
        private Trailer[] trailers;
        private Review[] reviews;
        Extras(Trailer[] trailers){
            this.trailers = trailers;
        }

        Extras(Review[] reviews){
            this.reviews = reviews;
        }
        public int getTrailersNum(){return this.trailers.length;}
        public int getReviewsNum(){return this.reviews.length;}
        public Trailer getTrailerAtIndex(int i){return trailers[i];}
        public Review getReviewAtIndex(int i){return reviews[i];}


        @Override
        public int describeContents() {
            return 0;
        }
        private Extras(Parcel in){
            trailers= in.createTypedArray(Trailer.CREATOR);
            reviews = in.createTypedArray(Review.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedArray(trailers, 0);
            dest.writeTypedArray(reviews, 0);

        }
        public static final Parcelable.Creator<Extras> CREATOR = new Parcelable.Creator<Extras>() {
            @Override
            public Extras createFromParcel(Parcel parcel) {
                return new Extras(parcel);
            }

            @Override
            public Extras[] newArray(int i) {
                return new Extras[i];
            }

        };
    }

