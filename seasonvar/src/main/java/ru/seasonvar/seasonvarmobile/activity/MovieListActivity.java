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
import android.widget.TabHost;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import ru.seasonvar.seasonvarmobile.R;
import ru.seasonvar.seasonvarmobile.SeasonvarHttpClient;
import ru.seasonvar.seasonvarmobile.activity.adapter.EpisodesAdapter;
import ru.seasonvar.seasonvarmobile.activity.adapter.MovieAdapter;
import ru.seasonvar.seasonvarmobile.entity.Movie;
import ru.seasonvar.seasonvarmobile.entity.MovieList;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends Activity {

    private static final int PLAY_CODE = 12345;
    private ListView listView;
    private ListView listView2;
    private MovieList movieList;

    private MovieAdapter adapter;
    private MovieAdapter adapter2;
    private Movie currentMovie = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.movie_list);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        } else {
            movieList = new MovieList();
            currentMovie = null;
        }
        listView = (ListView) findViewById(R.id.listView);
        listView2 = (ListView) findViewById(R.id.listView2);
        listView.setItemsCanFocus(false);
        listView2.setItemsCanFocus(false);
        adapter = new MovieAdapter(movieList.getHasNewEpisodes(), this);
        adapter2 = new MovieAdapter(movieList.getOld(), this);
        listView.setAdapter(adapter);
        listView2.setAdapter(adapter2);
        adapter.setNextEpisodeListener(getNextEpisodeListener(adapter));
        adapter2.setNextEpisodeListener(getNextEpisodeListener(adapter2));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Movie m = adapter.getItem(position);
                new AsyncTask() {

                    @Override
                    protected void onPostExecute(Object o) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MovieListActivity.this);
                        final String[] items = convertToCharSequences(m.getUrls());
                        builder.setAdapter(new EpisodesAdapter(MovieListActivity.this, items, m), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    currentMovie = m;
                                    ArrayList<Uri> list = new ArrayList<Uri>();
                                    int i = which;
                                    while (i >= 0) {
                                        list.add(Uri.parse(m.getUrls().get(i).getString("file")));
                                        i--;
                                    }
                                    openVideoPlayer(list, Uri.parse(m.getUrls().get(which).getString("file")));
                                } catch (JSONException e) {
                                    Log.e(this.getClass().getName(), e.getMessage(), e);
                                }
                            }
                        });
                        builder.create().show();
                    }

                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            if (m.getUrls() == null) {
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

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Movie m = adapter2.getItem(position);
                new AsyncTask() {

                    @Override
                    protected void onPostExecute(Object o) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MovieListActivity.this);
                        final String[] items = convertToCharSequences(m.getUrls());
                        builder.setAdapter(new EpisodesAdapter(MovieListActivity.this, items, m), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    currentMovie = m;
                                    ArrayList<Uri> list = new ArrayList<Uri>();
                                    int i = which;
                                    while (i >= 0) {
                                        list.add(Uri.parse(m.getUrls().get(i).getString("file")));
                                        i--;
                                    }
                                    openVideoPlayer(list, Uri.parse(m.getUrls().get(which).getString("file")));
                                } catch (JSONException e) {
                                    Log.e(this.getClass().getName(), e.getMessage(), e);
                                }
                            }
                        });
                        builder.create().show();
                    }

                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            if (m.getUrls() == null) {
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

        TabHost mTabHost = (TabHost)findViewById(R.id.tabHost);
        mTabHost.setup();

        setProgressBarIndeterminateVisibility(true);
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                if (movieList == null || movieList.getHasNewEpisodes() == null) {
                    movieList = SeasonvarHttpClient.getInstance().getMovieList();
                    adapter.setData(movieList.getHasNewEpisodes());
                    adapter2.setData(movieList.getOld());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                setProgressBarIndeterminateVisibility(false);
                adapter.notifyDataSetChanged();
            }
        }.execute();

    }

    private NextEpisodeListener getNextEpisodeListener(MovieAdapter adapter) {
        return new NextEpisodeListener(adapter) {
            @Override
            public void onItemClickListener(View v, int position) {
                final Movie m = getAdapter().getItem(position);
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            if (m.getUrls() == null) {
                                m.setUrls(SeasonvarHttpClient.getInstance().getSerialVideoList(m));
                            }
                        } catch (Exception e) {
                            Log.e(this.getClass().getName(), e.getMessage(), e);
                        }
                        return m.getUrls();
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        try {
                            if (m.getUrls() != null && m.getLastViewed() > -1) {
                                currentMovie = m;
                                ArrayList<Uri> list = new ArrayList<Uri>();
                                int i = m.getUrls().size() - m.getLastViewed() - 1;
                                Uri file = Uri.parse(m.getUrls().get(i).getString("file"));
                                while (i >= 0) {
                                    list.add(Uri.parse(m.getUrls().get(i).getString("file")));
                                    i--;
                                }
                                openVideoPlayer(list, file);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();


            }
        };
    }

    private void openVideoPlayer(ArrayList<Uri> list, Uri file) throws JSONException {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(file, "application/x-mpegURL");
        intent.putExtra("video_list", list.toArray(new Uri[list.size()]));
        intent.putExtra("return_result", true);
        try {
            intent.setPackage("com.mxtech.videoplayer.pro");
            startActivityForResult(intent, PLAY_CODE);
        } catch (Exception e) {
            intent.setPackage("com.mxtech.videoplayer.ad");
            startActivityForResult(intent, PLAY_CODE);
        }
    }

    private String [] convertToCharSequences(List<JSONObject> urls) {
        String [] episodes = new String[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            JSONObject url = urls.get(i);
            try {
                episodes[i] = url.getString("comment").replaceAll("<br>", " ");
            } catch (JSONException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
                episodes[i] = "";
            }
        }
        return episodes;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("currentMovie", currentMovie);
        outState.putParcelable("movieList", movieList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentMovie = savedInstanceState.getParcelable("currentMovie");
        movieList = savedInstanceState.getParcelable("movieList");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_CODE && data != null) {
            Uri lastFileUrl = data.getData();
            Integer position = (Integer) data.getExtras().get("position");
            Integer duration = (Integer) data.getExtras().get("duration");
            String endBy = (String) data.getExtras().get("end_by");
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

            if (episode > 0) {
                final int ep;
                if (endBy.equals("playback_completion")){
                    ep = episode;
                } else {
                    if (duration.equals(position)) {
                        ep = episode;
                    } else {
                        if (episode > currentMovie.getLastViewed() + 1) {
                            ep = episode - 1;
                        } else {
                            return;
                        }
                    }
                }

                Toast toast = Toast.makeText(MovieListActivity.this, "marking " + currentMovie.getTitle() + " episode " + ep, Toast.LENGTH_LONG);
                toast.show();
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        SeasonvarHttpClient.getInstance().markEpisode(currentMovie, ep);
                        return null;
                    }
                }.execute();

            }
        }
    }

}
