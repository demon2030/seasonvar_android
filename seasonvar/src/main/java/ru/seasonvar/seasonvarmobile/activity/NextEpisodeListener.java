package ru.seasonvar.seasonvarmobile.activity;

import android.view.View;
import org.json.JSONException;
import ru.seasonvar.seasonvarmobile.activity.adapter.MovieAdapter;

/**
 * Created by Andrey_Demidenko on 4/8/2015 11:41 AM.
 */
public abstract class NextEpisodeListener {
    private MovieAdapter adapter;
    public NextEpisodeListener(MovieAdapter adapter) {
        this.adapter = adapter;
    }

    public MovieAdapter getAdapter() {
        return adapter;
    }

    abstract public void onItemClickListener(View v, int position) throws JSONException;
}
