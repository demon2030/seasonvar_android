package ru.seasonvar.seasonvarmobile.entity;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Andrey_Demidenko on 2/2/2015 3:26 PM.
 */
public class Movie {
    private String id;
    private String title;
    private String season;
    private String current;
    private String link;
    private String img;
    private String lastDate;
    private String lastUpdate;

    private int lastViewed;

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
        try{
            setLastViewed(Integer.parseInt(current.substring(0, current.indexOf(" "))));
        } catch (Exception e){

        }
    }

    public String getLink() {
        return link;
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
}
