package ru.seasonvar.seasonvarmobile;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.seasonvar.seasonvarmobile.entity.Movie;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SeasonvarHttpClient {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36";
    private static CloseableHttpClient httpClient;
    private static SeasonvarHttpClient instance = new SeasonvarHttpClient();

    private SeasonvarHttpClient() {
        BasicCookieStore cookieStore = new BasicCookieStore();
        httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    public boolean login(final String login, final String password){
        boolean result = false;
        try {
            HttpUriRequest httpget = RequestBuilder.get()
                    .setUri(new URI("http://seasonvar.ru/?mod=login"))
                    .addHeader("User-Agent", USER_AGENT)
                    .build();
            httpClient.execute(httpget).close();

            HttpUriRequest loginForm = RequestBuilder.post()
                    .setUri(new URI("http://seasonvar.ru/?mod=login"))
                    .addParameter("login", login)
                    .addParameter("password", password)
                    .addHeader("User-Agent", USER_AGENT)
                    .build();
            CloseableHttpResponse response = httpClient.execute(loginForm);
            result = response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 302;
            response.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);
        }
        return result;
    }

    public static SeasonvarHttpClient getInstance(){
        return instance;
    }

    public List<Movie> getMovieList(){
        List<Movie> result = new ArrayList<Movie>();
        try {
            HttpUriRequest pause = RequestBuilder.get()
                    .setUri(new URI("http://seasonvar.ru/?mod=pause"))
                    .addHeader("User-Agent", USER_AGENT)
                    .build();
            CloseableHttpResponse response3 = httpClient.execute(pause);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                HttpEntity entity = response3.getEntity();
                entity.consumeContent();
                entity.writeTo(outputStream);
            } finally {
                response3.close();
            }
            String utf8 = outputStream.toString("UTF8");
            Elements elements = Jsoup.parse(utf8).select("div.section > div > div.newrap, div.mark_col");
            for (Element element : elements) {
                Log.d("Parser", element.text());
                Movie movie = new Movie();
                movie.setId(element.id());
                movie.setLink(element.select("a").first().attr("href"));
                movie.setImg(element.select("img.img").first().attr("src"));
                movie.setTitle(element.select(".title").first().text());
                movie.setSeason(element.select(".season").first().text());
                movie.setLastDate(element.select(".last").first().text());
                Elements translate = element.select(".lastupd, .translate");
                if (translate.size() > 0){
                    movie.setLastUpdate(translate.first().text());
                }
                result.add(movie);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);
        }
        return result;
    }
    public String getVideoUrl(Movie m, int episode) throws URISyntaxException, IOException {
        HttpUriRequest req = RequestBuilder.get()
                .setUri(new URI(m.getLink()))
                .addHeader("User-Agent", USER_AGENT)
                .build();
        CloseableHttpResponse response = httpClient.execute(req);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            HttpEntity entity = response.getEntity();
            entity.consumeContent();
            entity.writeTo(outputStream);
        } finally {
            response.close();
        }
        String html = outputStream.toString("UTF8");
        //http://seasonvar.ru/?wserial=no&0.12871506065130234
        //http://seasonvar.ru/plStat.php
        //http://temp-cdn.datalock.ru/crossdomain.xml
        //http://temp-cdn.datalock.ru/fi2lm/2c672de96f114872f4a9e49b5908c03c/7f_greys.anatomy.s11e01.rus.hdtv.1080p.foxlife.a1.06.11.14.mp4
        Jsoup.parse(html).select("");

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.parse(videoPath), "video/mp4");
//        startActivity(intent);
        //http://stackoverflow.com/questions/14559406/launch-mx-player-through-intent
        return "";
    }

    public void close(){
        try {
            httpClient.close();
        } catch (IOException e) {
            Log.e("Error", e.getMessage(), e);
        }
    }
}
