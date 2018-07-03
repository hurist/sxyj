package com.ffcc66.sxyj.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2015/12/27.
 */
public class BookList extends DataSupport implements Serializable {
    private int id;     //id，也是图书在书架中的位置
    private int bookid = 0;
    private String coverpath = "";
    private String bookname = "";    //图书名称
    private String writer = ""; //图书作者
    private long wordcount = 0;
    private String bookpath = "";    //文件存储位置
    private long begin = 0;         //开始位置
    private float readprocess = (float) 0;
    private long lastreadtime = 0;   //最后一次阅读的时间
    private String charset = "";     //文件编码
    private int type = 0;
    private String fileURL = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public String getCoverpath() {
        return coverpath;
    }

    public void setCoverpath(String coverpath) {
        this.coverpath = coverpath;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public long getWordcount() {
        return wordcount;
    }

    public void setWordcount(long wordcount) {
        this.wordcount = wordcount;
    }

    public String getBookpath() {
        return bookpath;
    }

    public void setBookpath(String bookpath) {
        this.bookpath = bookpath;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public float getReadprocess() {
        return readprocess;
    }

    public void setReadprocess(float readprocess) {
        this.readprocess = readprocess;
    }

    public long getLastreadtime() {
        return lastreadtime;
    }

    public void setLastreadtime(long lastreadtime) {
        this.lastreadtime = lastreadtime;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }
}
