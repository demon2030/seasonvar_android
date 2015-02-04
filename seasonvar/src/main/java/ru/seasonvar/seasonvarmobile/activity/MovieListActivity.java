package ru.seasonvar.seasonvarmobile.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import ru.seasonvar.seasonvarmobile.MovieAdapter;
import ru.seasonvar.seasonvarmobile.R;
import ru.seasonvar.seasonvarmobile.SeasonvarHttpClient;
import ru.seasonvar.seasonvarmobile.entity.Movie;

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
                Movie m = adapter.getItem(position);

            }
        });

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
            }
        }.execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SeasonvarHttpClient.getInstance().close();
    }
}
