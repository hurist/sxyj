package com.ffcc66.sxyj.response;

import java.io.Serializable;
import java.util.Date;

public class Response implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String code;
    //
    private String msg;

    private Long time = new Date().getTime();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the time
     */
    public Long getTime() {
        return time;
    }

    /**
     * @param time
     *            the time to set
     */
    public void setTime(Long time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setContent(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}