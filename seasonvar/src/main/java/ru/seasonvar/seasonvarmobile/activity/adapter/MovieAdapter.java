package ru.seasonvar.seasonvarmobile.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import ru.seasonvar.seasonvarmobile.activity.utils.ImageDownloader;
import ru.seasonvar.seasonvarmobile.activity.NextEpisodeListener;
import ru.seasonvar.seasonvarmobile.R;
import ru.seasonvar.seasonvarmobile.entity.Movie;

import java.util.List;

/**
 * Created by Andrey_Demidenko on 2/2/2015 4:45 PM.
 */
public class MovieAdapter extends BaseAdapter{

    private List<Movie> data;
    private Activity activity;
    private final ImageDownloader imageDownloader = new ImageDownloader();
    private NextEpisodeListener nextEpisodeListener;

    public MovieAdapter(List<Movie> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Movie getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.movie_list_item, parent, false);
            holder.titleView = (TextView) convertView.findViewById(R.id.title);
            holder.seasonView = (TextView) convertView.findViewById(R.id.season);
            holder.currentView = (TextView) convertView.findViewById(R.id.current);
            holder.updateView = (TextView) convertView.findViewById(R.id.update);
            holder.imageView= (ImageView) convertView.findViewById(R.id.imageView);
            holder.viewNext = (ImageButton) convertView.findViewById(R.id.viewNextEpisode);
            holder.viewNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        nextEpisodeListener.onItemClickListener(v, position);
                    } catch (JSONException e) {
                        Log.e(MovieAdapter.class.getName(), e.getMessage(), e);
                    }
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Movie m = getItem(position);

        holder.titleView.setText(m.getTitle());
        holder.currentView.setText(m.getCurrent());
        holder.seasonView.setText(m.getSeason());
        holder.updateView.setText(m.getLastUpdate() + " - " + m.getLastDate());
        if (holder.imageView!= null){
            imageDownloader.download(m.getImg(), holder.imageView);
        }

        holder.imageView.setFocusable(false);
        holder.imageView.setFocusableInTouchMode(false);
        return convertView;
    }

    public void setNextEpisodeListener(NextEpisodeListener nextEpisodeListener) {
        this.nextEpisodeListener = nextEpisodeListener;
    }

    static class ViewHolder {
        TextView titleView;
        TextView seasonView;
        TextView currentView;
        TextView updateView;
        ImageView imageView;
        ImageButton viewNext;
    }
}
