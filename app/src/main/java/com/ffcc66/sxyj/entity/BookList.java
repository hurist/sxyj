package com.ffcc66.sxyj.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2015/12/27.
 */
public class BookList extends DataSupport implements Serializable {
    private int id;     //id，也是图书在书架中的位置
    private String coverpath;
    private String bookname;    //图书名称
    private String bookpath;    //文件存储位置
    private long begin;         //开始位置
    private float readprocess = (float) 0;
    private long lastreadtime;   //最后一次阅读的时间
    private String charset;     //文件编码

    public String getBookname() {
        return this.bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getBookpath() {
        return this.bookpath;
    }

    public void setBookpath(String bookpath) {
        this.bookpath = bookpath;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public long getLastreadtime() {
        return lastreadtime;
    }

    public void setLastreadtime(long lastestreadtime) {
        this.lastreadtime = lastestreadtime;
    }

    public String getCoverpath() {
        return coverpath;
    }

    public void setCoverpath(String coverpath) {
        this.coverpath = coverpath;
    }

    public float getReadprocess() {
        return readprocess;
    }

    public void setReadprocess(float readprocess) {
        this.readprocess = readprocess;
    }
}
