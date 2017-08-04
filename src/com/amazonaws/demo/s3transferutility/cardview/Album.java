package com.amazonaws.demo.s3transferutility.cardview;

import java.io.Serializable;

/**
 * Created by Lincoln on 18/05/16.
 */
public class Album implements Serializable {
    private String name;
    private int numOfSongs;
    private String thumbnail;
    private String link;



    public Album() {

    }

    public Album(String name, int numOfSongs, String thumbnail, String link) {
        this.name = name;
        this.numOfSongs = numOfSongs;
        this.thumbnail = thumbnail;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfSongs() {
        return numOfSongs;
    }

    public void setNumOfSongs(int numOfSongs) {
        this.numOfSongs = numOfSongs;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
