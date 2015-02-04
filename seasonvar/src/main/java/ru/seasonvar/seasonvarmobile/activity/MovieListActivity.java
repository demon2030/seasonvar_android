package ru.seasonvar.seasonvarmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import ru.seasonvar.seasonvarmobile.MovieAdapter;
import ru.seasonvar.seasonvarmobile.R;
import ru.seasonvar.seasonvarmobile.SeasonvarHttpClient;
import ru.seasonvar.seasonvarmobile.entity.Movie;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends Activity {

    private ListView listView;
    private final List<Movie> movieList = new ArrayList<Movie>();
    private MovieAdapter adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new MovieAdapter(movieList, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Movie m = adapter.getItem(position);
                new AsyncTask() {

                    private String url;

                    @Override
                    protected void onPostExecute(Object o) {
                        Log.i("good", "all good");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri videoUri = Uri.parse("https://www.dropbox.com/s/rki2gup2vp50erl/tetris.mp4?dl=1");
                        intent.setDataAndType(videoUri, "application/x-mpegURL");
                        intent.setPackage("com.mxtech.videoplayer.ad");
                        startActivity(intent);
                    }

                    @Override
                    protected Object doInBackground(Object[] params) {
                        Log.i("good?", "are all good?");
                        try {
                            url = SeasonvarHttpClient.getInstance().getVideoUrl(m, 1);
                        } catch (URISyntaxException e) {
                            Log.e("error", e.getMessage(), e);
                        } catch (IOException e) {
                            Log.e("error", e.getMessage(), e);
                        }
                        return null;
                    }
                }.execute();
            }
        });

        setProgressBarIndeterminateVisibility(true);
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                movieList.clear();
                movieList.addAll(SeasonvarHttpClient.getInstance().getMovieList());
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                adapter.notifyDataSetChanged();
                setProgressBarIndeterminateVisibility(false);
            }
        }.execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SeasonvarHttpClient.getInstance().close();
    }
}
