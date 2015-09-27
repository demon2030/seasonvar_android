package ru.seasonvar.seasonvarmobile.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Andrey_Demidenko on 4/14/2015 5:11 PM.
 */
public class MovieList implements Parcelable {
    private ArrayList<Movie> hasNewEpisodes;
    private ArrayList<Movie> old;

    public ArrayList<Movie> getHasNewEpisodes() {
        return hasNewEpisodes;
    }

    public void setHasNewEpisodes(ArrayList<Movie> hasNewEpisodes) {
        this.hasNewEpisodes = hasNewEpisodes;
    }

    public ArrayList<Movie> getOld() {
        return old;
    }

    public void setOld(ArrayList<Movie> old) {
        this.old = old;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableArray(hasNewEpisodes.toArray(new Movie[hasNewEpisodes.size()]), 0);
        dest.writeParcelableArray(old.toArray(new Movie[old.size()]), 0);
    }

    public MovieList(Parcel in) {
        hasNewEpisodes = new ArrayList<Movie>();
        old = new ArrayList<Movie>();
//        hasNewEpisodes.addAll((Collection<? extends Movie>) Arrays.asList(in.readParcelableArray(this.getClass().getClassLoader())));
    }

    public MovieList() {
    }
}
