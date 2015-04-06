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
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Andrey_Demidenko on 2/2/2015 4:45 PM.
 */
public class MovieAdapter extends BaseAdapter {

    private List<Movie> data;
    private Activity activity;
    private final ImageDownloader imageDownloader = new ImageDownloader();

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
//            new DownloadImageTask(holder.imageView).execute(m.getImg());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView titleView;
        TextView seasonView;
        TextView currentView;
        TextView updateView;
        ImageView imageView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public DownloadImageTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
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

        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
//                    } else {
//                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.list_placeholder);
//                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }
}
