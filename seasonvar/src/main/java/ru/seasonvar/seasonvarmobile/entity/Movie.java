package ru.seasonvar.seasonvarmobile.entity;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey_Demidenko on 2/2/2015 3:26 PM.
 */
public class Movie implements Parcelable {
    private String id;
    private String title;
    private String season;
    private String current;
    private String link;
    private String img;
    private String lastDate;
    private String lastUpdate;

    private int lastViewed = -1;

    private List<JSONObject> urls;
    private JSONObject episodesMap;

    public Movie() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
        try {
            setLastViewed(Integer.parseInt(current.substring(0, current.indexOf(" "))));
        } catch (Exception e) {

        }
    }

    public String getLink() {
        return link.split("#")[0];
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<JSONObject> getUrls() {
        return urls;
    }

    public void setUrls(List<JSONObject> urls) {
        this.urls = urls;
    }

    public JSONObject getEpisodesMap() {
        return episodesMap;
    }

    public void setEpisodesMap(JSONObject episodesMap) {
        this.episodesMap = episodesMap;
    }

    public int getLastViewed() {
        return lastViewed;
    }

    public void setLastViewed(int lastViewed) {
        this.lastViewed = lastViewed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(season);
        dest.writeString(current);
        dest.writeString(link);
        dest.writeString(img);
        dest.writeString(lastDate);
        dest.writeString(lastUpdate);
        dest.writeInt(lastViewed);
        List<String> u = new ArrayList<String>();
        if (urls != null){
            for (JSONObject url : urls) {
                u.add(url.toString());
            }
        }
        dest.writeStringList(u);
        dest.writeString(episodesMap != null ? episodesMap.toString(): "null");
    }

    public Movie(Parcel in) throws JSONException {
        id = in.readString();
        title = in.readString();
        season = in.readString();
        current = in.readString();
        link = in.readString();
        img = in.readString();
        lastDate = in.readString();
        lastUpdate = in.readString();
        lastViewed = in.readInt();
        List<String> u = new ArrayList<String>();
        in.readStringList(u);
        if (!u.isEmpty()){
            urls = new ArrayList<JSONObject>();
            for (String s : u) {
                urls.add(new JSONObject(s));
            }
        }
        String s = in.readString();
        if (!s.equals("null")){
            episodesMap = new JSONObject(s);
        }
    }



    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            try {
                return new Movie(in);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
