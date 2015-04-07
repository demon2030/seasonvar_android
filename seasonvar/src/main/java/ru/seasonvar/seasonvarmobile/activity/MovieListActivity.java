package ru.seasonvar.seasonvarmobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import org.json.JSONException;
import org.json.JSONObject;
import ru.seasonvar.seasonvarmobile.MovieAdapter;
import ru.seasonvar.seasonvarmobile.R;
import ru.seasonvar.seasonvarmobile.SeasonvarHttpClient;
import ru.seasonvar.seasonvarmobile.entity.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends Activity {

    private static final int PLAY_CODE = 12345;
    private ListView listView;
    private final List<Movie> movieList = new ArrayList<Movie>();
    private MovieAdapter adapter;

    private Movie currentMovie = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new MovieAdapter(movieList, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Movie m = adapter.getItem(position);
                new AsyncTask() {

//                    private List<JSONObject> urls;

                    @Override
                    protected void onPostExecute(Object o) {
                        CharSequence[] episodes = new CharSequence[m.getUrls().size()];
                        for (int i = 0; i < m.getUrls().size(); i++) {
                            JSONObject url = m.getUrls().get(i);
                            try {
                                episodes[i] = url.getString("comment").replaceAll("<br>", " ");
                            } catch (JSONException e) {
                                Log.e(this.getClass().getName(), e.getMessage(), e);
                                episodes[i] = "";
                            }
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(MovieListActivity.this);
                        builder.setItems(episodes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    ArrayList<Uri> list = new ArrayList<Uri>();
                                    int i = which;
                                    while (i >= 0){
                                        list.add(Uri.parse(m.getUrls().get(i).getString("file")));
                                        i--;
                                    }
                                    intent.setDataAndType(Uri.parse(m.getUrls().get(which).getString("file")), "application/x-mpegURL");
                                    intent.putExtra("video_list", list.toArray(new Uri[list.size()]));
                                    intent.putExtra("return_result", true);

                                    currentMovie = m;
                                    try {
                                        intent.setPackage("com.mxtech.videoplayer.pro");
                                        startActivityForResult(intent, PLAY_CODE);
                                    } catch (Exception e){
                                        intent.setPackage("com.mxtech.videoplayer.ad");
                                        startActivityForResult(intent, PLAY_CODE);
                                    }
                                } catch (JSONException e) {
                                    Log.e(this.getClass().getName(), e.getMessage(), e);
                                }
                            }
                        });
                        builder.create().show();
                    }

                    @Override
                    protected Object doInBackground(Object[] params) {
                        Log.i("good?", "are all good?");
                        try {
                            if (m.getUrls() == null){
                                m.setUrls(SeasonvarHttpClient.getInstance().getSerialVideoList(m));
                            }
                        } catch (Exception e) {
                            Log.e(this.getClass().getName(), e.getMessage(), e);
                        }
                        return m.getUrls();
                    }
                }.execute();
            }
        });

        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                setProgressBarIndeterminateVisibility(true);
                movieList.clear();
                movieList.addAll(SeasonvarHttpClient.getInstance().getMovieList());
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                setProgressBarIndeterminateVisibility(false);
                adapter.notifyDataSetChanged();
            }
        }.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_CODE && data != null){
            Uri lastFileUrl = data.getData();
            int episode = -1;
            List<JSONObject> urls = currentMovie.getUrls();
            for (int i = 0; i < urls.size(); i++) {
                JSONObject json = urls.get(i);
                try {
                    if (json.getString("file").equals(lastFileUrl.toString())) {
                        episode = urls.size() - i;
                        break;
                    }
                } catch (JSONException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                }
            }
            if (episode > 0){
                final int ep = episode;
                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] params) {
                        SeasonvarHttpClient.getInstance().markEpisode(currentMovie, ep);
                        return null;
                    }
                }.execute();

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SeasonvarHttpClient.getInstance().close();
    }
}
