package ru.seasonvar.seasonvarmobile.activity;

import android.view.View;
import org.json.JSONException;

/**
 * Created by Andrey_Demidenko on 4/8/2015 11:41 AM.
 */
public interface NextEpisodeListener {
    public void onItemClickListener(View v, int position) throws JSONException;
}
