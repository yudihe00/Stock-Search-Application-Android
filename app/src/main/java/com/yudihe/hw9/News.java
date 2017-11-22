package com.yudihe.hw9;

/**
 * Created by heyudi on 11/21/17.
 */

public class News {
    private String title;
    private String link;
    private String pubDate;
    private String authorName;

    public News(String title, String link, String pubDate, String authorName){
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.authorName = authorName;
    }

    public String getTitle(){
        return title;
    }

    public String getLink(){
        return link;
    }
    public String getPubDate(){
        return pubDate;
    }
    public String getAuthorName(){
        return authorName;
    }

}
