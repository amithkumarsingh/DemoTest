package com.whitecoats.model;

public class CommunicationListModel {

    private String title;
    private String category;
    private String date;
    private String contentPath;
    private int articlesId;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String desc;

    public int getArticlesId() {
        return articlesId;
    }

    public void setArticlesId(int articlesId) {
        this.articlesId = articlesId;
    }

    private int articlesType;

    public int getArticlesType() {
        return articlesType;
    }

    public void setArticlesType(int articlesType) {
        this.articlesType = articlesType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent_value() {
        return content_value;
    }

    public void setContent_value(String content_value) {
        this.content_value = content_value;
    }

    private String content_value;

}
