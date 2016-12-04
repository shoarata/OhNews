package com.example.android.ohnews;

/**
 * Created by arata on 29/11/2016.
 */

public class News {
    private String title;
    private String section;
    private String url;
    private String date;

    public News(String title, String section, String url, String date){
        this.title = title;
        this.section = section;
        this.url = url;
        this.date = date;
    }
    public String getTitle(){
        return title;
    }
    public String getSection(){
        return section;
    }
    public String getUrl(){
        return url;
    }
    public String getDate(){
        return date;
    }

}
