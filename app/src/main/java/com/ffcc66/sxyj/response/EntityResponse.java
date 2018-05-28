package com.ffcc66.sxyj.response;

public class EntityResponse<T> extends Response {

    private static final long serialVersionUID = 3750508105973880680L;
    private T object;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

}
