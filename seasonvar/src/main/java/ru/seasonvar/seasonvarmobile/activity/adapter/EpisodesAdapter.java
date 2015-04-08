package ru.seasonvar.seasonvarmobile.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.seasonvar.seasonvarmobile.R;
import ru.seasonvar.seasonvarmobile.activity.MovieListActivity;
import ru.seasonvar.seasonvarmobile.entity.Movie;

/**
* Created by Andrey_Demidenko on 4/8/2015 5:21 PM.
*/
public class EpisodesAdapter extends BaseAdapter {
    private MovieListActivity movieListActivity;
    private final String[] items;
    private final Movie m;

    public EpisodesAdapter(MovieListActivity movieListActivity, String[] items, Movie m) {
        this.movieListActivity = movieListActivity;
        this.items = items;
        this.m = m;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView episodeName;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) movieListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.episode_list_item, parent, false);
            episodeName = (TextView) convertView.findViewById(R.id.episodeName);
            convertView.setTag(episodeName);
        } else {
            episodeName = (TextView) convertView.getTag();
        }
        episodeName.setText(items[position]);
        int i = m.getUrls().size() - m.getLastViewed() - 1;
        if (position > i){
            episodeName.setTextColor(Color.GRAY);
        } else {
            if (position == i){
                episodeName.setTextColor(Color.RED);
            } else {
                episodeName.setTextColor(Color.GREEN);
            }
        }
        return convertView;
    }
}
