package com.ffcc66.sxyj.entity;

import org.litepal.crud.DataSupport;

public class SearchHistory extends DataSupport {

    private int id;
    private String username = "anyone";
    private String searchword;

    public SearchHistory(String username, String searchword) {
        this.username = username;
        this.searchword = searchword;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSearchword() {
        return searchword;
    }

    public void setSearchword(String searchword) {
        this.searchword = searchword;
    }
}
