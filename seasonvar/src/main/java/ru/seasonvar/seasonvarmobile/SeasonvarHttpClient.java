package ru.seasonvar.seasonvarmobile;

import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.seasonvar.seasonvarmobile.entity.Movie;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeasonvarHttpClient {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36";
    private static CloseableHttpClient httpClient;
    private static SeasonvarHttpClient instance = new SeasonvarHttpClient();
    private final BasicCookieStore cookieStore;

    private SeasonvarHttpClient() {
        cookieStore = new BasicCookieStore();
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
                try {
                    movie.setSeason(element.select(".season").first().text());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    public List<JSONObject> getSerialVideoList(Movie m) throws URISyntaxException, IOException, JSONException {
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        cookieStore.addCookie(new BasicClientCookie("html5default", "1"));
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
        Elements elements = Jsoup.parse(html).select("script");
        String script = "";
        for (Element el : elements) {
            if (el.outerHtml().contains("$(\"#videoplayer719\").load(\"player.php\"")){
                script = el.outerHtml();
                break;
            }
        }
        Pattern p = Pattern.compile("secure\\\"\\: \"(\\w+)\\\"\\}\\);");

        Matcher matcher = p.matcher(script);
        matcher.find();
        String secure = matcher.group(1);

        p = Pattern.compile("serial\\\"\\: \"(\\w+)\"");
        matcher = p.matcher(script);
        matcher.find();
        String serial = matcher.group(1);

        req = RequestBuilder.post()
                .setUri(new URI("http://seasonvar.ru/player.php"))
                .addParameter("id", m.getId().substring(1))
                .addParameter("serial", serial)
                .addParameter("type", "html5")
                .addParameter("secure", secure)
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build();
        response = httpClient.execute(req);
        outputStream = new ByteArrayOutputStream();
        try {
            HttpEntity entity = response.getEntity();
            entity.consumeContent();
            entity.writeTo(outputStream);
        } finally {
            response.close();
        }
        String playerPHP = outputStream.toString("UTF8");
        p = Pattern.compile("pl\":\"(.+?)\"");
        matcher = p.matcher(playerPHP);
        matcher.find();
        String xmlUrl = "http://seasonvar.ru" + matcher.group(1);

        p = Pattern.compile("var arFiles = \\{(.+?)\\}");
        matcher = p.matcher(playerPHP);
        matcher.find();
        String fix = "{" + matcher.group(1) + "}";
        JSONObject fixMap = new JSONObject(fix);


        req = RequestBuilder.get()
                .setUri(new URI(xmlUrl))
                .addHeader("User-Agent", USER_AGENT)
                .build();
        response = httpClient.execute(req);
        outputStream = new ByteArrayOutputStream();
        try {
            HttpEntity entity = response.getEntity();
            entity.consumeContent();
            entity.writeTo(outputStream);
        } finally {
            response.close();
        }
        String file = outputStream.toString();
        JSONObject json = new JSONObject(file);
        JSONArray playlist = json.getJSONArray("playlist");
        for (int i=0; i < playlist.length(); i++){
            JSONObject episode = playlist.getJSONObject(i);
            list.add(episode);
            String f = episode.getString("file");
            f = f.substring(0, f.lastIndexOf("/")+1);
            String code = episode.getString("galabel");
            code = code.substring(code.indexOf("_")+1);
            Iterator keys = fixMap.keys();
            while (keys.hasNext()){
                String key = (String) keys.next();
                if (fixMap.getString(key).equals(code)){
                    f =f+key;
                    break;
                }
            }
            episode.put("file", f);
        }

        return list;
    }

    public void close(){
        try {
            httpClient.close();
        } catch (IOException e) {
            Log.e("Error", e.getMessage(), e);
        }
    }
}
