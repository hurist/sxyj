package com.ffcc66.sxyj.entity;

public class Book {

    private int cover;
    private String bookname;
    private String writer;
    private String introduction;
    private String collectionNum;
    private String rankingNum;
    private String readprocess;

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
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

    public String getReadprocess() {
        return readprocess;
    }

    public void setReadprocess(String readprocess) {
        this.readprocess = readprocess;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(String collectionNum) {
        this.collectionNum = collectionNum;
    }

    public String getRankingNum() {
        return rankingNum;
    }

    public void setRankingNum(String rankingNum) {
        this.rankingNum = rankingNum;
    }
}
