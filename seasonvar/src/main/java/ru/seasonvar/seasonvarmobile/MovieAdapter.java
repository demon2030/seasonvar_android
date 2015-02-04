package ru.seasonvar.seasonvarmobile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ru.seasonvar.seasonvarmobile.entity.Movie;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Andrey_Demidenko on 2/2/2015 4:45 PM.
 */
public class MovieAdapter extends BaseAdapter {

    private List<Movie> data;
    private Activity activity;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.movie_list_item, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView season = (TextView) convertView.findViewById(R.id.season);
        TextView current = (TextView) convertView.findViewById(R.id.current);
        TextView update = (TextView) convertView.findViewById(R.id.update);
        ImageView img = (ImageView) convertView.findViewById(R.id.imageView);

        Movie m = getItem(position);

        title.setText(m.getTitle());
        current.setText(m.getCurrent());
        season.setText(m.getSeason());
        update.setText(m.getLastUpdate() + " - " + m.getLastDate());
//        new DownloadImageTask(img).execute(m.getImg());
        return convertView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
