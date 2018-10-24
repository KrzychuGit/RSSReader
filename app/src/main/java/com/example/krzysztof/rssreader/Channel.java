package com.example.krzysztof.rssreader;

public class Channel {


    private String title;
    private String url;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Channel(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
