package com.ffcc66.sxyj.response.entity;


import java.io.Serializable;

public class ResponseBook implements Serializable {

    private int id;
    private String name;
    private String author;
    private String type;
    private String introduction;
    private String cover_img;
    private String file;
    private int wordcount;
    private int looknum;
    private int collectionnum;
    private int searchnum;

    public ResponseBook() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public String getCover_img() {
        return cover_img;
    }
    public void setCover_img(String cover_img) {
        this.cover_img = cover_img;
    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }
    public int getWordcount() {
        return wordcount;
    }
    public void setWordcount(int wordcount) {
        this.wordcount = wordcount;
    }
    public int getLooknum() {
        return looknum;
    }
    public void setLooknum(int looknum) {
        this.looknum = looknum;
    }
    public int getCollectionnum() {
        return collectionnum;
    }
    public void setCollectionnum(int collectionnum) {
        this.collectionnum = collectionnum;
    }

    public int getSearchnum() {
        return searchnum;
    }

    public void setSearchnum(int searchnum) {
        this.searchnum = searchnum;
    }

}
